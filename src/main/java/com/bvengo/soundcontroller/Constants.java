package com.bvengo.soundcontroller;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class Constants {
    // Text constants
    public static final Text SOUND_SCREEN_TITLE = Text.translatable(SoundController.MOD_ID + ".options.title");
    public static final Text SEARCH_FIELD_PLACEHOLDER = Text.translatable(SoundController.MOD_ID + ".options.search.placeholder");
    public static final Text SEARCH_FIELD_TITLE = Text.translatable(SoundController.MOD_ID + ".options.search.title");
    public static final Text SEARCH_FILTER_TOOLTIP = Text.translatable(SoundController.MOD_ID + ".options.filter.tooltip");

    // Locations
    public static final Identifier FILTER_BUTTON_LOCATION = new Identifier(SoundController.MOD_ID, "textures/ui/filter_button.png");
}
