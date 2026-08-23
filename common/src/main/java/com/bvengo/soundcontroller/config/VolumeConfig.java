package com.bvengo.soundcontroller.config;

import com.bvengo.soundcontroller.SoundController;
import com.bvengo.soundcontroller.VolumeData;
import com.bvengo.soundcontroller.region.RegionData;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
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
        return volumeData.getVolume() * baseVolume * getRegionVolume(sound);
    }

    public boolean shouldStartSilently(SoundInstance sound) {
        return sound instanceof TickableSoundInstance && getRegionVolume(sound) == 0.0f;
    }

    private float getRegionVolume(SoundInstance sound) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return VolumeData.DEFAULT_VOLUME;
        }

        Vec3 soundPos = sound.isRelative()
            ? mc.player.position()
            : new Vec3(sound.getX(), sound.getY(), sound.getZ());
        String serverKey = SoundController.getCurrentServerKey();
        String worldKey = SoundController.getCurrentWorldKey();
        Identifier soundId = sound.getIdentifier();

        float minRegionVolume = Float.MAX_VALUE;
        boolean hasOverride = false;

        List<RegionData> active = SoundController.getRegionConfig().getActiveRegions(serverKey, worldKey, soundPos);
        for (RegionData region : active) {
            if (region.hasSoundOverride(soundId)) {
                minRegionVolume = Math.min(minRegionVolume, region.getVolumeForSound(soundId));
                hasOverride = true;
            }
        }

        return hasOverride ? minRegionVolume : VolumeData.DEFAULT_VOLUME;
    }

    public boolean areSubtitlesEnabled() {
        return subtitlesEnabled;
    }

    public void toggleSubtitles() {
        subtitlesEnabled = !subtitlesEnabled;
    }
}
