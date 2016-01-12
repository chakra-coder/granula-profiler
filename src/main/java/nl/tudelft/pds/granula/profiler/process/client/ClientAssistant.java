package nl.tudelft.pds.granula.profiler.process.client;

import akka.actor.UntypedActor;
import nl.tudelft.pds.granula.profiler.comm.message.*;

/**
 * Created by wlngai on 1/9/16.
 */
public class ClientAssistant extends UntypedActor {

    ActorId actorId = ActorId.getRandomId();
    ProfilerClient profilerClient = null;

    public ClientAssistant(ProfilerClient profilerClient) {
        this.profilerClient = profilerClient;
        profilerClient.setClientAssistant(this);
    }

    public void sendHealthRequest() {
        String path = profilerClient.getMasterInfo().getPath();
        getContext().actorSelection(path).tell(new ClientHealthRequest(), getSelf());

    }

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof ClientHealthResponse) {
            ClientHealthResponse clientHealthResponse = (ClientHealthResponse) message;
            System.out.println(clientHealthResponse.getMessage());
        } else if (message instanceof ClientTerminateRESTfulResponse) {
            ClientTerminateRESTfulResponse clientTerminateRESTfulResponse = (ClientTerminateRESTfulResponse) message;
            System.out.println(clientTerminateRESTfulResponse.getMessage());
        } else {
            unhandled(message);
        }
    }

    public void sendTerminateWebApiRequest() {
        String path = profilerClient.getMasterInfo().getPath();
        getContext().actorSelection(path).tell(new ClientTerminateRESTFULRequest(), getSelf());
    }
}
