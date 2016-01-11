package nl.tudelft.pds.granula.profiler.comm.message;

/**
 * Created by wlngai on 1/9/16.
 */
public class DescriptionRequest implements Request {
    String message;

    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }
}
