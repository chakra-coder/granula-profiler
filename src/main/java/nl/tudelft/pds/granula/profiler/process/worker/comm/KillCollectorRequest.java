package nl.tudelft.pds.granula.profiler.process.worker.comm;

import nl.tudelft.pds.granula.profiler.comm.message.Request;

/**
 * Created by wlngai on 1/9/16.
 */
public class KillCollectorRequest implements Request {

    public KillCollectorRequest() {
    }

    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
