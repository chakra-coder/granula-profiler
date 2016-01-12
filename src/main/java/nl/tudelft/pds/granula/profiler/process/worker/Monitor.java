package nl.tudelft.pds.granula.profiler.process.worker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Created by wlngai on 12-1-16.
 */
public class Monitor {

    public static void monitor(int processId, String metric) {
        switch (metric) {
            case "cputime":
                readCpu(String.format("/proc/%s/stat", processId));
                break;
            default:
                throw new IllegalStateException("No such metric defined");
        }
    }

    public static void readCpu(String fileName) {

        //read file into stream, try-with-resources
//        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
//            stream.forEach(System.out::println);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        long cpuTimePrevious = 0;
        long cpuTime = 0;
        long cpuTimeDiff = 0;
        while(true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

                String line;
                while ((line = br.readLine()) != null) {
                    String[] procMetrics = line.split(" ");
                    cpuTimePrevious = cpuTime;
                    cpuTime = Long.parseLong(procMetrics[13]);
                    cpuTimeDiff = cpuTime - cpuTimePrevious;

                    System.out.println(cpuTimeDiff);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        }

}
