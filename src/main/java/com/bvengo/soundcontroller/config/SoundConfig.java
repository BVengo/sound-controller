package com.bvengo.soundcontroller.config;

import java.util.Map;
import java.util.TreeMap;


public class SoundConfig {
    private static SoundConfig instance;

    public Map<String, Float> soundVolumes = new TreeMap<>();

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
        // Use SoundConfigWriter to get the saved / default sound volumes?
    }

    public void save() {
        // Use SoundConfigWriter to save the sound volumes?
    }

    public void addSound(String soundId) {
        soundVolumes.put(soundId, 1.0f);
    }

    public Float getVolumeMultiplier(String soundId) {
        return soundVolumes.getOrDefault(soundId, 1.0f);

    }

    public void setVolumeMultiplier(String soundId, float volume) {
        // Set the volume in the map, creating the inner map if necessary
        soundVolumes.put(soundId, volume);
    }
}