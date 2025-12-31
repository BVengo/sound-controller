package com.bvengo.soundcontroller.config;

import com.bvengo.soundcontroller.VolumeData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class VolumeConfig {
    private static VolumeConfig instance;
    public static final int CONFIG_VERSION = 4;

    private final Map<Identifier, VolumeData> soundVolumes;

    public boolean subtitlesEnabled = false;

    private VolumeConfig() {
        this.soundVolumes = new HashMap<>();
        this.reload();
    }

    public static VolumeConfig getInstance() {
        if (instance == null) {
            instance = new VolumeConfig();
        }
        return instance;
    }

    public void saveAsync() {
        CompletableFuture.runAsync(() -> ConfigParser.saveConfig(this));
    }

    public void reload() {
        this.soundVolumes.clear();
        ConfigParser.loadConfig(this);

        Map<Identifier, VolumeData> originalLoadedVolumes = new HashMap<>(this.soundVolumes);

        // Update map with any sounds missing from the config file
        for (Identifier id : Minecraft.getInstance().getSoundManager().getAvailableSounds()) {
            this.soundVolumes.computeIfAbsent(id, VolumeData::new);
        }

        if (!originalLoadedVolumes.equals(this.soundVolumes)) {
            this.saveAsync();
        }
    }

    public Map<Identifier, VolumeData> getVolumes() {
        return this.soundVolumes;
    }

    public float getAdjustedVolume(SoundInstance sound, float baseVolume) {
        VolumeData volumeData = this.soundVolumes.get(sound.getIdentifier());
        return volumeData != null
                ? volumeData.getVolume() * baseVolume
                : baseVolume;
    }

    public boolean areSubtitlesEnabled() {
        return this.subtitlesEnabled;
    }

    public void toggleSubtitles() {
        this.subtitlesEnabled = !this.subtitlesEnabled;
    }
}