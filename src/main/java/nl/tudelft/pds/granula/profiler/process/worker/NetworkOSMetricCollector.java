package nl.tudelft.pds.granula.profiler.process.worker;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkOSMetricCollector extends SystemMetricCollector{

    Map<String, List<Long>> networkTraffics;
    String REMOTE = "remote";
    String LOCAL = "local";
    String LO = "lo";

    double totalTrafficPrevious = 0;
    double totalTrafficCurrent = 0;
    double totalTrafficDiff = 0;

    public NetworkOSMetricCollector() {
    }

    @Override
    public void init(int processId) {
        super.init(processId);
        createCache();
    }

    @Override
    public String decidePath() {
        return "/proc/net/dev";
    }

    private void createCache() {
        networkTraffics = new HashMap<>();

        try {
            reader.readLine();
            reader.readLine();
            networkTraffics.put(REMOTE, new ArrayList<>());
            networkTraffics.put(LOCAL, new ArrayList<>());
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    String netInterface = line.trim().split("\\s+")[0];
                    networkTraffics.put(netInterface, new ArrayList<>());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void collectOnce() throws IOException {

        reader.readLine();
        reader.readLine();
        long remoteReceiveVolume = 0, remoteTransmitVolume = 0;
        long localReceiveVolume = 0, localTransmitVolume = 0;

        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            String[] netIntMetrics = line.trim().split("\\s+");
            String netInterface = netIntMetrics[0];
            long receiveVolume = Long.parseLong(netIntMetrics[1]);
            long transmitVolume = Long.parseLong(netIntMetrics[9]);
//            networkTraffics.get(netInterface).add(receiveVolume);

            if(netInterface != LO) {
                remoteReceiveVolume += receiveVolume;
                remoteTransmitVolume += transmitVolume;
            } else {
                localReceiveVolume += receiveVolume;
                localTransmitVolume += transmitVolume;
            }
        }

        totalTrafficCurrent = remoteReceiveVolume;
        totalTrafficDiff = totalTrafficCurrent - totalTrafficPrevious;
        totalTrafficPrevious = remoteReceiveVolume;


        System.out.println("totalTrafficDiff " + " " + totalTrafficDiff);
    }


    public static void main(String[] args) {
        NetworkOSMetricCollector networkOSMetricCollector = new NetworkOSMetricCollector();
        networkOSMetricCollector.init(2947);
        // 0.11ms for 1000 iteration (this operation is repeated per monitoring interval. start() and stop() once is not more efficient.
        networkOSMetricCollector.stop();
    }
}
