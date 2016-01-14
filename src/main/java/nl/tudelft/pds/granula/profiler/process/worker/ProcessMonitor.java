package nl.tudelft.pds.granula.profiler.process.worker;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import nl.tudelft.pds.granula.profiler.process.worker.comm.*;
import nl.tudelft.pds.granula.profiler.util.FileUtil;
import nl.tudelft.pds.granula.profiler.util.TimeUtility;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ProcessMonitor extends UntypedActor {

    int processId;
    Map<String, Class> collectorDefinitions;
    Map<String, ActorRef> collectors;
    boolean isNormal;
    JobMonitor jobMonitor;

    public ProcessMonitor(int processId, JobMonitor jobMonitor) {
        this.processId = processId;
        this.jobMonitor = jobMonitor;
        jobMonitor.registerProcessMonitor(processId, this);
    }

    public static void main(String[] args) {
        Config config = ConfigFactory.load("profiler-worker");
        int port = config.getInt("akka.profiler.worker.port");
        config = config.withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef(port));
        ProfilerWorker.actorSystem = ActorSystem.create("profiler-worker", config);

        ProcessMonitor processMonitor = new ProcessMonitor(7630, null);
        processMonitor.init();
        processMonitor.configure("process.cpu.time", 1000);
//        processMonitor.configure("os.network.volume", 1000);
        processMonitor.start();
        TimeUtility.Pause(2000);
        processMonitor.stop();
        processMonitor.configure("process.cpu.time", 100);
        processMonitor.configure("os.network.volume", 100);
        TimeUtility.Pause(2000);
        processMonitor.start();
        TimeUtility.Pause(2000);
        processMonitor.stop();
        processMonitor.kill();
        processMonitor.start();


    }

    public void init() {
        if(!isNormal) {
            try {
                defineCollectors();
                if(!processExists()) {
                    throw new IllegalArgumentException(String.format("%s failed to initialize. Process %s does not exists.",
                            this.getClass().getSimpleName(), processId));
                }
                markNormal();
                activateCollectors();
            } catch (IllegalArgumentException e) {
                System.out.println(String.format("%s failed to initialize.",
                        this.getClass().getSimpleName()));
                markAbnormal();
            }
        }
    }

    public void start() {
        if(isNormal) {
            System.out.println(String.format("%s is starting its collectors.", getName()));
            collectors.forEach((metric, collector) -> collector.tell(new StartCollectorRequest(), null));
        } else {
            System.out.println(String.format("%s is detective, cannot start its collectors.", getName()));
        }
    }

    public void stop() {
        System.out.println(String.format("%s is stopping its collectors.", getName()));
        collectors.forEach((metric, collector) -> collector.tell(new StopCollectorRequest(), null));
    }

    public void configure(String metric, int interval) {
        if(isNormal) {
            ActorRef systemMetricCollector = getOrCreateCollector(metric);
            systemMetricCollector.tell(new ConfigureCollectorRequest(processId, interval), null);
        } else {
            System.out.println(String.format("%s is detective, cannot be configured.", getName()));
        }
    }

    public void monitor(String metric, int interval) {
        configure(metric, interval);
    }

    public void kill() {
        System.out.println(String.format("%s is terminating its collectors.", getName()));
        collectors.forEach((metric, collector) -> collector.tell(new KillCollectorRequest(), null));
    }


    private ActorRef getOrCreateCollector(String metric) {
        if(collectors.containsKey(metric)) {
            return collectors.get(metric);
        } else {
            if(collectorDefinitions.containsKey(metric)) {
                ActorRef collector = ProfilerWorker.actorSystem.actorOf(Props.create(collectorDefinitions.get(metric)),
                        processId + "-" + metric + "-collector");
                collector.tell(new InitCollectorRequest(processId), null);
                collectors.put(metric, collector);
                return collector;
            } else {
                throw new IllegalStateException("metric is not defined.");
            }
        }
    }

    public void defineCollectors() {
        collectorDefinitions = new HashMap<>();
        collectors = new HashMap<>();
        collectorDefinitions.put("process.cpu.time", CpuProcessMetricCollector.class);
        collectorDefinitions.put("os.network.volume", NetworkOSMetricCollector.class);
    }

    public void activateCollectors() {
        monitor("process.cpu.time", 1000);
        monitor("os.network.volume", 1000);
    }

    public boolean isNormal() {
        return isNormal;
    }

    private boolean processExists() {
        return FileUtil.fileExists(Paths.get("/proc/" + processId));
    }


    private void markNormal() {
        System.out.println(String.format("%s is marked as operational.", getName()));
        isNormal = true;
    }

    private void markAbnormal() {
        System.out.println(String.format("%s is marked as defective.", getName()));
        isNormal = false;
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void onReceive(Object message) throws Exception {

    }
}
