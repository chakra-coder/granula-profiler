package nl.tudelft.pds.granula.profiler.process.master;

import nl.tudelft.pds.granula.profiler.Contact;
import nl.tudelft.pds.granula.profiler.process.worker.RestfulResponse;
import nl.tudelft.pds.granula.profiler.process.worker.WorkerAssistant;
import ro.pippo.core.Application;

/**
 * Created by wlngai on 1/10/16.
 */
public class RestfulMasterAssistant extends Application {

    MasterAssistant masterAssistant;

    public RestfulMasterAssistant(MasterAssistant masterAssistant) {
        this.masterAssistant = masterAssistant;
    }

    @Override
    protected void onInit() {
        // send 'Hello World' as response
        GET("/", (routeContext) -> {
            routeContext.send("Hello World");
            System.out.println("hello");
        });

        GET("/monitor-process", (routeContext) -> {
            int processId = routeContext.getParameter("processId").toInt();
            String metric = routeContext.getParameter("metric").toString();
            RestfulResponse restfulResponse = new RestfulResponse("monitoring process");
            Contact contact = Contact.createContact();
            masterAssistant.monitor(processId, metric);
            routeContext.json().send(contact);
        });
    }
}
