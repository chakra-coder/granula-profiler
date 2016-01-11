package nl.tudelft.pds.granula.profiler.process.master;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import nl.tudelft.pds.granula.profiler.comm.message.HealthResponse;
import nl.tudelft.pds.granula.profiler.comm.message.RegisterRequest;
import nl.tudelft.pds.granula.profiler.comm.message.ActorId;
import nl.tudelft.pds.granula.profiler.process.ProcessInfo;

import java.util.*;


public class ProfilerMaster {
    Map<ActorId, WorkerInfo> workers;
    MasterAssistant mAssistant;
    List<ActorRef> clients = new ArrayList<>();

    public ProfilerMaster() {

    }

    public void init() {
        workers = new HashMap<>();

        Config config = ConfigFactory.load("profiler-master");
        int port = config.getInt("akka.profiler.master.port");
        String ip = config.getString("akka.profiler.master.ip");
        config = config.withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef(port));
        config = config.withValue("akka.remote.netty.tcp.hostname", ConfigValueFactory.fromAnyRef(ip));
        final ActorSystem system = ActorSystem.create("profiler-master",config);
        final ActorRef master = system.actorOf(Props.create(MasterAssistant.class, this), "profiler-master");

        System.out.println("Started Profiler Master");

    }

    public void registerWorker(RegisterRequest registerRequest, ActorRef actorRef) {
        WorkerInfo workerInfo = new WorkerInfo();
        workerInfo.setPath(actorRef.path().toString());
        workerInfo.setId(registerRequest.getWorkerId());
        workerInfo.setActorRef(actorRef);
        workerInfo.setIp(ProcessInfo.Path2IpAddress(workerInfo.getPath()));
        workerInfo.setStatus(ProcessInfo.Status.LIVE);
        workers.put(workerInfo.getId(), workerInfo);
        System.out.println("Register: " + workerInfo.toString());
    }

    public void deregisterWorker() {

    }

    public void getWorkersDescription() {

    }

    public void verifyWorkerHealth(ActorRef user) {
        clients.add(user);
        for (WorkerInfo info : workers.values()) {
            info.setStatus(ProcessInfo.Status.UNKNOWN);
            mAssistant.verifyWorkerHealth(info);
        }

    }

    public void verifyWorkerHealth() {
        for (WorkerInfo info : workers.values()) {
            info.setStatus(ProcessInfo.Status.UNKNOWN);
            mAssistant.verifyWorkerHealth(info);
        }

    }

    public void reportWorkerHealth(HealthResponse healthResponse) {
        WorkerInfo workerInfo = workers.get(healthResponse.getActorId());
        workerInfo.setStatus(ProcessInfo.Status.LIVE);
        System.out.println(
                String.format("Master got a health reponse from %s, who says: \"%s\"",
                        workerInfo, healthResponse.getStatus()));
        int liveCount = 0, deadCount = 0, lostCount = 0, unknownCount = 0;
        for (WorkerInfo info : workers.values()) {
            switch (info.getStatus()) {
                case LIVE:
                    liveCount++;
                    break;
                case DEAD:
                    deadCount++;
                    break;
                case LOST:
                    lostCount++;
                    break;
                case UNKNOWN:
                    unknownCount++;
                    break;
                default:
                    break;
            }
        }
        String healthReport = String.format("Worker status: LIVE(%s), DEAD(%s), LOST(%s), UNKNOWN(%s)",
                liveCount, deadCount, lostCount, unknownCount);
        System.out.println(healthReport);

        mAssistant.reportWorkerHealth(clients.get(0), healthReport);

        if(unknownCount == 0) {
            clients.clear();
        }

    }

    public void benchmarkEnvironment() {

    }

    public void setMasterAssistant(MasterAssistant mAssistant) {
        this.mAssistant = mAssistant;
    }
}
