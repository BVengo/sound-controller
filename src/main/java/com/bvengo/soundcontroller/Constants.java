package com.bvengo.soundcontroller;

import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class Constants {
    // Text constants
    public static final Text SOUND_SCREEN_TITLE = Text.translatable(SoundController.MOD_ID + ".options.title");
    public static final Text SEARCH_FIELD_PLACEHOLDER = Text.translatable(SoundController.MOD_ID + ".options.search.placeholder");
    public static final Text SEARCH_FIELD_TITLE = Text.translatable(SoundController.MOD_ID + ".options.search.title");
    public static final Text SEARCH_FILTER_TOOLTIP = Text.translatable(SoundController.MOD_ID + ".options.filter.tooltip");
}
