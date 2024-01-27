package mdteam.ait.tardis.link;

import mdteam.ait.tardis.Tardis;
import mdteam.ait.tardis.TardisDesktop;
import mdteam.ait.tardis.TardisTravel;
import mdteam.ait.tardis.data.DoorData;

import java.util.Optional;

public interface Linkable {
    Optional<Tardis> getTardis();
    void setTardis(Tardis tardis);

    /**
     * If false, calling {@link Linkable#setTardis(Tardis)} might throw an exception!
     */
    default boolean linkable() {
        return true;
    }
}