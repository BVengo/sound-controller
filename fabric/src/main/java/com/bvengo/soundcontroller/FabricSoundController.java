package com.bvengo.soundcontroller;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.packs.PackType;

public final class FabricSoundController implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		SoundController.bootstrap(FabricLoader.getInstance().getConfigDir());
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> SoundController.onClientStarted());

		ResourceLoader.get(PackType.CLIENT_RESOURCES)
			.registerReloadListener(SoundReloadListener.ID, new SoundReloadListener());
	}
}
