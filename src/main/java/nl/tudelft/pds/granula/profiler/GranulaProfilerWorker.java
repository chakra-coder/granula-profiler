package nl.tudelft.pds.granula.profiler;

import nl.tudelft.pds.granula.profiler.process.master.ProfilerMaster;
import nl.tudelft.pds.granula.profiler.process.worker.ProfilerWorker;

public class GranulaProfilerWorker {

    public static void main(String[] args) {
        startProfilerWorker();
    }



    public static void startProfilerMaster() {
        ProfilerMaster profilerMaster = new ProfilerMaster();
        profilerMaster.init();
    }


    public static void startProfilerWorker() {

        int workerSize = 3;
        for (int i = 0; i < workerSize; i++) {
            ProfilerWorker profilerWorker = new ProfilerWorker();
            profilerWorker.init();
        }

    }
}
