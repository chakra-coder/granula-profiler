package nl.tudelft.pds.granula.profiler.process.master;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.typesafe.config.ConfigFactory;
import nl.tudelft.pds.granula.profiler.backup.RestfulApp;
import nl.tudelft.pds.granula.profiler.comm.message.*;
import nl.tudelft.pds.granula.profiler.process.worker.RestfulWorkerAssistant;
import ro.pippo.core.Pippo;

/**
 * Created by wlngai on 1/9/16.
 */
public class MasterAssistant extends UntypedActor {

    ProfilerMaster profilerMaster;
    Pippo pippo;

    public MasterAssistant(ProfilerMaster profilerMaster) {
        this.profilerMaster = profilerMaster;
        profilerMaster.setMasterAssistant(this);
        startWebApi();
    }

    public void startWebApi() {
        final MasterAssistant masterAssistant = this;
        final int port = ConfigFactory.load("profiler-worker").getInt("akka.profiler.master.web.port");

        (new Thread() {
            public void run() {
                System.out.println("Started worker web at port "+ port);
                pippo = new Pippo(new RestfulMasterAssistant(masterAssistant));
                pippo.getServer().getSettings().port(port);
                pippo.start();

            }
        }).start();

    }


    public void stopWebApi(ClientTerminateRESTFULRequest clientTerminateRESTFULRequest, ActorRef actorRef) {
        pippo.stop();
        ClientTerminateRESTfulResponse clientTerminateRESTfulResponse = new ClientTerminateRESTfulResponse();
        clientTerminateRESTfulResponse.setMessage("Stopped Web API.");
        actorRef.tell(clientTerminateRESTfulResponse, getSelf());
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
        } else if (message instanceof ClientTerminateRESTFULRequest) {
            ClientTerminateRESTFULRequest clientTerminateRESTFULRequest = (ClientTerminateRESTFULRequest) message;
            stopWebApi(clientTerminateRESTFULRequest, getSender());
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

    public void monitor(int processId, String metric) {
        //TODO fake method
        System.out.println("hi, master monitor stuff");
    }
}
