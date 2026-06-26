package com.bvengo.soundcontroller;

import com.bvengo.soundcontroller.config.ConfigParser;
import com.bvengo.soundcontroller.config.VolumeConfig;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SoundController {
	public static final String MOD_ID = "soundcontroller";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static VolumeConfig config;

	private SoundController() {
	}

	public static void bootstrap(Path configDir) {
		ConfigParser.setConfigDir(configDir);
	}

	public static void onClientStarted() {
		config = VolumeConfig.getInstance();
		LOGGER.info("{} loaded.", LOGGER.getName());
	}

	public static VolumeConfig getConfig() {
		if (config == null) {
			config = VolumeConfig.getInstance();
		}

		return config;
	}
}
