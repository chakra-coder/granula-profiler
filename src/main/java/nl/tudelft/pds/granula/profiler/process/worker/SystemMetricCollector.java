package nl.tudelft.pds.granula.profiler.process.worker;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicLong;

import akka.actor.Cancellable;
import akka.actor.UntypedActor;
import nl.tudelft.pds.granula.profiler.process.worker.comm.*;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.IOUtils;
import scala.concurrent.duration.Duration;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public abstract class SystemMetricCollector extends UntypedActor {

    boolean isRunning = false;

    int processId;
    int interval;
    RandomAccessFile reader;
    Cancellable collectAction;

    public void init(int processId) {
        this.processId = processId;
        try {
            reader = new RandomAccessFile(decidePath(), "r");
            isRunning = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        isRunning = true;
        startRoutine();
    }

    public abstract String decidePath();

    public void stop() {
        stopRoutine();
        isRunning = false;
    }

    public void kill() {
        IOUtils.closeQuietly(reader);
        getContext().stop(getSelf());
    }

    public abstract void collectOnce() throws IOException;

    public void collect() {
        System.out.println("reached"+ System.currentTimeMillis());
        try {
                reader.seek(0);
                collectOnce();
        } catch (IOException e) {
            stopRoutine();
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

        if (message instanceof ConfigureCollectorRequest) {
            ConfigureCollectorRequest configureCollectorRequest = (ConfigureCollectorRequest) message;
            interval = configureCollectorRequest.getInterval();
            if(isRunning) {
                stopRoutine();
                startRoutine();
            }
        } else if (message instanceof KillCollectorRequest) {
            KillCollectorRequest killCollectorRequest = (KillCollectorRequest) message;
            kill();
        } else if (message instanceof StopCollectorRequest) {
            StopCollectorRequest stopCollectorRequest = (StopCollectorRequest) message;
            stop();
        } else if(message instanceof StartCollectorRequest) {
            start();
        } else if (message instanceof CollectCollectorRequest) {
            CollectCollectorRequest collectCollectorRequest = (CollectCollectorRequest) message;
            collect();
        } else if (message instanceof InitCollectorRequest) {
            InitCollectorRequest initCollectorRequest = (InitCollectorRequest) message;
            init(initCollectorRequest.getProcessId());
        } else {
            unhandled(message);
        }
    }

    public void stopRoutine() {
        if(collectAction != null) {
            collectAction.cancel();
        }
    }

    private void startRoutine() {
        stopRoutine();
        collectAction = getContext().system().scheduler().schedule(
                Duration.Zero(), Duration.create(interval, MILLISECONDS), getSelf(),
                new CollectCollectorRequest(), getContext().dispatcher(), getSelf());
    }

    public boolean isRunning() {
        return isRunning;
    }
}

