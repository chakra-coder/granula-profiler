package nl.tudelft.pds.granula.profiler.process.master;

import akka.actor.ActorRef;
import nl.tudelft.pds.granula.profiler.comm.message.ActorId;
import nl.tudelft.pds.granula.profiler.process.ProcessInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wlngai on 1/9/16.
 */
public class WorkerInfo extends ProcessInfo {
    ActorId id;
    String path;
    ActorRef actorRef;
    String ip;
    Status status;


    public ActorId getId() {
        return id;
    }

    public void setId(ActorId id) {
        this.id = id;
    }

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("WorkerInfo(%s, %s)", id.getShortId(), ip);
    }
}
