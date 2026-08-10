package com.bvengo.soundcontroller;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.packs.PackType;

public final class FabricSoundController implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		SoundController.bootstrap(FabricLoader.getInstance().getConfigDir());
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> SoundController.onClientStarted());

		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
			.registerReloadListener(new FabricSoundReloadListener());
	}

	private static final class FabricSoundReloadListener extends SoundReloadListener
		implements IdentifiableResourceReloadListener {
		@Override
		public net.minecraft.resources.ResourceLocation getFabricId() {
			return ID;
		}
	}
}
