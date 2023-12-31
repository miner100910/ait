package mdteam.ait.client.animation;

import mdteam.ait.AITMod;
import mdteam.ait.core.blockentities.ExteriorBlockEntity;
import mdteam.ait.core.sounds.MatSound;
import mdteam.ait.tardis.TardisTravel;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;

@Deprecated
public class ClassicAnimation extends ExteriorAnimation {
    public ClassicAnimation(ExteriorBlockEntity exterior) {
        super(exterior);
    }

    @Override
    public void tick() {
        if (exterior.tardis() == null)
            return;

        TardisTravel.State state = exterior.tardis().getTravel().getState();

        if (this.timeLeft < 0)
            this.setupAnimation(exterior.tardis().getTravel().getState()); // fixme is a jank fix for the timeLeft going negative on client

        if (state == TardisTravel.State.DEMAT) {
            this.alpha = (float) this.timeLeft / (this.startTime);
            this.timeLeft--;

            runAlphaChecks(state);
        } else if (state == TardisTravel.State.MAT) {
            // Maybe this will fix the class animation taking too long
            this.alpha = ((float) this.timeLeft / (this.startTime) - 1) * -1;
            this.timeLeft--;
            this.setAlpha(this.alpha);
            runAlphaChecks(state);
        } else if (state == TardisTravel.State.LANDED/* && alpha != 1f*/) {
            this.setAlpha(1f);
        }
    }

    @Override
    public void setupAnimation(TardisTravel.State state) {
        if (exterior.tardis() == null) {
            AITMod.LOGGER.error("Tardis for exterior " + exterior + " was null! Panic!!!!");
            alpha = 0f; // just make me vanish.
            return;
        }
        MatSound sound = exterior.tardis().getExterior().getType().getSound(state);

        this.timeLeft = sound.timeLeft();
        this.maxTime = sound.maxTime();
        this.startTime = sound.startTime();

        if (state == TardisTravel.State.DEMAT) {
            this.timeLeft = 390;
            this.maxTime = 390;
            this.startTime = 390;
            this.alpha = 1f;
        } else if (state == TardisTravel.State.MAT) {
            this.alpha = 0f;
        } else if (state == TardisTravel.State.LANDED) {
            this.alpha = 1f;
        }
    }
}
