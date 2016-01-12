package nl.tudelft.pds.granula.profiler.util;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by wlngai on 12-1-16.
 * # http://stackoverflow.com/questions/434718/sockets-discover-port-availability-using-java
 */
public class PortChecker {

    // not working
    public static boolean isPortAvailable(int port) {
        Socket s = null;
        try {
            s = new Socket("localhost", port);

            // If the code makes it this far without an exception it means
            // something is using the port and has responded.
            return false;
        } catch (IOException e) {
            return true;
        } finally {
            if( s != null){
                try {
                    s.close();
                } catch (IOException e) {
                    throw new RuntimeException("You should handle this error." , e);
                }
            }
        }
    }
}
