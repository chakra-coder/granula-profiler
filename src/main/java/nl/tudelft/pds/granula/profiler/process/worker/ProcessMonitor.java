package nl.tudelft.pds.granula.profiler.process.worker;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import nl.tudelft.pds.granula.profiler.process.ProcessInfo;
import nl.tudelft.pds.granula.profiler.process.worker.comm.StartCollectorRequest;
import nl.tudelft.pds.granula.profiler.process.worker.comm.StopCollectorRequest;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ProcessMonitor {
    Map<String, Class> collectorDefinitions;
    Map<String, ActorRef> collectors;

    int processId;

    public ProcessMonitor(int processId) {
        this.processId = processId;
    }

    public static void main(String[] args) {
        Config config = ConfigFactory.load("profiler-worker");
        int port = config.getInt("akka.profiler.worker.port");
        config = config.withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef(port));
        ProfilerWorker.actorSystem = ActorSystem.create("profiler-worker", config);

        ProcessMonitor processMonitor = new ProcessMonitor(2974);
        processMonitor.start();
        processMonitor.monitor("process.cpu.time", 1000, 100000);
        processMonitor.monitor("os.network.volume", 1000, 100000);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        processMonitor.stop();

    }

    public void start() {
        defineCollectors();
    }

    public void stop() {
        collectors.forEach( (metric, collector) -> collector.tell(new StopCollectorRequest(), null));
    }

    public void monitor(String metric, int interval, int duration) {
        ActorRef systemMetricCollector = getOrCreateCollector(metric);
        systemMetricCollector.tell(new StartCollectorRequest(processId, interval, duration), null);
    }


    private ActorRef getOrCreateCollector(String metric) {
        if(collectors.containsKey(metric)) {
            return collectors.get(metric);
        } else {
            if(collectorDefinitions.containsKey(metric)) {
                ActorRef collector = ProfilerWorker.actorSystem.actorOf(Props.create(collectorDefinitions.get(metric)), metric + "-collector");
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

}
