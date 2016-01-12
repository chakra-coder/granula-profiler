package nl.tudelft.pds.granula.profiler.backup;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class RestfulClient {
    int port = 0;
    String path = null;


    public static void main(String[] args) throws Exception {
        RestfulClient restfulClient = new RestfulClient();
        restfulClient.repeat(1);
    }


    public RestfulClient() {
        String masterWebPort = "akka.profiler.master.web.port";
        String workerWebPort = "akka.profiler.worker.web.port";
        Config config = ConfigFactory.load("common");
        port = config.getInt(workerWebPort);
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
