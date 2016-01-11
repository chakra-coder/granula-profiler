package nl.tudelft.pds.granula.profiler;

import ro.pippo.core.Pippo;

/**
 * Created by wlngai on 1/10/16.
 */
public class Pippoapp {
    public static void main(String[] args) {
        Pippo pippo = new Pippo();
        pippo.getApplication().GET("/", (routeContext) -> routeContext.send("Hello World!"));
        pippo.start();
    }
}
