package nl.tudelft.pds.granula.profiler.process.worker;

import akka.actor.UntypedActor;
import nl.tudelft.pds.granula.profiler.process.master.WorkerInfo;
import nl.tudelft.pds.granula.profiler.comm.message.*;
import nl.tudelft.pds.granula.profiler.comm.message.ActorId;

/**
 * Created by wlngai on 1/9/16.
 */
public class WorkerAssistant extends UntypedActor {

    ActorId actorId = ActorId.getRandomId();
    ProfilerWorker profilerWorker = null;

    public WorkerAssistant(ProfilerWorker profilerWorker) {
        this.profilerWorker = profilerWorker;
        profilerWorker.setWorkerAssistant(this);
        sendRegisterRequest();
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
}
