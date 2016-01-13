package nl.tudelft.pds.granula.profiler.process.worker;

import akka.actor.UntypedActor;

import java.util.HashMap;
import java.util.Map;

public class JobMonitor {

    Map<Integer, ProcessMonitor> processMonitors;
    String jobId;

    public JobMonitor(String jobId) {
        this.jobId = jobId;
    }

    public static void main(String[] args) {
        JobMonitor jobMonitor = new JobMonitor("job-x");
        jobMonitor.start();
        jobMonitor.monitor(2974, "process.cpu.time", 1000, 10000);
        jobMonitor.monitor(2974, "os.network.volume", 1000, 10000);
    }

    public void start() {
        processMonitors = new HashMap<>();
    }

    public void monitor(int processId, String metric, int interval, int duration) {
        ProcessMonitor processMonitor = getOrCreateProcessMonitor(processId);
        processMonitor.monitor(metric, interval, duration);
    }

    public void stop() {
        processMonitors.forEach( (processId, processMonitor) -> processMonitor.stop() );
    }

    private ProcessMonitor getOrCreateProcessMonitor(int processId) {
        if(processMonitors.containsKey(processId)) {
            return processMonitors.get(processId);
        } else {
            ProcessMonitor processMonitor = new ProcessMonitor(processId);
            processMonitor.start();
            processMonitors.put(processId, processMonitor);
            return processMonitor;
        }
    }
}
