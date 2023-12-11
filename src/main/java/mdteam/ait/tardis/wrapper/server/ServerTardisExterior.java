package mdteam.ait.tardis.wrapper.server;

import io.wispforest.owo.ops.WorldOps;
import mdteam.ait.client.renderers.exteriors.ExteriorEnum;
import mdteam.ait.core.util.TardisUtil;
import mdteam.ait.tardis.TardisExterior;
import mdteam.ait.tardis.wrapper.server.manager.ServerTardisManager;
import mdteam.ait.tardis.Tardis;

public class ServerTardisExterior extends TardisExterior {

    public ServerTardisExterior(Tardis tardis, ExteriorEnum exterior) {
        super(tardis, exterior);
    }

    @Override
    public void setType(ExteriorEnum exterior) {
        super.setType(exterior);
        WorldOps.updateIfOnServer(TardisUtil.getTardisDimension(), tardis.getDoor().getInteriorDoorPosition());
        this.sync();
    }

    public void sync() {
        ServerTardisManager.getInstance().sendToSubscribers(this.tardis);
    }
}
