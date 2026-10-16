package com.bvengo.soundcontroller;

//? if fabric {
/*import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
//? if >=26.1 {
/^import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
^///?} else {
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
//?}
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public final class FabricSoundController implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SoundController.bootstrap(FabricLoader.getInstance().getConfigDir());
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> SoundController.onClientStarted());

        /^? if >=26.1 {^//^ResourceLoader^//^?} else {^/ResourceManagerHelper/^?}^/.get(PackType.CLIENT_RESOURCES)
                .registerReloadListener(/^? if >=26.1 {^//^SoundReloadListener.ID, new SoundReloadListener()^//^?} else {^/new FabricSoundReloadListener()/^?}^/);
    }


    //? if <26.1 {
    private static final class FabricSoundReloadListener extends SoundReloadListener
            implements IdentifiableResourceReloadListener {
        @Override
        public ResourceLocation getFabricId() {
            return ID;
        }
    }
    //?}

}
*///?}
