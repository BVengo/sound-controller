package com.bvengo.soundcontroller.config;

import com.bvengo.soundcontroller.SoundController;
import com.bvengo.soundcontroller.VolumeData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.io.*;
import java.lang.reflect.Type;
import java.util.TreeMap;


public class VolumeConfig {
    private static VolumeConfig instance;

    private TreeMap<String, VolumeData> soundVolumes;

    private static final File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), SoundController.MOD_ID + ".json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Type type = new TypeToken<TreeMap<String, VolumeData>>(){}.getType();

    private VolumeConfig() {
        load();
    }

    public static VolumeConfig getInstance() {
        if (instance == null) {
            instance = new VolumeConfig();
        }
        return instance;
    }

    public void load() {
        loadVolumes();
        updateVolumes();
        save();
    }

    private void loadVolumes() {
        // If the file exists, load the existing configs into soundVolumes
        if(file.exists()) {
            try (Reader reader = new FileReader(file)) {
                soundVolumes = gson.fromJson(reader, type);
            } catch (Exception e) {
                SoundController.LOGGER.error("Unable to load sound config from file. Perhaps the latest update changed the config structure?", e);
                soundVolumes = new TreeMap<>();
            }
        } else {
            // If the file doesn't exist, initialise an empty map
            SoundController.LOGGER.info("Unable to find sound config file, creating new one.");
            soundVolumes = new TreeMap<>();
        }
    }

    private void updateVolumes() {
        // Update map with any sounds missing from the config file
        for (SoundEvent soundEvent : Registries.SOUND_EVENT) {
            if (soundEvent != SoundEvents.INTENTIONALLY_EMPTY) {
                String soundId = soundEvent.getId().toString();
                soundVolumes.putIfAbsent(soundId, new VolumeData(soundId));
            }
        }
    }

    public void save() {
        // Only save sounds that have been modified
        TreeMap<String, VolumeData> modifiedValues = soundVolumes
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().isModified())
                .collect(
                        TreeMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        TreeMap::putAll
                );

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(modifiedValues, writer);
        } catch (IOException e) {
            SoundController.LOGGER.error("Unable to save sound config to file.", e);
        }
    }

    public TreeMap<String, VolumeData> getVolumes() {
        return soundVolumes;
    }

    public VolumeData getVolumeData(String soundId) {
        return soundVolumes.getOrDefault(soundId, new VolumeData(soundId));
    }
}