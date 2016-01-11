package nl.tudelft.pds.granula.profiler.process.master;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import nl.tudelft.pds.granula.profiler.comm.message.*;

/**
 * Created by wlngai on 1/9/16.
 */
public class MasterAssistant extends UntypedActor {

    ProfilerMaster profilerMaster;

    public MasterAssistant(ProfilerMaster profilerMaster) {
        this.profilerMaster = profilerMaster;
        profilerMaster.setMasterAssistant(this);
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof HealthResponse) {
            HealthResponse healthResponse = (HealthResponse) message;
            profilerMaster.reportWorkerHealth(healthResponse);
        } else if (message instanceof ClientHealthRequest) {
            ClientHealthRequest clientHealthRequest = (ClientHealthRequest) message;
            profilerMaster.verifyWorkerHealth(getSender());
        } else if (message instanceof RegisterRequest) {
            RegisterRequest registerRequest = (RegisterRequest) message;

            profilerMaster.registerWorker(registerRequest, getSender());
        } else {
            unhandled(message);
        }
    }

    public void verifyWorkerHealth(WorkerInfo workerInfo) {
        workerInfo.getActorRef().tell(new HealthRequest(), getSelf());
    }

    public void reportWorkerHealth(ActorRef actorRef, String report) {
        ClientHealthResponse clientHealthResponse = new ClientHealthResponse();
        clientHealthResponse.setMessage(report);
        System.out.println("send" + clientHealthResponse.getMessage());
        actorRef.tell(clientHealthResponse, getSelf());
    }

}
