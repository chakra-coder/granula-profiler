package nl.tudelft.pds.granula.profiler.backup;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class RestfulClientV2 {
    int port = 0;
    String path = null;


    public static void main(String[] args) throws Exception {
        RestfulClientV2 restfulClient = new RestfulClientV2();
        restfulClient.monitorProcess(5153, "cputime");
    }


    public RestfulClientV2() {
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


    public void monitorProcess(int processId, String metric) {

        try {
            HttpResponse<JsonNode> jsonResponse =
                    Unirest.get(path + "/monitor-process?" +  "processId=" + processId + "&" + "metric=" + metric).asJson();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
