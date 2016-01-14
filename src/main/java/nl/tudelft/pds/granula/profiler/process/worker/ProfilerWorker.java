package nl.tudelft.pds.granula.profiler.process.worker;


import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import nl.tudelft.pds.granula.profiler.process.ProcessInfo;

import java.util.HashMap;
import java.util.Map;

public class ProfilerWorker {

    public static ActorSystem actorSystem;
    MasterInfo masterInfo;
    WorkerAssistant wAssistant;
    Map<String, JobMonitor> jobMonitors;

    public ProfilerWorker() {
        masterInfo = new MasterInfo();
    }

    public void init() {

        Config config = ConfigFactory.load("profiler-worker");
        int port = config.getInt("akka.profiler.worker.port");
//        port = (new Random()).nextInt(4000)+4000;
        config = config.withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef(port));

        int masterPort = config.getInt("akka.profiler.master.port");
        String masterIp = config.getString("akka.profiler.master.ip");
        masterInfo.setPath(String.format("akka.tcp://profiler-master@%s:%s/user/profiler-master", masterIp, masterPort));
        masterInfo.setIp(ProcessInfo.Path2IpAddress(masterInfo.getPath()));

        actorSystem = ActorSystem.create("profiler-worker", config);
        actorSystem.actorOf(Props.create(WorkerAssistant.class, this), "profiler-worker");

        jobMonitors = new HashMap<>();

    }

    public void setWorkerAssistant(WorkerAssistant wAssistant) {
        this.wAssistant = wAssistant;
    }

    public MasterInfo getMasterInfo() {
        return masterInfo;
    }

    public void setMasterInfo(MasterInfo masterInfo) {
        this.masterInfo = masterInfo;
    }

    private JobMonitor getOrCreateJobMonitor(String jobId) {
        if(jobMonitors.containsKey(jobId)) {
            return jobMonitors.get(jobId);
        } else {
            JobMonitor jobMonitor = new JobMonitor(jobId);
            jobMonitor.start();
            jobMonitors.put(jobId, jobMonitor);
            return jobMonitor;
        }
    }

    public void monitor(String jobId, int processId, String metric, int interval, int duration) {
        JobMonitor jobMonitor = getOrCreateJobMonitor(jobId);
        jobMonitor.monitor(processId, metric, interval, duration);
    }
}
