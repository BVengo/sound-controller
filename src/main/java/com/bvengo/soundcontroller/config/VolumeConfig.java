package com.bvengo.soundcontroller.config;

import com.bvengo.soundcontroller.VolumeData;
import com.bvengo.soundcontroller.mixin.SoundSystemAccessor;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class VolumeConfig {
    private static VolumeConfig instance;
    public static final int CONFIG_VERSION = 4;

    private final HashMap<Identifier, VolumeData> soundVolumes;

    private boolean subtitlesEnabled = false;

    private VolumeConfig() {
        soundVolumes = new HashMap<>();
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
                Identifier soundId = soundEvent.id();
                soundVolumes.putIfAbsent(soundId, new VolumeData(soundId));
            }
        }
    }

    public HashMap<Identifier, VolumeData> getVolumes() {
        return soundVolumes;
    }

    public VolumeData getVolumeData(Identifier soundId) {
        return soundVolumes.getOrDefault(soundId, new VolumeData(soundId));
    }

    public float getAdjustedVolume(SoundInstance sound, SoundSystemAccessor soundSystem) {
        VolumeData volumeData = getVolumeData(sound.getId());
        return volumeData.getAdjustedVolume(sound,  soundSystem);
    }

    public boolean areSubtitlesEnabled() {
        return subtitlesEnabled;
    }

    public void toggleSubtitles() {
        subtitlesEnabled = !subtitlesEnabled;
    }
}