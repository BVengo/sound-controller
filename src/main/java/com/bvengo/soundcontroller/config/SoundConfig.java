package com.bvengo.soundcontroller.config;

import java.util.HashMap;
import java.util.Map;

import com.bvengo.soundcontroller.SoundControllerMod;

import net.minecraft.util.Identifier;

public class SoundConfig {
    private static SoundConfig instance;

    private Map<Identifier, Float> soundVolumes = new HashMap<Identifier, Float>();

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

    public void addSound(Identifier identifier) {
        // Add a sound to the config
        SoundControllerMod.LOGGER.info("Added sound: " + identifier.toString());
    }

    public float getVolumeMultiplier(Identifier soundId) {
        // Get the volume from the map, or return 1.0F if it doesn't exist
        SoundControllerMod.LOGGER.info("Getting volume: " + soundId.toString());

        // Breaking leaves = grass.break/heat for an easy test
        return switch (soundId.toString()) {
            case "minecraft:block.grass.break", "minecraft:block.grass.hit" -> 0.0f;
            case "minecraft:ui.button.click", "minecraft:music.menu" -> 0.5f;
            default -> 1.0f;
        };
    }

    public void setVolumeMultiplier(Identifier soundId, float volume) {
        // Set the volume in the map, creating the inner map if necessary
        soundVolumes.put(soundId, volume);
    }
}