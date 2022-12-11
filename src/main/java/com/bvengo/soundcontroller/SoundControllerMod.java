package com.bvengo.soundcontroller;

import com.bvengo.soundcontroller.config.SoundControllerConfigs;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SoundControllerMod implements ClientModInitializer {
	public static final String MOD_ID = "soundcontroller";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info(LOGGER.getName() + " setting up...");

		ClientLifecycleEvents.CLIENT_STARTED.register((client) -> SoundControllerConfigs.init());

		LOGGER.info(LOGGER.getName() + " loaded.");
	}
}
