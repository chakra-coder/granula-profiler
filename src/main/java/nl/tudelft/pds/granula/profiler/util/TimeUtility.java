package nl.tudelft.pds.granula.profiler.util;

/**
 * Created by wlngai on 1/14/16.
 */
public class TimeUtility {

    public static void Pause(int ms) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
