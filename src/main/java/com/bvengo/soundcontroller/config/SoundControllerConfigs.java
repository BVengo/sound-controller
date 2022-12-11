package com.bvengo.soundcontroller.config;

import com.bvengo.soundcontroller.SoundControllerMod;
import net.minecraft.registry.Registries;

import java.util.List;
import java.util.TreeMap;

public class SoundControllerConfigs {
    public static TreeMap<String, SoundConfig> flatMap = new TreeMap<>();

    private static ConfigWriter CONFIG;
    private static ConfigProvider configs;

    public static void init() {
        // Initialise the sound flatMap and CONFIG file.
        SoundControllerMod.LOGGER.info("Initialising sound configs");
        List<String> soundKeys = Registries.SOUND_EVENT.getKeys()   // Change this to include categories
                .stream().map(key -> key.getValue().toString()).toList();

        SoundCategory root = new SoundCategory(0, "", "");

        flatMap.put("master", root);
        soundKeys.forEach(key -> root.addChild(key).forEach(child -> flatMap.put(child.id, child)));

        // set individual records to have icons - have them stored in a list of <id>, <icon val (whatever that is)>
        // for(id in icon_ids) {
        //      soundMap.getSubcategory(id).icon = ...
        // }

        save();
        update();
    }

    public static void save() {
        // Saves configs to file.
        configs = new ConfigProvider();
        flatMap.forEach((key, value) -> {
            String clean_key = key.replaceAll("[:.]", "-");
            configs.addKeyValuePair(clean_key, value.volume, 1.0f);
        });

        if(CONFIG != null) {
            CONFIG.delete();
        }

        CONFIG = ConfigWriter.of(SoundControllerMod.MOD_ID + "_config").provider(configs).request();
    }

    public static void update() {
        // Updates the flatMap with existing values in the config file
        flatMap.forEach((key, value) -> {
            String clean_key = key.replaceAll("[:.]", "-");
            value.volume = (float) CONFIG.getOrDefault(clean_key, 1.0f);
        });
    }

    public static void update(String id, float volume) {
        /*
         * Update an individual sound volume. Accessing CONFIG will still provide the old value,
         * but flatMap will provide the new until save() has been called.
         */
        flatMap.get(id).volume = volume;
    }
}
