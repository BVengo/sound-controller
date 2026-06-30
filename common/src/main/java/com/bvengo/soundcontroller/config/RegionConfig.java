package com.bvengo.soundcontroller.config;

import com.bvengo.soundcontroller.SoundController;
import com.bvengo.soundcontroller.region.RegionData;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegionConfig {
    private static RegionConfig instance;
    public static final int CONFIG_VERSION = 1;

    private final List<RegionData> regions = new ArrayList<>();
    private String loadedServerKey = "";

    private RegionConfig() {
        loadServer(SoundController.getCurrentServerKey());
    }

    public static RegionConfig getInstance() {
        if (instance == null) {
            instance = new RegionConfig();
        }
        return instance;
    }

    public List<RegionData> getRegions() {
        loadServer(SoundController.getCurrentServerKey());
        return regions;
    }

    public void addRegion(RegionData region) {
        loadServer(region.getServerKey());
        regions.add(region);
    }

    public void removeRegion(RegionData region) {
        loadServer(region.getServerKey());
        regions.remove(region);
    }

    public List<RegionData> getActiveRegions(String serverKey, String worldKey, Vec3 playerPos) {
        loadServer(serverKey);
        List<RegionData> active = new ArrayList<>();
        for (RegionData region : regions) {
            if (region.isActive(serverKey, worldKey, playerPos)) {
                active.add(region);
            }
        }
        return active;
    }

    public void save() {
        RegionConfigParser.saveConfig(this, loadedServerKey);
    }

    void replaceLoadedRegions(String serverKey, List<RegionData> loadedRegions) {
        regions.clear();
        regions.addAll(loadedRegions);
        loadedServerKey = serverKey;
    }

    private void loadServer(String serverKey) {
        if (Objects.equals(loadedServerKey, serverKey)) {
            return;
        }
        RegionConfigParser.loadConfig(this, serverKey);
    }
}
