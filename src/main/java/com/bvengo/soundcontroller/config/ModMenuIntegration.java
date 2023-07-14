package com.bvengo.soundcontroller.config;

import com.bvengo.soundcontroller.mixin.ScreenAccessor;
import com.bvengo.soundcontroller.ui.AllSoundOptionsScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;


public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new AllSoundOptionsScreen(parent, ((ScreenAccessor)(Object)parent).getClient().options);
    }
}
