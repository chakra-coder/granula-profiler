package nl.tudelft.pds.granula.profiler.process.worker;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import nl.tudelft.pds.granula.profiler.process.ProcessInfo;
import nl.tudelft.pds.granula.profiler.util.PortChecker;

import java.net.BindException;
import java.util.Random;

public class ProfilerWorker {

    MasterInfo masterInfo;
    WorkerAssistant wAssistant;

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

        final ActorSystem system = ActorSystem.create("profiler-worker", config);
        system.actorOf(Props.create(WorkerAssistant.class, this), "profiler-worker");

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

    public void monitor(int processId, String metric) {
        Monitor.monitor(processId, metric);
    }
}
