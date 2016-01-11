package nl.tudelft.pds.granula.profiler.process;

import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class RestfulClient {
    public static void main(String[] args) throws Exception {
        something(0);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
//            header(i);
        }
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }


    public static void header(int i) throws Exception{
        HttpResponse<JsonNode> jsonResponse = Unirest.get("http://localhost:8338/json?id=" + i)
                .queryString("name", "Mark").asJson();
        System.out.println(jsonResponse.getHeaders());
//        System.out.println(jsonResponse.getBody().toString());
    }

    public static void something2(int i) throws Exception{
        HttpResponse<JsonNode> jsonResponse = Unirest.get("http://localhost:8338/json?id=" + i)
                .queryString("name", "Mark")
                .asJson();
//        System.out.println(jsonResponse.getBody().toString());
    }

    public static void something(int i) throws Exception{
        Future<HttpResponse<JsonNode>> jsonResponse = Unirest.get("http://localhost:8338/json?id=" + i)
                .queryString("name", "Mark")
                .asJsonAsync(new Callback<JsonNode>() {

                    public void failed(UnirestException e) {
                        System.out.println("The request has failed");
                    }

                    public void completed(HttpResponse<JsonNode> response) {
                        int code = response.getStatus();
                        Headers headers = response.getHeaders();
                        JsonNode body = response.getBody();
                        InputStream rawBody = response.getRawBody();
                        System.out.println(body.toString());
                        try {
                            Unirest.shutdown();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    public void cancelled() {
                        System.out.println("The request has been cancelled");
                    }

                });


//        System.out.println(jsonResponse.getBody().toString());
    }
}
