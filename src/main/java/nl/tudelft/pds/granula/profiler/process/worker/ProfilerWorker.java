package nl.tudelft.pds.granula.profiler.process.worker;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import nl.tudelft.pds.granula.profiler.process.ProcessInfo;

import java.util.Random;

public class ProfilerWorker {

    MasterInfo masterInfo;
    WorkerAssistant wAssistant;

    public ProfilerWorker() {
        masterInfo = new MasterInfo();
        masterInfo.setPath("akka.tcp://profiler-master@127.0.0.1:2554/user/profiler-master");
        masterInfo.setIp(ProcessInfo.Path2IpAddress(masterInfo.getPath()));

    }

    public void init() {

        Config config = ConfigFactory.load("profiler-worker");
        int port = (new Random()).nextInt(4000) + 8000;
        config = config.withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef(port));

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
}
