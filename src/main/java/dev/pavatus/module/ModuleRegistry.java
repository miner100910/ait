package dev.pavatus.module;

import dev.pavatus.planet.PlanetModule;
import dev.pavatus.register.datapack.DatapackRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

// these arent datapack definable
public class ModuleRegistry extends DatapackRegistry<Module> {
    public static final ModuleRegistry INSTANCE = new ModuleRegistry();

    @Override
    public void onCommonInit() {
        super.onCommonInit();

        register(PlanetModule.instance());

        iterator().forEachRemaining(Module::init);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onClientInit() {
        super.onClientInit();

        iterator().forEachRemaining(Module::initClient);
    }

    @Override
    public Module fallback() {
        return null;
    }

    @Override
    public void syncToClient(ServerPlayerEntity player) {

    }

    @Override
    public void readFromServer(PacketByteBuf buf) {

    }

    public static ModuleRegistry instance() {
        return INSTANCE;
    }
}
