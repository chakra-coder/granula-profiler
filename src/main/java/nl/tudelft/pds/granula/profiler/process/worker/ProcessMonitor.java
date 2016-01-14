package nl.tudelft.pds.granula.profiler.process.worker;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import nl.tudelft.pds.granula.profiler.process.worker.comm.*;
import nl.tudelft.pds.granula.profiler.util.TimeUtility;

import java.util.HashMap;
import java.util.Map;

public class ProcessMonitor {

    int processId;
    Map<String, Class> collectorDefinitions;
    Map<String, ActorRef> collectors;
    boolean isInit;

    public ProcessMonitor(int processId) {
        this.processId = processId;
    }

    public static void main(String[] args) {
        Config config = ConfigFactory.load("profiler-worker");
        int port = config.getInt("akka.profiler.worker.port");
        config = config.withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef(port));
        ProfilerWorker.actorSystem = ActorSystem.create("profiler-worker", config);

        ProcessMonitor processMonitor = new ProcessMonitor(16495);
        processMonitor.init();
        processMonitor.configure("process.cpu.time", 1000);
        processMonitor.configure("os.network.volume", 1000);
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
        if(!isInit) {
            defineCollectors();
            isInit = true;
        }

    }

    public void start() {
        collectors.forEach((metric, collector) -> collector.tell(new StartCollectorRequest(), null));
    }

    public void stop() {
        collectors.forEach((metric, collector) -> collector.tell(new StopCollectorRequest(), null));
    }

    public void configure(String metric, int interval) {
        ActorRef systemMetricCollector = getOrCreateCollector(metric);
        systemMetricCollector.tell(new ConfigureCollectorRequest(processId, interval), null);
    }

    public void kill() {
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

    public boolean isInit() {
        return isInit;
    }
}
