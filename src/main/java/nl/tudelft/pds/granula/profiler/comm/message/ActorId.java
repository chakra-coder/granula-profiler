package nl.tudelft.pds.granula.profiler.comm.message;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by wlngai on 1/9/16.
 */
public class ActorId implements Serializable {
    String id;

    public ActorId(String id) {
        this.id = id;
    }

    public String getShortId() {
        return id.substring(0, 3);
    }

    public static ActorId getRandomId() {
        return new ActorId(String.valueOf(new Random().nextInt(10000000) + 1000000));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActorId actorId = (ActorId) o;
        return !(id != null ? !id.equals(actorId.id) : actorId.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
