package com.bvengo.soundcontroller;

import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bvengo.soundcontroller.config.VolumeConfig;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

@Environment(EnvType.CLIENT)
public class SoundController implements ClientModInitializer {
	public static final String MOD_ID = "soundcontroller";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static VolumeConfig CONFIG;

	@Override
	public void onInitializeClient() {
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			// Load and register all sounds
			CONFIG = VolumeConfig.getInstance();
		});

		ResourceLoader.get(PackType.CLIENT_RESOURCES)
			.registerReloader(SoundReloadListener.ID, new SoundReloadListener());

		LOGGER.info("{} loaded.", LOGGER.getName());
	}
}
