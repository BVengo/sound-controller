package com.bvengo.soundcontroller.config;

import com.bvengo.soundcontroller.SoundController;
import com.bvengo.soundcontroller.VolumeData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;

public class ConfigParser {
	private static final Logger LOG = SoundController.LOGGER;

	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve(SoundController.MOD_ID + ".json");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static void loadConfig(VolumeConfig config) {
		if (!Files.exists(FILE)) {
			LOG.info("Config file not found. Creating a new one.");
			return;
		}

		try {
			JsonObject jsonObject = GSON.fromJson(Files.readString(FILE), JsonObject.class);
			if (jsonObject == null) {
				LOG.warn("Config file is empty, using defaults");
				return;
			}
			parseConfig(config, jsonObject);
		} catch (Exception e) {
			LOG.warn("Error reading config file, using defaults", e);
		}
	}

	public static synchronized void saveConfig(VolumeConfig config) {
		saveToJsonFile(createJsonConfig(config));
	}

	private static JsonObject createJsonConfig(VolumeConfig config) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("version", VolumeConfig.CONFIG_VERSION);
		jsonObject.addProperty("subtitlesEnabled", config.subtitlesEnabled);

		JsonArray sounds = new JsonArray();
		config.getVolumes().values().stream()
				.filter(volumeData -> volumeData.getVolume() != 1.0F)
				.sorted(Comparator.comparing(v -> v.getId().toString()))
				.forEach(volumeData -> {
					JsonObject soundObject = new JsonObject();
					soundObject.addProperty("soundId", volumeData.getId().toString());
					soundObject.addProperty("volume", volumeData.getVolume());
					sounds.add(soundObject);
				});

		jsonObject.add("sounds", sounds);
		return jsonObject;
	}

	private static void saveToJsonFile(JsonObject jsonObject) {
		try {
			Files.writeString(FILE, GSON.toJson(jsonObject));
		} catch (Exception e) {
			LOG.warn("Unable to save sound config to file", e);
		}
	}

	private static void parseConfig(VolumeConfig config, JsonObject jsonObject) {
		int version = jsonObject.has("version") ? jsonObject.get("version").getAsInt() : -1;

		if (version == -1) {
			LOG.warn("Config file does not have a version. Trying to parse old un-versioned format");
			parseConfigUnversioned(config, jsonObject);
			return;
		}

		if (version == 4) {
			parseConfig4(config, jsonObject);
			return;
		}

		LOG.warn("Unknown config version {}, expected {} - not parsing", version, VolumeConfig.CONFIG_VERSION);
	}

	private static void addVolumeData(Map<Identifier, VolumeData> soundVolumes, String soundId, float volume) {
		Identifier id = Identifier.tryParse(soundId);

		if (soundVolumes.containsKey(id)) {
			LOG.warn("Duplicate sound ID found in config: {}. Taking first only.", soundId);
			return;
		}

		VolumeData volumeData = new VolumeData(id, volume);

		if(id != null) {
			soundVolumes.put(id, volumeData);
		} else {
			LOG.warn("Invalid sound ID found in config: {}. Skipping.", soundId);
		}
	}


	/**
	 * Parse V4 configs. The structure is as follows:
	 * <pre>
	 * {
	 *    "version": 4,
	 *    "sounds": [
	 *    	{
	 *    		"soundId": "minecraft:entity.player.hurt",
	 *    		"volume": 0.5
	 *    	},
	 *    	...
	 * }
	 * </pre>
	 */
	private static void parseConfig4(VolumeConfig config, JsonObject jsonObject) {
		JsonElement subtitlesElement = jsonObject.get("subtitlesEnabled");
		if (subtitlesElement != null) {
			config.subtitlesEnabled = subtitlesElement.getAsBoolean();
		}

		Map<Identifier, VolumeData> soundVolumes = config.getVolumes();
		JsonArray sounds = jsonObject.getAsJsonArray("sounds");
		for (JsonElement soundElement : sounds) {
			JsonObject soundObject = soundElement.getAsJsonObject();
			String soundId = soundObject.get("soundId").getAsString();
			float volume = soundObject.get("volume").getAsFloat();

			addVolumeData(soundVolumes, soundId, volume);
		}

		LOG.info("Successfully loaded in configs");
	}

	/**
	 * Parse un-versioned configs (before 1.1.0/1.20.5).
	 * The structure is as follows:
	 * <pre>
	 * {
	 * 		// version 1:
	 * 		"minecraft.entity.player.hurt": 0.5,
	 * 		...
	 *
	 * 		// version 2:
	 * 		"minecraft:entity.player.hurt": {
	 *    		"id": "minecraft:entity.player.hurt",
	 *    		"volume": 0.5,
	 *    		"shouldOverride": false
	 *        },
	 * 		...
	 *
	 * 		// version 3:
	 *    	"minecraft:entity.player.hurt": {
	 *    		"soundId": "minecraft:entity.player.hurt",
	 *    		"volume": 0.5
	 *        },
	 *    	...
	 * }
	 * </pre>
	 */
	private static void parseConfigUnversioned(VolumeConfig config, JsonObject jsonObject) {
		Map<Identifier, VolumeData> soundVolumes = config.getVolumes();

		// Iterate over each entry in the JSON object assuming each key is a sound ID
		jsonObject.entrySet().forEach(entry -> {
			String key = entry.getKey();  // Should be the soundId as well
			JsonElement element = entry.getValue();

			String soundId;
			float volume;

			if (element.isJsonPrimitive()) {
				if (element.getAsJsonPrimitive().isNumber()) {
					// Handle V1 format
					soundId = key;
					volume = element.getAsFloat();
				} else {
					String msg = "Unsupported config format for sound ID: " + key;
					LOG.error(msg);
					throw new IllegalStateException(msg);
				}
			} else {
				JsonObject soundObject = element.getAsJsonObject();
				if (soundObject.has("id")) {
					// Handle V2 format
					soundId = soundObject.get("id").getAsString();
					volume = soundObject.get("volume").getAsFloat();
					// ignore "shouldOverride"
				} else if (soundObject.has("soundId")) {
					// Handle V3 format
					soundId = soundObject.get("soundId").getAsString();
					volume = soundObject.get("volume").getAsFloat();
				} else {
					String msg = "Unsupported config format for sound ID: " + key;
					SoundController.LOGGER.error(msg);
					throw new IllegalStateException(msg);
				}
			}

			addVolumeData(soundVolumes, soundId, volume);
		});
	}
}