package nl.tudelft.pds.granula.profiler.comm.message;

/**
 * Created by wlngai on 1/9/16.
 */
public class HealthResponse implements Response {
    ActorId actorId;
    String status;

    public HealthResponse(ActorId actorId) {
        this.actorId = actorId;
    }

    public ActorId getActorId() {
        return actorId;
    }

    public void setActorId(ActorId actorId) {
        this.actorId = actorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
