package nl.tudelft.pds.granula.profiler.process.worker;

import akka.actor.UntypedActor;

import java.util.HashMap;
import java.util.Map;

public class JobMonitor {

    String jobId;
    Map<Integer, ProcessMonitor> processMonitors;
    boolean isInit;

    public JobMonitor(String jobId) {
        this.jobId = jobId;
    }

    public static void main(String[] args) {
        JobMonitor jobMonitor = new JobMonitor("job-x");
        jobMonitor.start();
        jobMonitor.monitor(2974, "process.cpu.time", 1000, 10000);
        jobMonitor.monitor(2974, "os.network.volume", 1000, 10000);
    }

    public void init() {
        if(!isInit) {
            processMonitors = new HashMap<>();
            isInit = true;
        }
    }

    public void start() {

    }

    public void monitor(int processId, String metric, int interval, int duration) {
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
            ProcessMonitor processMonitor = new ProcessMonitor(processId);
            processMonitor.start();
            processMonitors.put(processId, processMonitor);
            return processMonitor;
        }
    }

    public void initProcessMonitor(int processId) {
        ProcessMonitor processMonitor = getOrCreateProcessMonitor(processId);
        processMonitor.init();
    }

    public void configureProcessMonitor(int processId, String metric, int interval) {
        ProcessMonitor processMonitor = getOrCreateProcessMonitor(processId);
        processMonitor.configure(metric, interval);
    }

    public boolean isInit() {
        return isInit;
    }
}
