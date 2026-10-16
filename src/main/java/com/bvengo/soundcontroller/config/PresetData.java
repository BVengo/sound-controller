package com.bvengo.soundcontroller.config;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class PresetData {
    private String name;
    private final HashMap<ResourceLocation, Float> sounds;

    public PresetData(String name, HashMap<ResourceLocation, Float> sounds) {
        this.name = name;
        this.sounds = sounds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<ResourceLocation, Float> getSounds() {
        return sounds;
    }
}
