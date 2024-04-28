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
import net.minecraft.util.Identifier;

import java.io.*;
import java.lang.reflect.Type;
import java.util.TreeMap;

public class VolumeConfig {
    private static VolumeConfig instance;
    public static final int CONFIG_VERSION = 4;

    private TreeMap<String, VolumeData> soundVolumes;

    private boolean subtitlesEnabled = false;

    private VolumeConfig() {
        soundVolumes = new TreeMap<>();
        ConfigParser.loadConfig(this);
        updateVolumes();
        ConfigParser.saveConfig(this);
    }

    public static VolumeConfig getInstance() {
        if (instance == null) {
            instance = new VolumeConfig();
        }
        return instance;
    }

    public void save() {
        ConfigParser.saveConfig(this);
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

    public TreeMap<String, VolumeData> getVolumes() {
        return soundVolumes;
    }

    public VolumeData getVolumeData(String soundId) {
        return soundVolumes.getOrDefault(soundId, new VolumeData(soundId));
    }

    public boolean areSubtitlesEnabled() {
        return subtitlesEnabled;
    }

    public void toggleSubtitles() {
        subtitlesEnabled = !subtitlesEnabled;
    }
}