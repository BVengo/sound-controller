package com.bvengo.soundcontroller.config;

import java.util.ArrayList;
import java.util.List;

public class PresetConfig {
    private static PresetConfig instance;
    public static final int CONFIG_VERSION = 1;

    private final List<PresetData> presets = new ArrayList<>();

    private PresetConfig() {
        PresetConfigParser.loadConfig(this);
    }

    public static PresetConfig getInstance() {
        if (instance == null) {
            instance = new PresetConfig();
        }
        return instance;
    }

    public List<PresetData> getPresets() {
        return presets;
    }

    public void addPreset(PresetData preset) {
        presets.add(preset);
    }

    public void removePreset(PresetData preset) {
        presets.remove(preset);
    }

    public void save() {
        PresetConfigParser.saveConfig(this);
    }
}
