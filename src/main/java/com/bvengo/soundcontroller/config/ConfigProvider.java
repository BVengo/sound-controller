package com.bvengo.soundcontroller.config;

import com.mojang.datafixers.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ConfigProvider implements ConfigWriter.DefaultConfig {
    private final List<Pair<String, Float>> configsList = new ArrayList<>();
    private String configContents = "";

    public List<Pair<String, Float>> getConfigsList() {
        return configsList;
    }

    public void addKeyValuePair(String key, Float value, Float default_value) {
        Pair<String, Float> keyValuePair = new Pair<>(key, value);

        configsList.add(keyValuePair);
        configContents += keyValuePair.getFirst() + "=" + keyValuePair.getSecond() +
                "  # default: " + default_value + "\n";
    }

    @Override
    public String get(String namespace) {
        return configContents;
    }
}