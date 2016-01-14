package nl.tudelft.pds.granula.profiler.backup;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class RestfulClientV3 {
    int port = 0;
    String path = null;


    public static void main(String[] args) throws Exception {
        RestfulClientV3 restfulClient = new RestfulClientV3();
        restfulClient.monitorProcess("random_job_1", 2974, "process.cpu.time", 1000, 10000);
        restfulClient.monitorProcess("random_job_1", 2974, "os.network.volume", 1000, 10000);

        Thread.sleep(5000);
        restfulClient.monitorProcess("random_job_1", 2974, "process.cpu.time", 100, 10000);
        restfulClient.monitorProcess("random_job_1", 2974, "os.network.volume", 100, 10000);
        Thread.sleep(5000);
        restfulClient.monitorProcess("random_job_1", 2974, "process.cpu.time", 1000, 10000);
        restfulClient.monitorProcess("random_job_1", 2974, "os.network.volume", 1000, 10000);
    }


    public RestfulClientV3() {
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


    public void monitorProcess(String jobId, int processId, String metric, int interval, int duration) {

        try {
            HttpResponse<JsonNode> jsonResponse =
                    Unirest.get(path + "/monitor-process?"
                            + "jobId=" + jobId
                            + "&" + "processId=" + processId
                            + "&" + "metric=" + metric
                            + "&" + "interval=" + interval
                            + "&" + "duration=" + duration
                    ).asJson();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
