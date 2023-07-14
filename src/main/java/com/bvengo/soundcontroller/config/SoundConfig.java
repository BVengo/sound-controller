package com.bvengo.soundcontroller.config;

import com.bvengo.soundcontroller.SoundController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.io.*;
import java.lang.reflect.Type;
import java.util.TreeMap;


public class SoundConfig {
    private static SoundConfig instance;

    private TreeMap<String, Float> soundVolumes;

    private static final String CONFIG_PATH = SoundController.MOD_ID + ".json";
    private static File file;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Type type = new TypeToken<TreeMap<String, Float>>(){}.getType();

    private SoundConfig() {
        load();
    }

    public static SoundConfig getInstance() {
        if (instance == null) {
            instance = new SoundConfig();
        }
        return instance;
    }

    public void load() {
        if (file != null) {
            return;
        }
        file = new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG_PATH);

        // If file exists, load it into soundVolumes
        if(file.exists()) {
            try (Reader reader = new FileReader(file)) {
                soundVolumes = gson.fromJson(reader, type);
            } catch (IOException e) {
                SoundController.LOGGER.error("Unable to load sound config from file.", e);
                soundVolumes = new TreeMap<>();
            }
        } else {
            SoundController.LOGGER.warn("Unable to find sound config file, creating new one.");
            soundVolumes = new TreeMap<>();
        }

        // Update with any new sounds
        for (SoundEvent soundEvent : Registries.SOUND_EVENT) {
            if (soundEvent != SoundEvents.INTENTIONALLY_EMPTY) {
                Identifier identifier = soundEvent.getId();

                addSound(identifier.toString());
            }
        }

        save();
    }

    public void save() {
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(soundVolumes, writer);
        } catch (IOException e) {
            SoundController.LOGGER.error("Unable to save sound config to file.", e);
        }
    }

    public TreeMap<String, Float> getVolumes() {
        return soundVolumes;
    }

    public void addSound(String soundId) {
        soundVolumes.putIfAbsent(soundId, 1.0f);
    }

    public Float getVolumeMultiplier(String soundId) {
        if (!this.soundVolumes.containsKey(soundId)) {
            SoundController.LOGGER.warn("Unable to find volume for sound " + soundId + ", returning 1.0f");
        }
        return soundVolumes.getOrDefault(soundId, 1.0f);
    }

    public void setVolumeMultiplier(String soundId, float volume) {
        soundVolumes.put(soundId, volume);
    }
}