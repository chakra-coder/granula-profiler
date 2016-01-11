package nl.tudelft.pds.granula.profiler.comm.message;

/**
 * Created by wlngai on 1/9/16.
 */
public class RegisterRequest implements Request {
    String message;
    ActorId workerId;
    String workerPath;

    public RegisterRequest(ActorId workerId, String workerPath) {
        this.workerId = workerId;
        this.workerPath = workerPath;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ActorId getWorkerId() {
        return workerId;
    }

    public void setWorkerId(ActorId workerId) {
        this.workerId = workerId;
    }

    public String getWorkerPath() {
        return workerPath;
    }

    public void setWorkerPath(String workerPath) {
        this.workerPath = workerPath;
    }
}
