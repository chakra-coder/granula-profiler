package nl.tudelft.pds.granula.profiler.process.worker;

import akka.actor.ActorRef;
import nl.tudelft.pds.granula.profiler.comm.message.ActorId;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MasterInfo {
    String path;
    ActorRef actorRef;
    String ip;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ActorRef getActorRef() {
        return actorRef;
    }

    public void setActorRef(ActorRef actorRef) {
        this.actorRef = actorRef;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
