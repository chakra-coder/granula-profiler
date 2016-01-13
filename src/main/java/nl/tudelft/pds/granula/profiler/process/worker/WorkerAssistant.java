package nl.tudelft.pds.granula.profiler.process.worker;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import nl.tudelft.pds.granula.profiler.process.master.WorkerInfo;
import nl.tudelft.pds.granula.profiler.comm.message.*;
import nl.tudelft.pds.granula.profiler.comm.message.ActorId;
import ro.pippo.core.Pippo;

/**
 * Created by wlngai on 1/9/16.
 */
public class WorkerAssistant extends UntypedActor {

    ActorId actorId = ActorId.getRandomId();
    ProfilerWorker profilerWorker = null;
    Pippo pippo;

    public WorkerAssistant(ProfilerWorker profilerWorker) {
        this.profilerWorker = profilerWorker;
        profilerWorker.setWorkerAssistant(this);
        sendRegisterRequest();
        startWebApi();
    }

    public void startWebApi() {
        final WorkerAssistant workerAssistant = this;
        final int port = ConfigFactory.load("profiler-worker").getInt("akka.profiler.worker.web.port");

        (new Thread() {
            public void run() {
                System.out.println("Started worker web at port "+ port);
                pippo = new Pippo(new RestfulWorkerAssistant(workerAssistant));
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


    private void sendRegisterRequest() {

        String path = profilerWorker.getMasterInfo().getPath();
        RegisterRequest registerRequest = new RegisterRequest(actorId, getSelf().path().toString());
        getContext().actorSelection(path).tell(registerRequest, getSelf());

    }

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof HealthRequest) {
            HealthResponse healthResponse = new HealthResponse(actorId);
            healthResponse.setStatus("I'm high.");
            getSender().tell(healthResponse, getSelf());
        } else {
            unhandled(message);
        }
    }

    public void verifyWorkerHealth(WorkerInfo workerInfo) {

    }

    public ProfilerWorker getProfilerWorker() {
        return profilerWorker;
    }

    public void setProfilerWorker(ProfilerWorker profilerWorker) {
        this.profilerWorker = profilerWorker;
    }

    public void printSomething(int id, String metrics) {
        System.out.println("execute this."+ id + metrics);
    }

    public void monitor(String jobId, int processId, String metric, int interval, int duration) {
        profilerWorker.monitor(jobId, processId, metric, interval, duration);
    }
}
