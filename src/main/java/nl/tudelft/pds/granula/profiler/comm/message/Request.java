package nl.tudelft.pds.granula.profiler.comm.message;


import java.io.Serializable;

/**
 * Created by wlngai on 1/9/16.
 */
public interface Request extends Serializable {

    String getMessage();
    void setMessage(String message);
}
