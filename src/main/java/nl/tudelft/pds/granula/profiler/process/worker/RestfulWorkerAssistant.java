package nl.tudelft.pds.granula.profiler.process.worker;

import nl.tudelft.pds.granula.profiler.Contact;
import ro.pippo.core.Application;
import ro.pippo.core.Pippo;

import java.io.File;

/**
 * Created by wlngai on 1/10/16.
 */
public class RestfulWorkerAssistant extends Application {

    WorkerAssistant workerAssistant;

    public RestfulWorkerAssistant(WorkerAssistant workerAssistant) {
        this.workerAssistant = workerAssistant;
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
            workerAssistant.monitor(processId, metric);
            routeContext.json().send(contact);
        });
    }
}
