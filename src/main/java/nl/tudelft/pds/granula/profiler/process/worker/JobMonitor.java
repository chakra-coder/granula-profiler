package nl.tudelft.pds.granula.profiler.process.worker;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import nl.tudelft.pds.granula.profiler.util.Execution;
import nl.tudelft.pds.granula.profiler.util.TimeUtility;
import scala.Int;

import java.util.*;

public class JobMonitor {

    String jobId;
    Map<Integer, ProcessMonitor> processMonitors;
    boolean isInit;

    public JobMonitor(String jobId) {
        this.jobId = jobId;
    }

    public static void main(String[] args) {


        Config config = ConfigFactory.load("profiler-worker");
        int port = config.getInt("akka.profiler.worker.port");
        config = config.withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef(port));
        ProfilerWorker.actorSystem = ActorSystem.create("profiler-worker", config);

//        int processId = 7630;
//        JobMonitor jobMonitor = new JobMonitor("job-x");
//        jobMonitor.init();
//        jobMonitor.start();
//        TimeUtility.Pause(1000);
//        jobMonitor.monitorProcess(processId);
//
//        jobMonitor.configure(processId, "process.cpu.time", 5000);
//        jobMonitor.configure(processId, "os.network.volume", 5000);

        JobMonitor jobMonitor = new JobMonitor("job-x");
        jobMonitor.init();
        jobMonitor.start();

        Set<Integer> processIds = new HashSet<>();
        while(true) {
            for (Integer processId : getProcesses()) {
                if(!processIds.contains(processId)) {
                    System.out.println(processId);
                    jobMonitor.monitorProcess(processId);
                    processIds.add(processId);
                }
            }

            TimeUtility.Pause(1000);
//            jobMonitor.configure(processId, "process.cpu.time", 5000);
//            jobMonitor.configure(processId, "os.network.volume", 5000);
        }

    }

    public static List<Integer> getProcesses() {
        List<Integer> pids = new ArrayList<>();
        String x = Execution.execute("ps", "-aux");
        for (String s : x.split("\n")) {
            if(s.contains("/usr/lib/firefox/firefox")) {
                int pId = Integer.parseInt(s.split("\\s+")[1]);
                pids.add(pId);


            }
        }
        return pids;
    }

    public void init() {
        if(!isInit) {
            processMonitors = new HashMap<>();
            isInit = true;
        }
    }

    public void start() {
        boolean isNormal = true;
        if(isNormal) {
            System.out.println(String.format("%s is starting its process monitors.", getName()));
            processMonitors.forEach((metric, pMonitor) -> pMonitor.start());
        } else {
            System.out.println(String.format("%s is detective, cannot start its process monitors.", getName()));
        }
    }

    public void configure(int processId, String metric, int interval) {
        ProcessMonitor processMonitor = getOrCreateProcessMonitor(processId);
        processMonitor.configure(metric, interval);
    }

    public void stop() {
        processMonitors.forEach((processId, processMonitor) -> processMonitor.stop());
    }

    public void kill() {
        processMonitors.forEach((processId, processMonitor) -> {
            processMonitor.kill();
            removeProcessMonitor(processId);
        });
    }

    private void removeProcessMonitor(int processId) {
        processMonitors.remove(processId);
    }

    private ProcessMonitor getOrCreateProcessMonitor(int processId) {
        if(processMonitors.containsKey(processId)) {
            return processMonitors.get(processId);
        } else {
            ProfilerWorker.actorSystem.actorOf(Props.create(ProcessMonitor.class, processId, this),
                    processId + "-monitor");
            //TODO too ugly
            while(!processMonitors.containsKey(processId)) {
                TimeUtility.Pause(10);
            }
            ProcessMonitor processMonitor = processMonitors.get(processId);
            processMonitor.init();
            return processMonitor;
        }
    }

    public ProcessMonitor registerProcessMonitor(int processId, ProcessMonitor processMonitor) {
        processMonitors.put(processId, processMonitor);
        return processMonitor;
    }

    public void monitorProcess(int processId) {
        ProcessMonitor processMonitor = getOrCreateProcessMonitor(processId);
        processMonitor.start();
    }

    public void configureProcessMonitor(int processId, String metric, int interval) {
        ProcessMonitor processMonitor = getOrCreateProcessMonitor(processId);
        processMonitor.configure(metric, interval);
    }

    public boolean isInit() {
        return isInit;
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

}
