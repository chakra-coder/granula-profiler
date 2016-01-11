package nl.tudelft.pds.granula.profiler;

import ro.pippo.core.Application;
import ro.pippo.core.Pippo;

import java.io.File;

/**
 * Created by wlngai on 1/10/16.
 */
public class RestfulApp extends Application {

    @Override
    protected void onInit() {
        // send 'Hello World' as response
        GET("/", (routeContext) -> routeContext.send("Hello World"));

        // send a file as response
        GET("/file", (routeContext) -> routeContext.send(new File("pom.xml")));

        // send a json as response
        GET("/json", (routeContext) -> {
            Contact contact = Contact.createContact();
            int id = routeContext.getParameter("id").toInt();
            String name = routeContext.getParameter("name").toString();
            contact.setId(id);
            contact.setName(name);
            routeContext.json().send(contact);
        });

        // send xml as response
        GET("/xml", (routeContext) -> {
            Contact contact = Contact.createContact();
            routeContext.xml().send(contact);
        });

//        // send an object and negotiate the Response content-type, default to XML
//        GET("/negotiate", (routeContext) -> {
//            routeContext.xml().negotiateContentType().send(contact);
//        });

        // send a template as response
        GET("/template", (routeContext) -> {
            routeContext.setLocal("greeting", "Hello");
            routeContext.render("hello");
        });
    }




    public static void main(String[] args) {
        Pippo pippo = new Pippo(new RestfulApp());
        pippo.getApplication().GET("/", (routeContext) -> routeContext.send("Hello World!"));
        pippo.start();
    }
}
