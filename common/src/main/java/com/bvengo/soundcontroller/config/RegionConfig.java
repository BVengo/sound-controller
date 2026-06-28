package com.bvengo.soundcontroller.config;

import com.bvengo.soundcontroller.region.RegionData;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class RegionConfig {
    private static RegionConfig instance;
    public static final int CONFIG_VERSION = 1;

    private final List<RegionData> regions = new ArrayList<>();

    private RegionConfig() {
        RegionConfigParser.loadConfig(this);
    }

    public static RegionConfig getInstance() {
        if (instance == null) {
            instance = new RegionConfig();
        }
        return instance;
    }

    public List<RegionData> getRegions() {
        return regions;
    }

    public void addRegion(RegionData region) {
        regions.add(region);
    }

    public void removeRegion(RegionData region) {
        regions.remove(region);
    }

    public List<RegionData> getActiveRegions(String serverKey, String worldKey, Vec3 playerPos) {
        List<RegionData> active = new ArrayList<>();
        for (RegionData region : regions) {
            if (region.isActive(serverKey, worldKey, playerPos)) {
                active.add(region);
            }
        }
        return active;
    }

    public void save() {
        RegionConfigParser.saveConfig(this);
    }
}
