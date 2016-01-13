package nl.tudelft.pds.granula.profiler.process.worker.comm;

import nl.tudelft.pds.granula.profiler.comm.message.Request;

/**
 * Created by wlngai on 1/9/16.
 */
public class StartCollectorRequest implements Request {

    int processId, interval, duration;

    public StartCollectorRequest() {
    }

    public StartCollectorRequest(int processId, int interval, int duration) {
        this.processId = processId;
        this.interval = interval;
        this.duration = duration;
    }

    public int getProcessId() {
        return processId;
    }

    public int getInterval() {
        return interval;
    }

    public int getDuration() {
        return duration;
    }

    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
