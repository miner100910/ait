package loqor.ait.core.tardis.handler;

import java.util.Objects;
import java.util.UUID;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import loqor.ait.AITMod;
import loqor.ait.api.KeyedTardisComponent;
import loqor.ait.api.TardisEvents;
import loqor.ait.api.TardisTickable;
import loqor.ait.core.AITSounds;
import loqor.ait.core.item.SiegeTardisItem;
import loqor.ait.core.tardis.TardisDesktop;
import loqor.ait.core.tardis.manager.ServerTardisManager;
import loqor.ait.core.tardis.util.TardisUtil;
import loqor.ait.data.properties.Property;
import loqor.ait.data.properties.Value;
import loqor.ait.data.properties.bool.BoolProperty;
import loqor.ait.data.properties.bool.BoolValue;
import loqor.ait.data.properties.integer.IntProperty;
import loqor.ait.data.properties.integer.IntValue;

public class SiegeHandler extends KeyedTardisComponent implements TardisTickable {

    public static final Identifier DEFAULT_TEXTURRE = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/siege_mode/siege_mode.png");
    public static final Identifier BRICK_TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/siege_mode/siege_mode_brick.png");

    private static final Property<UUID> HELD_KEY = new Property<>(Property.Type.UUID, "siege_held_uuid", new UUID(0, 0));
    private static final Property<Identifier> TEXTURE = new Property<>(Property.Type.IDENTIFIER, "texture", DEFAULT_TEXTURRE);

    private static final IntProperty SIEGE_TIME = new IntProperty("siege_time", 0);
    private static final BoolProperty ACTIVE = new BoolProperty("siege_mode", false);

    private final Value<UUID> heldKey = HELD_KEY.create(this);
    private final IntValue siegeTime = SIEGE_TIME.create(this);
    private final BoolValue active = ACTIVE.create(this);
    private final Value<Identifier> texture = TEXTURE.create(this);

    public SiegeHandler() {
        super(Id.SIEGE);
    }

    static {
        TardisEvents.DEMAT.register(tardis -> tardis.siege().isActive() ? TardisEvents.Interaction.FAIL : TardisEvents.Interaction.PASS);

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.getPlayer();

            ServerTardisManager.getInstance().forEach(tardis -> {
                if (!tardis.siege().isActive())
                    return;

                if (!Objects.equals(tardis.siege().getHeldPlayerUUID(), player.getUuid()))
                    return;

                SiegeTardisItem.placeTardis(tardis, SiegeTardisItem.fromEntity(player));
            });
        });
    }

    @Override
    public void onLoaded() {
        active.of(this, ACTIVE);
        siegeTime.of(this, SIEGE_TIME);
        heldKey.of(this, HELD_KEY);
        texture.of(this, TEXTURE);
    }

    public boolean isActive() {
        return active.get();
    }

    public boolean isSiegeBeingHeld() {
        return heldKey.get() != null;
    }

    public UUID getHeldPlayerUUID() {
        if (!this.isSiegeBeingHeld())
            return null;

        return heldKey.get();
    }

    public void setSiegeBeingHeld(UUID playerId) {
        if (playerId != null)
            this.tardis.alarm().enabled().set(true);

        this.heldKey.set(playerId);
    }

    public int getTimeInSiegeMode() {
        return this.siegeTime.get();
    }

    public void setActive(boolean siege) {
        if (this.tardis.getFuel() <= (0.01 * FuelHandler.TARDIS_MAX_FUEL))
            return; // The required amount of fuel to enable/disable siege mode

        SoundEvent sound;

        if (siege) {
            sound = AITSounds.SIEGE_ENABLE;
            this.tardis.engine().disablePower();

            TardisUtil.giveEffectToInteriorPlayers(this.tardis.asServer(),
                    new StatusEffectInstance(StatusEffects.NAUSEA, 100, 0, false, false));
        } else {
            sound = AITSounds.SIEGE_DISABLE;
            this.tardis.alarm().enabled().set(false);

            if (this.tardis.getExterior().findExteriorBlock().isEmpty())
                this.tardis.travel().placeExterior(false);
        }

        for (BlockPos console : this.tardis.getDesktop().getConsolePos()) {
            TardisDesktop.playSoundAtConsole(console, sound, SoundCategory.BLOCKS, 3f, 1f);
        }

        this.tardis.removeFuel(0.01 * FuelHandler.TARDIS_MAX_FUEL * this.tardis.travel().instability());
        this.active.set(siege);
    }

    @Override
    public void tick(MinecraftServer server) {
        if (!this.active.get())
            return;

        this.siegeTime.flatMap(i -> this.active.get() ? i + 1 : 0);

        if (server.getTicks() % 20 != 0)
            return;

        boolean isHeld = this.isSiegeBeingHeld();

        if (isHeld && this.tardis.getExterior().findExteriorBlock().isPresent())
            this.setSiegeBeingHeld(null);

        boolean freeze = !isHeld && this.getTimeInSiegeMode() > 60 * 20;

        for (ServerPlayerEntity player : TardisUtil.getPlayersInsideInterior(this.tardis.asServer())) {
            if (!player.isAlive())
                continue;

            if (!freeze || player.canFreeze()) {
                this.unfreeze(player);
                continue;
            }

            this.freeze(player);
        }
    }

    private void freeze(ServerPlayerEntity player) {
        if (player.getFrozenTicks() < player.getMinFreezeDamageTicks())
            player.setFrozenTicks(player.getMinFreezeDamageTicks());

        player.setFrozenTicks(player.getFrozenTicks() + 2);
    }

    private void unfreeze(ServerPlayerEntity player) {
        if (player.getFrozenTicks() > player.getMinFreezeDamageTicks())
            player.setFrozenTicks(0);
    }

    public Value<Identifier> texture() {
        return texture;
    }
}