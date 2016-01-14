package nl.tudelft.pds.granula.profiler.process.worker;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicLong;

import akka.actor.Cancellable;
import akka.actor.ReceiveTimeout;
import akka.actor.UntypedActor;
import nl.tudelft.pds.granula.profiler.process.worker.comm.CollectCollectorRequest;
import nl.tudelft.pds.granula.profiler.process.worker.comm.StartCollectorRequest;
import nl.tudelft.pds.granula.profiler.process.worker.comm.StopCollectorRequest;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.IOUtils;
import scala.concurrent.duration.Duration;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public abstract class SystemMetricCollector extends UntypedActor {

    boolean isRunning = false;

    int processId;
    int interval;
    AtomicLong stopTime;
    RandomAccessFile reader;
    Cancellable collectAction;

    public void start(int processId, int interval, long duration) {
        this.processId = processId;
        this.interval = interval;
        this.stopTime = new AtomicLong(System.currentTimeMillis() + duration);
        try {
            reader = new RandomAccessFile(decidePath(), "r");
            isRunning = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        collectAction = getContext().system().scheduler().schedule(
                Duration.Zero(), Duration.create(interval, MILLISECONDS), getSelf(),
                new CollectCollectorRequest(), getContext().dispatcher(), getSelf());
    }

    public abstract String decidePath();

    public void stop() {
        collectAction.cancel();
        stopTime.getAndSet(Long.MIN_VALUE);
        IOUtils.closeQuietly(reader);
        isRunning = false;
    }

    public abstract void collectOnce() throws IOException;

    public void collect() {
        System.out.println("reached"+ System.currentTimeMillis());
        try {
            if(!(System.currentTimeMillis() > stopTime.get())) {
                reader.seek(0);
                collectOnce();
            } else {
                collectAction.cancel();
            }

        } catch (IOException e) {
            isRunning = false;
            e.printStackTrace();
        }
    }

    public static String collectFromExecution(String program, String parameter) {

        String output = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DefaultExecutor executor = new DefaultExecutor();
            executor.setStreamHandler(new PumpStreamHandler(baos));
            executor.setExitValues(null);

            CommandLine commandLine = new CommandLine(program);
            commandLine.addArgument(parameter, false);
            executor.execute(commandLine);
            output = baos.toString().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof StartCollectorRequest) {
            StartCollectorRequest startCollectorRequest = (StartCollectorRequest) message;
            if(isRunning) {
                stopTime.set(startCollectorRequest.getDuration() + System.currentTimeMillis());
                interval = startCollectorRequest.getInterval();
                collectAction.cancel();
                collectAction = getContext().system().scheduler().schedule(
                        Duration.Zero(), Duration.create(interval, MILLISECONDS), getSelf(),
                        new CollectCollectorRequest(), getContext().dispatcher(), getSelf());
            } else {
                start(startCollectorRequest.getProcessId(), startCollectorRequest.getInterval(), startCollectorRequest.getDuration());
            }

        } else if (message instanceof StopCollectorRequest) {
            StopCollectorRequest stopCollectorRequest = (StopCollectorRequest) message;
            stop();
            getContext().stop(getSelf());
        } else if (message instanceof CollectCollectorRequest) {
            CollectCollectorRequest collectCollectorRequest = (CollectCollectorRequest) message;
            collect();
        } else {
            unhandled(message);
        }
    }


    private float readUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" +");  // Split on one or more spaces

            long idle1 = Long.parseLong(toks[4]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {}

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" +");

            long idle2 = Long.parseLong(toks[4]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    public boolean isRunning() {
        return isRunning;
    }
}

