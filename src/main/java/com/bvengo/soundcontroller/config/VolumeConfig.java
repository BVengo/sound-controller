package com.bvengo.soundcontroller.config;

import com.bvengo.soundcontroller.SoundController;
import com.bvengo.soundcontroller.VolumeData;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class VolumeConfig {
    private static VolumeConfig instance;
    public static final int CONFIG_VERSION = 4;

    private final HashMap<Identifier, VolumeData> soundVolumes;

    public boolean subtitlesEnabled = false;

    private VolumeConfig() {
        soundVolumes = new HashMap<>();
        updateVolumes();
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

    public void updateVolumes() {
        this.soundVolumes.clear();
        
        ConfigParser.loadConfig(this);

        // Update map with any sounds missing from the config file
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();

        for (Identifier id : soundManager.getAvailableSounds()) {
            soundVolumes.putIfAbsent(id, new VolumeData(id));
        }

        ConfigParser.saveConfig(this);
    }

    public HashMap<Identifier, VolumeData> getVolumes() {
        return soundVolumes;
    }

    public VolumeData getVolumeData(Identifier soundId) {
        return soundVolumes.getOrDefault(soundId, new VolumeData(soundId));
    }

    public float getAdjustedVolume(SoundInstance sound, float baseVolume) {
        VolumeData volumeData = getVolumeData(sound.getIdentifier());
		return volumeData.getVolume() * baseVolume;
    }

    public boolean areSubtitlesEnabled() {
        return subtitlesEnabled;
    }

    public void toggleSubtitles() {
        subtitlesEnabled = !subtitlesEnabled;
    }
}