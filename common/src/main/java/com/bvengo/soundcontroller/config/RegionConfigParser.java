package com.bvengo.soundcontroller.config;

import com.bvengo.soundcontroller.SoundController;
import com.bvengo.soundcontroller.VolumeData;
import com.bvengo.soundcontroller.region.BoxGeometry;
import com.bvengo.soundcontroller.region.RegionData;
import com.bvengo.soundcontroller.region.RegionGeometry;
import com.bvengo.soundcontroller.region.SphereGeometry;
import com.google.gson.*;
import net.minecraft.resources.Identifier;

import java.io.*;
import java.util.Comparator;

public class RegionConfigParser {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = SoundController.MOD_ID + "-regions.json";

    private static File getFile() {
        return ConfigParser.getConfigDir().resolve(FILE_NAME).toFile();
    }

    public static void loadConfig(RegionConfig config) {
        File file = getFile();
        if (!file.exists()) {
            return;
        }

        try (Reader reader = new FileReader(file)) {
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            if (root == null) return;

            JsonArray regionsArray = root.getAsJsonArray("regions");
            if (regionsArray == null) return;

            for (JsonElement el : regionsArray) {
                RegionData region = parseRegion(el.getAsJsonObject());
                if (region != null) {
                    config.getRegions().add(region);
                }
            }

            SoundController.LOGGER.info("Loaded {} region(s).", config.getRegions().size());
        } catch (Exception e) {
            SoundController.LOGGER.error("Error reading regions config: ", e);
        }
    }

    private static RegionData parseRegion(JsonObject obj) {
        try {
            String name = obj.get("name").getAsString();
            String serverKey = obj.get("serverKey").getAsString();
            String worldKey = obj.get("worldKey").getAsString();

            RegionGeometry geometry = parseGeometry(obj.getAsJsonObject("geometry"));
            if (geometry == null) return null;

            RegionData region = new RegionData(name, serverKey, worldKey, geometry);
            if (obj.has("enabled")) region.setEnabled(obj.get("enabled").getAsBoolean());

            JsonArray sounds = obj.getAsJsonArray("sounds");
            if (sounds != null) {
                for (JsonElement soundEl : sounds) {
                    JsonObject soundObj = soundEl.getAsJsonObject();
                    String soundId = soundObj.get("soundId").getAsString();
                    float volume = soundObj.get("volume").getAsFloat();
                    Identifier id = Identifier.tryParse(soundId);
                    if (id != null) {
                        region.getSoundOverrides().put(id, new VolumeData(id, volume));
                    }
                }
            }

            return region;
        } catch (Exception e) {
            SoundController.LOGGER.warn("Failed to parse region entry: {}", e.getMessage());
            return null;
        }
    }

    private static RegionGeometry parseGeometry(JsonObject obj) {
        if (obj == null) return null;
        String type = obj.get("type").getAsString();
        return switch (type) {
            case "sphere" -> new SphereGeometry(
                obj.get("x").getAsDouble(), obj.get("y").getAsDouble(), obj.get("z").getAsDouble(),
                obj.get("radius").getAsDouble()
            );
            case "box" -> new BoxGeometry(
                obj.get("x1").getAsDouble(), obj.get("y1").getAsDouble(), obj.get("z1").getAsDouble(),
                obj.get("x2").getAsDouble(), obj.get("y2").getAsDouble(), obj.get("z2").getAsDouble()
            );
            default -> {
                SoundController.LOGGER.warn("Unknown geometry type: {}", type);
                yield null;
            }
        };
    }

    public static void saveConfig(RegionConfig config) {
        JsonObject root = new JsonObject();
        root.addProperty("version", RegionConfig.CONFIG_VERSION);

        JsonArray regionsArray = new JsonArray();
        for (RegionData region : config.getRegions()) {
            regionsArray.add(serializeRegion(region));
        }
        root.add("regions", regionsArray);

        File file = getFile();
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(root, writer);
        } catch (IOException e) {
            SoundController.LOGGER.error("Unable to save regions config.", e);
        }
    }

    private static JsonObject serializeRegion(RegionData region) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", region.getName());
        obj.addProperty("enabled", region.isEnabled());
        obj.addProperty("serverKey", region.getServerKey());
        obj.addProperty("worldKey", region.getWorldKey());
        obj.add("geometry", serializeGeometry(region.getGeometry()));

        JsonArray sounds = new JsonArray();
        region.getSoundOverrides().entrySet().stream()
            .filter(e -> e.getValue().isModified())
            .sorted(Comparator.comparing(e -> e.getKey().toString()))
            .forEach(e -> {
                JsonObject soundObj = new JsonObject();
                soundObj.addProperty("soundId", e.getKey().toString());
                soundObj.addProperty("volume", e.getValue().getVolume());
                sounds.add(soundObj);
            });
        obj.add("sounds", sounds);

        return obj;
    }

    private static JsonObject serializeGeometry(RegionGeometry geometry) {
        JsonObject obj = new JsonObject();
        if (geometry instanceof SphereGeometry sphere) {
            obj.addProperty("type", "sphere");
            obj.addProperty("x", sphere.x);
            obj.addProperty("y", sphere.y);
            obj.addProperty("z", sphere.z);
            obj.addProperty("radius", sphere.radius);
        } else if (geometry instanceof BoxGeometry box) {
            obj.addProperty("type", "box");
            obj.addProperty("x1", box.x1);
            obj.addProperty("y1", box.y1);
            obj.addProperty("z1", box.z1);
            obj.addProperty("x2", box.x2);
            obj.addProperty("y2", box.y2);
            obj.addProperty("z2", box.z2);
        }
        return obj;
    }
}
