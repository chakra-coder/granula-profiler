package nl.tudelft.pds.granula.profiler.process.worker;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CpuProcessMetricCollector extends  SystemMetricCollector{
    long kernelClockTick;
    int count = 0;
    List<Double> cpuTimes;

    double cpuTimePrevious = 0;
    double cpuTimeCurrent = 0;
    double cpuTimeDiff = 0;

    public CpuProcessMetricCollector() {
    }

    @Override
    public void init(int processId) {
        cpuTimes = new ArrayList<>();
        // 140ms for first iteration (this operation is only executed once per process
        kernelClockTick = Long.parseLong(collectFromExecution("getconf", "CLK_TCK"));

        super.init(processId);
    }

    @Override
    public String decidePath() {
        return String.format("/proc/%s/stat", processId);
    }

    @Override
    public void collectOnce() throws IOException {
        String[] procMetrics = reader.readLine().split(" ");
        cpuTimePrevious = cpuTimeCurrent;
        cpuTimeCurrent = calculateCpuTime(procMetrics);
        cpuTimes.add(cpuTimeCurrent);
        cpuTimeDiff = cpuTimeCurrent - cpuTimePrevious;
        System.out.println("cpu.time " + cpuTimeCurrent);
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

        CpuProcessMetricCollector cpuProcessMetricCollector = new CpuProcessMetricCollector();
        cpuProcessMetricCollector.init(5153);
        // 0.11ms for 1000 iteration (this operation is repeated per monitoring interval. start() and stop() once is not more efficient.
        cpuProcessMetricCollector.stop();
        System.out.println("count = " + cpuProcessMetricCollector.count);

    }
}
