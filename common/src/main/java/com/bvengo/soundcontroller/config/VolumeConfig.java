package com.bvengo.soundcontroller.config;

import com.bvengo.soundcontroller.SoundController;
import com.bvengo.soundcontroller.VolumeData;
import com.bvengo.soundcontroller.region.RegionData;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

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
        float volume = volumeData.getVolume() * baseVolume;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.level != null) {
            Vec3 playerPos = mc.player.position();
            String serverKey = SoundController.getCurrentServerKey();
            String worldKey = SoundController.getCurrentWorldKey();
            Identifier soundId = sound.getIdentifier();

            float minRegionVolume = Float.MAX_VALUE;
            boolean hasOverride = false;

            List<RegionData> active = SoundController.getRegionConfig().getActiveRegions(serverKey, worldKey, playerPos);
            for (RegionData region : active) {
                if (region.hasSoundOverride(soundId)) {
                    minRegionVolume = Math.min(minRegionVolume, region.getVolumeForSound(soundId));
                    hasOverride = true;
                }
            }

            if (hasOverride) {
                volume *= minRegionVolume;
            }
        }

        return volume;
    }

    public boolean areSubtitlesEnabled() {
        return subtitlesEnabled;
    }

    public void toggleSubtitles() {
        subtitlesEnabled = !subtitlesEnabled;
    }
}