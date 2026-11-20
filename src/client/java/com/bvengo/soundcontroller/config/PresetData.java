package com.bvengo.soundcontroller.config;

import net.minecraft.resources.Identifier;

import java.util.HashMap;

public class PresetData {
    private String name;
    private final HashMap<Identifier, Float> sounds;

    public PresetData(String name, HashMap<Identifier, Float> sounds) {
        this.name = name;
        this.sounds = sounds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<Identifier, Float> getSounds() {
        return sounds;
    }
}
