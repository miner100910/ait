package mdteam.ait.tardis.data.loyalty;

import mdteam.ait.tardis.Tardis;
import mdteam.ait.tardis.data.TardisLink;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.UUID;

public class LoyaltyData extends TardisLink {
    private HashMap<UUID, Loyalty> data;

    public LoyaltyData(Tardis tardisId, HashMap<UUID, Loyalty> data) {
        super(tardisId, "loyalty");
        this.data = data;
    }

    public LoyaltyData(Tardis tardis) {
        this(tardis, new HashMap<>());
    }

    public HashMap<UUID, Loyalty> data() {
        return this.data;
    }

    public void add(ServerPlayerEntity player) {
        this.add(player, Loyalty.NONE);
    }

    public void add(ServerPlayerEntity player, Loyalty loyalty) {
        this.data().put(player.getUuid(), loyalty);

        this.sync();
    }

    public Loyalty get(ServerPlayerEntity player) {
        return this.data().get(player.getUuid());
    }
}