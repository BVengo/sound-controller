package com.bvengo.soundcontroller.region;

import com.bvengo.soundcontroller.VolumeData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;

public class RegionData {
    private String name;
    private final String serverKey;
    private final String worldKey;
    private RegionGeometry geometry;
    private HashMap<Identifier, VolumeData> soundOverrides;
    private boolean enabled = true;

    public RegionData(String name, String serverKey, String worldKey, RegionGeometry geometry) {
        this.name = name;
        this.serverKey = serverKey;
        this.worldKey = worldKey;
        this.geometry = geometry;
        this.soundOverrides = new HashMap<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getServerKey() { return serverKey; }
    public String getWorldKey() { return worldKey; }

    public RegionGeometry getGeometry() { return geometry; }
    public void setGeometry(RegionGeometry geometry) { this.geometry = geometry; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public HashMap<Identifier, VolumeData> getSoundOverrides() { return soundOverrides; }
    public void setSoundOverrides(HashMap<Identifier, VolumeData> overrides) { this.soundOverrides = overrides; }

    public boolean hasSoundOverride(Identifier soundId) {
        return soundOverrides.containsKey(soundId);
    }

    public float getVolumeForSound(Identifier soundId) {
        VolumeData data = soundOverrides.get(soundId);
        return data != null ? data.getVolume() : VolumeData.DEFAULT_VOLUME;
    }

    public boolean isActive(String serverKey, String worldKey, Vec3 playerPos) {
        return this.enabled
            && this.serverKey.equals(serverKey)
            && this.worldKey.equals(worldKey)
            && this.geometry.contains(playerPos);
    }
}
