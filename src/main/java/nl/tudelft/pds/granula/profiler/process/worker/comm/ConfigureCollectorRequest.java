package nl.tudelft.pds.granula.profiler.process.worker.comm;

import nl.tudelft.pds.granula.profiler.comm.message.Request;

/**
 * Created by wlngai on 1/9/16.
 */
public class ConfigureCollectorRequest implements Request {

    int processId, interval;

    public ConfigureCollectorRequest() {
    }

    public ConfigureCollectorRequest(int processId, int interval) {
        this.processId = processId;
        this.interval = interval;
    }

    public int getProcessId() {
        return processId;
    }

    public int getInterval() {
        return interval;
    }


    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
