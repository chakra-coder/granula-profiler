package nl.tudelft.pds.granula.profiler.process.worker.comm;

import nl.tudelft.pds.granula.profiler.comm.message.Request;

/**
 * Created by wlngai on 1/9/16.
 */
public class InitCollectorRequest implements Request {

    int processId;

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public InitCollectorRequest(int processId) {
        this.processId = processId;
    }

    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
