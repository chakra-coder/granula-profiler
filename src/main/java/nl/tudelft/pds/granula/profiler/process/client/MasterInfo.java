package nl.tudelft.pds.granula.profiler.process.client;

import akka.actor.ActorRef;

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
