package com.bvengo.soundcontroller.config;

import com.bvengo.soundcontroller.gui.AllSoundOptionsScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.Minecraft;


public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new AllSoundOptionsScreen(parent, Minecraft.getInstance().options);
    }
}
