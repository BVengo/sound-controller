package com.bvengo.soundcontroller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bvengo.soundcontroller.config.SoundConfig;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SoundControllerMod implements ClientModInitializer {
	public static final String MOD_ID = "soundcontroller";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			// Load and register all sounds
			SoundConfig config = SoundConfig.getInstance();

			for (SoundEvent soundEvent : Registries.SOUND_EVENT) {
				if (soundEvent != SoundEvents.INTENTIONALLY_EMPTY) {
					Identifier identifier = soundEvent.getId();

					// How to get category???
					config.addSound(identifier);
				}
			}

			config.save();

		});

		LOGGER.info(LOGGER.getName() + " loaded.");
	}
}
