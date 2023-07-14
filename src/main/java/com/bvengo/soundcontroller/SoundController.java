package com.bvengo.soundcontroller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bvengo.soundcontroller.config.SoundConfig;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

@Environment(EnvType.CLIENT)
public class SoundController implements ClientModInitializer {
	public static final String MOD_ID = "soundcontroller";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			// Load and register all sounds
			SoundConfig.getInstance();
		});

		LOGGER.info(LOGGER.getName() + " loaded.");
	}
}
