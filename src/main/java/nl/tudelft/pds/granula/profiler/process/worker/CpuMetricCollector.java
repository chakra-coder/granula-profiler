package nl.tudelft.pds.granula.profiler.process.worker;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CpuMetricCollector extends  SystemMetricCollector{
    long kernelClockTick;
    int count = 0;
    List<Double> cpuTimes;

    double cpuTimePrevious = 0;
    double cpuTimeCurrent = 0;
    double cpuTimeDiff = 0;

    public CpuMetricCollector(String path, int frequency, long stopTime) {
        super(path, frequency, stopTime);
    }

    @Override
    public void open() {
        super.open();
        cpuTimes = new ArrayList<>();
        // 140ms for first iteration (this operation is only executed once per process
        kernelClockTick = Long.parseLong(collectFromExecution("getconf", "CLK_TCK"));
    }

    @Override
    public void collectOnce() throws IOException {
        String[] procMetrics = reader.readLine().split(" ");
        cpuTimePrevious = cpuTimeCurrent;
        cpuTimeCurrent = calculateCpuTime(procMetrics);
        cpuTimes.add(cpuTimeCurrent);
        cpuTimeDiff = cpuTimeCurrent - cpuTimePrevious;
        System.out.println(cpuTimeCurrent);
        count++;
    }

    public double calculateCpuTime(String[] procMetrics) {
        long uTime = Long.parseLong(procMetrics[13]);
        long sTime = Long.parseLong(procMetrics[14]);
        long cuTime = Long.parseLong(procMetrics[15]);
        long csTime = Long.parseLong(procMetrics[16]);

        return ((double) uTime + sTime + cuTime + csTime) / kernelClockTick;
    }

    public static void main(String[] args) {
        CpuMetricCollector cpuMetricCollector = new CpuMetricCollector("/proc/5153/stat", 1000, System.currentTimeMillis() + 100000);
        cpuMetricCollector.open();
        // 0.11ms for 1000 iteration (this operation is repeated per monitoring interval. open() and close() once is not more efficient.
        cpuMetricCollector.collect();
        cpuMetricCollector.close();
        System.out.println("count = " + cpuMetricCollector.count);

    }
}
