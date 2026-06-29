package com.bvengo.soundcontroller.config;

import com.bvengo.soundcontroller.SoundController;
import com.google.gson.*;
import net.minecraft.resources.Identifier;

import java.io.*;
import java.util.Comparator;
import java.util.HashMap;

public class PresetConfigParser {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = SoundController.MOD_ID + "-presets.json";

    private static File getFile() {
        return ConfigParser.getConfigDir().resolve(FILE_NAME).toFile();
    }

    public static void loadConfig(PresetConfig config) {
        File file = getFile();
        if (!file.exists()) return;

        try (Reader reader = new FileReader(file)) {
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            if (root == null) return;

            JsonArray presetsArray = root.getAsJsonArray("presets");
            if (presetsArray == null) return;

            for (JsonElement el : presetsArray) {
                PresetData preset = parsePreset(el.getAsJsonObject());
                if (preset != null) {
                    config.getPresets().add(preset);
                }
            }

            SoundController.LOGGER.info("Loaded {} preset(s).", config.getPresets().size());
        } catch (Exception e) {
            SoundController.LOGGER.error("Error reading presets config: ", e);
        }
    }

    private static PresetData parsePreset(JsonObject obj) {
        try {
            String name = obj.get("name").getAsString();
            HashMap<Identifier, Float> sounds = new HashMap<>();

            JsonArray soundsArray = obj.getAsJsonArray("sounds");
            if (soundsArray != null) {
                for (JsonElement soundEl : soundsArray) {
                    JsonObject soundObj = soundEl.getAsJsonObject();
                    Identifier id = Identifier.tryParse(soundObj.get("soundId").getAsString());
                    float volume = soundObj.get("volume").getAsFloat();
                    if (id != null) {
                        sounds.put(id, volume);
                    }
                }
            }

            return new PresetData(name, sounds);
        } catch (Exception e) {
            SoundController.LOGGER.warn("Failed to parse preset entry: {}", e.getMessage());
            return null;
        }
    }

    public static void saveConfig(PresetConfig config) {
        JsonObject root = new JsonObject();
        root.addProperty("version", PresetConfig.CONFIG_VERSION);

        JsonArray presetsArray = new JsonArray();
        for (PresetData preset : config.getPresets()) {
            presetsArray.add(serializePreset(preset));
        }
        root.add("presets", presetsArray);

        File file = getFile();
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(root, writer);
        } catch (IOException e) {
            SoundController.LOGGER.error("Unable to save presets config.", e);
        }
    }

    private static JsonObject serializePreset(PresetData preset) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", preset.getName());

        JsonArray sounds = new JsonArray();
        preset.getSounds().entrySet().stream()
            .sorted(Comparator.comparing(e -> e.getKey().toString()))
            .forEach(e -> {
                JsonObject soundObj = new JsonObject();
                soundObj.addProperty("soundId", e.getKey().toString());
                soundObj.addProperty("volume", e.getValue());
                sounds.add(soundObj);
            });
        obj.add("sounds", sounds);

        return obj;
    }
}
