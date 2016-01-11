package nl.tudelft.pds.granula.profiler.process.client;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import nl.tudelft.pds.granula.profiler.comm.message.CommandRequest;
import nl.tudelft.pds.granula.profiler.comm.message.RegisterRequest;
import nl.tudelft.pds.granula.profiler.process.ProcessInfo;
import scala.concurrent.duration.Duration;

import java.util.Random;
import java.util.Scanner;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ProfilerClient {

    MasterInfo masterInfo;
    ClientAssistant cAssistant;

    public ProfilerClient() {
        masterInfo = new MasterInfo();
    }

    public void init() {

        Config config = ConfigFactory.load("profiler-client");
        int port = config.getInt("akka.profiler.client.port");

        int masterPort = config.getInt("akka.profiler.master.port");
        String masterIp = config.getString("akka.profiler.master.ip");
        masterInfo.setPath(String.format("akka.tcp://profiler-master@%s:%s/user/profiler-master", masterIp, masterPort));
        masterInfo.setIp(ProcessInfo.Path2IpAddress(masterInfo.getPath()));

        port = config.getInt("akka.profiler.client.port");
        port = (new Random()).nextInt(4000)+4000;
        config = config.withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef(port));
        final ActorSystem system = ActorSystem.create("profiler-client", config);
        system.actorOf(Props.create(ClientAssistant.class, this), "profiler-client");
        getUserInput();
    }

    public void getUserInput() {

        //        system.scheduler().schedule(Duration.create(1, SECONDS),
//                Duration.create(10, SECONDS), new Runnable() {
//                    @Override
//                    public void run() {
//                        cAssistant.sendHealthRequest();
//                    }
//                }, system.dispatcher());

        boolean stop = false;
        System.out.println("Granula Profiler Commandline Client");
        System.out.println("Enter a command (health, xx)");
        while (!stop) {
            Scanner in = new Scanner(System.in);
            String s = in.nextLine();
            switch (s) {
                case "health":
                    System.out.println("Monitoring Health");
                    cAssistant.sendHealthRequest();
                    break;
                case "xx":
                    System.out.println("XX is useless command");
                    break;
                case "exit":
                    System.out.println("Exiting");
                    System.exit(0);
                default:
                    System.out.println("\"" + s + "\" is not a valid command");
                    break;
            }
        }
    }

    public void setClientAssistant(ClientAssistant cAssistant) {
        this.cAssistant = cAssistant;
    }

    public MasterInfo getMasterInfo() {
        return masterInfo;
    }
}
