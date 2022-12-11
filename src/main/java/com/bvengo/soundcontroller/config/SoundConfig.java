package com.bvengo.soundcontroller.config;

public class SoundConfig {

    String id;
    String name;
    float volume = 1.0f;

    public SoundConfig(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
