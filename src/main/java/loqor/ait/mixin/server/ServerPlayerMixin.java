package loqor.ait.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.network.ServerPlayerEntity;

import loqor.ait.core.tardis.Tardis;
import loqor.ait.core.tardis.dim.TardisDimension;
import loqor.ait.core.tardis.util.TardisUtil;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void ait$tick(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // if player is in tardis and y is less than -100 save them
        if (player.getY() <= -100 && TardisDimension.isTardisDimension(player.getServerWorld())) {
            Tardis found = TardisDimension.get(player.getServerWorld()).orElse(null);

            if (found == null)
                return;
            TardisUtil.teleportInside(found, player);
            player.fallDistance = 0;
        }
    }
}
