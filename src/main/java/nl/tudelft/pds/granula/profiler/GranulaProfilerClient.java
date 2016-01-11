package nl.tudelft.pds.granula.profiler;

import nl.tudelft.pds.granula.profiler.process.client.ProfilerClient;

public class GranulaProfilerClient {

    public static void main(String[] args) {
        startProfilerClient();
    }

    public static void startProfilerClient() {
        ProfilerClient profilerClient = new ProfilerClient();
        profilerClient.init();

    }
}
