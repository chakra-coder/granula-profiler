package nl.tudelft.pds.granula.profiler.process.client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.function.Function;

public class RestfulClient {
    int port = 0;
    String path = null;


    public static void main(String[] args) throws Exception {
        RestfulClient restfulClient = new RestfulClient();
        restfulClient.repeat(1000);
    }


    public RestfulClient() {
        Config config = ConfigFactory.load("profiler-client");
        port = config.getInt("akka.profiler.worker.web.port");
        if(port == 0) {
            throw new IllegalArgumentException("worker port is not found");
        }
        path = String.format("http://localhost:%s", port);
        System.out.println(path);
    }

    public void repeat(int rep) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < rep; i++) {
            monitorProcess(i, "cpu");
            System.out.println("hi");
        }
        long endTime = System.currentTimeMillis();
        System.out.printf(" runs for " + (endTime - startTime) * 1.0 / rep + "ms" + "(avg in " + rep + " rounds)." );
    }

    public void monitorProcess(int processId, String metric) {

        try {
            HttpResponse<JsonNode> jsonResponse =
                    Unirest.get(path + "/monitor-process?" +  "processId=" + processId + "&" + "metric=" + metric).asJson();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
