package com.bvengo.soundcontroller.config;

import com.bvengo.soundcontroller.SoundController;
import com.bvengo.soundcontroller.VolumeData;
import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.*;
import java.util.Comparator;
import java.util.HashMap;

public class ConfigParser {
	private static final File file = new File(FabricLoader.getInstance().getConfigDir().toFile(),
			SoundController.MOD_ID + ".json");
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * Loads data into a VolumeConfig object from a JSON file.
	 *
	 * @param config The VolumeConfig to load the data into.
	 */
	public static void loadConfig(VolumeConfig config) {
		if (!file.exists()) {
			SoundController.LOGGER.info("Config file not found. Creating a new one.");
			buildEmptyConfig();
			return;
		}

		try (Reader reader = new FileReader(file)) {
			JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
			if (jsonObject == null) {
				SoundController.LOGGER.error("Config file is empty, creating a new one.");
				buildEmptyConfig();
				return;
			}
			parseConfig(config.getVolumes(), jsonObject);
		} catch (Exception e) {
			SoundController.LOGGER.error("Error reading config file, creating a new one. Original error: ", e);
			moveOldConfig();
			buildEmptyConfig();
		}
	}

	/**
	 * Saves the provided VolumeConfig to a JSON file.
	 *
	 * @param config The VolumeConfig to save.
	 */
	public static void saveConfig(VolumeConfig config) {
		JsonObject jsonObject = createJsonConfig(config);
		saveToJsonFile(jsonObject);
	}

	/**
	 * Creates a JsonObject from a VolumeConfig.
	 *
	 * @param config The VolumeConfig to convert to JSON.
	 * @return JsonObject representing the provided VolumeConfig.
	 */
	private static JsonObject createJsonConfig(VolumeConfig config) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("version", VolumeConfig.CONFIG_VERSION);

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

	/**
	 * Writes a JsonObject to a JSON file.
	 *
	 * @param jsonObject The JsonObject to write to file.
	 */
	private static void saveToJsonFile(JsonObject jsonObject) {
		try (Writer writer = new FileWriter(file)) {
			gson.toJson(jsonObject, writer);
		} catch (IOException e) {
			SoundController.LOGGER.error("Unable to save sound config to file.", e);
		}
	}

	/**
	 * Parses volume data from a JSON object into a map. Handles versioning.
	 *
	 * @param soundVolumes The map to store the parsed sound volumes.
	 * @param jsonObject The JSON object to parse.
	 */
	private static void parseConfig(HashMap<Identifier, VolumeData> soundVolumes, JsonObject jsonObject) {
		int version = jsonObject.has("version") ? jsonObject.get("version").getAsInt() : -1;

		// Check if the version key exists to determine the handling strategy
		if (version == -1) {
			String msg = "Config file does not have a version number. Trying to parse old un-versioned format.";
			SoundController.LOGGER.warn(msg);

			parseConfigUnversioned(soundVolumes, jsonObject);
			return;
		}

		if (version < 4 || version > VolumeConfig.CONFIG_VERSION) {
			String msg = "Version number invalid - must be between 4 and " + VolumeConfig.CONFIG_VERSION +
					" (inclusive). Got " + version + " instead. Storing old config file with `old.` prefix, " +
					"and initializing a new empty config file.";
			SoundController.LOGGER.error(msg);
			moveOldConfig();
			buildEmptyConfig();
			return;
		}

//		if (version == 4) {
			// Currently always true
			parseConfig4(soundVolumes, jsonObject);
//		}
	}

	/**
	 * Moves the old config file to a new file with a prefix of `old.`.
	 */
	private static void moveOldConfig() {
		File oldFile = new File(file.getParentFile(), "old." + file.getName());
		if (file.renameTo(oldFile)) {
			SoundController.LOGGER.info("Renamed old config file to " + oldFile.getName());
		} else {
			SoundController.LOGGER.error("Failed to rename old config file.");
		}
	}

	/**
	 * Builds an empty config file with the current version number.
	 */
	private static void buildEmptyConfig() {
		JsonObject newConfig = new JsonObject();
		newConfig.addProperty("version", VolumeConfig.CONFIG_VERSION);
		newConfig.add("sounds", new JsonArray());

		try (Writer writer = new FileWriter(file)) {
			gson.toJson(newConfig, writer);
		} catch (IOException e) {
			SoundController.LOGGER.error("Failed to create a new empty config file.", e);
		}
	}

	/**
	 * Adds a sound ID and volume to the soundVolumes map, provided the sound is valid.
	 *
	 * @param soundVolumes The map to store the sound volumes.
	 * @param soundId The sound ID to add.
	 * @param volume The volume to add.
	 */
	private static void addVolumeData(HashMap<Identifier, VolumeData> soundVolumes, String soundId, float volume) {
		Identifier id = Identifier.tryParse(soundId);

		if (soundVolumes.containsKey(id)) {
			SoundController.LOGGER.warn("Duplicate sound ID found in config: {}. Taking first only.", soundId);
			return;
		}

		VolumeData volumeData = new VolumeData(id, volume);

		if(id != null) {
			soundVolumes.put(id, volumeData);
		} else {
			SoundController.LOGGER.warn("Invalid sound ID found in config: {}. Skipping.", soundId);
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
	 *
	 * @param soundVolumes The map to store the parsed sound volumes.
	 * @param jsonObject The JSON object to parse.
	 */
	private static void parseConfig4(HashMap<Identifier, VolumeData> soundVolumes, JsonObject jsonObject) {
		JsonArray sounds = jsonObject.getAsJsonArray("sounds");
		for (JsonElement soundElement : sounds) {
			JsonObject soundObject = soundElement.getAsJsonObject();
			String soundId = soundObject.get("soundId").getAsString();
			float volume = soundObject.get("volume").getAsFloat();

			addVolumeData(soundVolumes, soundId, volume);
		}

		SoundController.LOGGER.info("Successfully loaded in configs.");
	}

	/**
	 * Parse un-versioned configs. The structure is as follows:
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
	 *    	},
	 *		...
	 *
	 * 		// version 3:
	 *    	"minecraft:entity.player.hurt": {
	 *    		"soundId": "minecraft:entity.player.hurt",
	 *    		"volume": 0.5
	 *    	},
	 *    	...
	 * }
	 * </pre>
	 *
	 * @param soundVolumes The map to store the parsed sound volumes.
	 * @param jsonObject The JSON object to parse.
	 */
	private static void parseConfigUnversioned(HashMap<Identifier, VolumeData> soundVolumes, JsonObject jsonObject) {
		// Iterate over each entry in the JSON object assuming each key is a sound ID
		jsonObject.entrySet().forEach(entry -> {
			String key = entry.getKey();  // Should be the soundId as well
			JsonElement element = entry.getValue();

			String soundId;
			float volume;

			if(element.isJsonPrimitive()) {
				if(element.getAsJsonPrimitive().isNumber()) {
					// Handle V1 format
					soundId = key;
					volume = element.getAsFloat();
				} else {
					String msg = "Unsupported config format for sound ID: " + key;
					SoundController.LOGGER.error(msg);
					throw new IllegalStateException(msg);
				}
			}
			else {
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