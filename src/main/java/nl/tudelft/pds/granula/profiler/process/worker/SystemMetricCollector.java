package nl.tudelft.pds.granula.profiler.process.worker;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

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
            isRunning = false;
        } catch (FileNotFoundException e) {
            System.out.println(String.format("Cannot read file %s. Check if process with id %s exists", decidePath(), processId));
            malFunction();
        }
    }

    public void start() {
        System.out.println(String.format("%s is starting its collection routine", getName()));
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

    public void routine() {
        try {
                reader.seek(0);
                collectOnce();
        } catch (IOException | NullPointerException e) {
            System.out.println(String.format("%s cannot execute its collection routine due to %s",
                    getName(), e.getClass().getSimpleName()));
            malFunction();
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

    private void configure(int interval) {
        System.out.println(String.format("%s configured its collection routine to execute at interval %s",
                getName(), interval));
        this.interval = interval;
        if(isRunning) {
            restartRoutine();
        }
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof InitCollectorRequest) {
            InitCollectorRequest initCollectorRequest = (InitCollectorRequest) message;
            init(initCollectorRequest.getProcessId());
        } else if(message instanceof StartCollectorRequest) {
            start();
        } else if (message instanceof ConfigureCollectorRequest) {
            ConfigureCollectorRequest configureCollectorRequest = (ConfigureCollectorRequest) message;
            configure(configureCollectorRequest.getInterval());
        } else if (message instanceof StopCollectorRequest) {
            StopCollectorRequest stopCollectorRequest = (StopCollectorRequest) message;
            stop();
        } else if (message instanceof KillCollectorRequest) {
            KillCollectorRequest killCollectorRequest = (KillCollectorRequest) message;
            kill();
        } else if (message instanceof RoutineCollectorRequest) {
            RoutineCollectorRequest routineCollectorRequest = (RoutineCollectorRequest) message;
            routine();
        }  else {
            unhandled(message);
        }
    }

    private void stopRoutine() {
        System.out.println(String.format("%s stopped its collection routine.", getName()));
        if(collectAction != null) {
            collectAction.cancel();
        }
    }

    private void startRoutine() {
        if(collectAction != null) {
            collectAction.cancel();
        }
        collectAction = getContext().system().scheduler().schedule(
                Duration.Zero(), Duration.create(interval, MILLISECONDS), getSelf(),
                new RoutineCollectorRequest(), getContext().dispatcher(), getSelf());
    }

    private void restartRoutine() {
        stopRoutine();
        startRoutine();
    }

    private void malFunction() {
        System.out.println(String.format("%s reported to be malfunctioning.", getName()));
        stop();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }
}

