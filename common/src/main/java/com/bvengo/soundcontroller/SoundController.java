package com.bvengo.soundcontroller;

import com.bvengo.soundcontroller.config.ConfigParser;
import com.bvengo.soundcontroller.config.RegionConfig;
import com.bvengo.soundcontroller.config.VolumeConfig;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SoundController {
	public static final String MOD_ID = "soundcontroller";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static VolumeConfig config;
	private static RegionConfig regionConfig;

	private SoundController() {
	}

	public static void bootstrap(Path configDir) {
		ConfigParser.setConfigDir(configDir);
	}

	public static void onClientStarted() {
		config = VolumeConfig.getInstance();
		regionConfig = RegionConfig.getInstance();
		LOGGER.info("{} loaded.", LOGGER.getName());
	}

	public static VolumeConfig getConfig() {
		if (config == null) {
			config = VolumeConfig.getInstance();
		}
		return config;
	}

	public static RegionConfig getRegionConfig() {
		if (regionConfig == null) {
			regionConfig = RegionConfig.getInstance();
		}
		return regionConfig;
	}

	public static String getCurrentServerKey() {
		Minecraft mc = Minecraft.getInstance();
		MinecraftServer server = mc.getSingleplayerServer();
		if (server != null) {
			return "singleplayer:" + server.getWorldData().getLevelName();
		}
		ServerData serverData = mc.getCurrentServer();
		if (serverData != null) {
			return "multiplayer:" + serverData.ip;
		}
		return "unknown";
	}

	public static String getCurrentWorldKey() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) return "unknown";
		return mc.level.dimension().identifier().toString();
	}
}
