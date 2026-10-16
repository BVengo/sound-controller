package com.bvengo.soundcontroller;

//? if fabric {
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
//? if >=26.1 {
/*import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
*///?} else
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.packs.PackType;

public final class FabricSoundController implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SoundController.bootstrap(FabricLoader.getInstance().getConfigDir());
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> SoundController.onClientStarted());

        /*? if >=26.1 {*//*ResourceLoader*//*?} else {*/ResourceManagerHelper/*?}*/.get(PackType.CLIENT_RESOURCES)
                .registerReloadListener(SoundReloadListener.ID, new SoundReloadListener());
    }
}
//?}
