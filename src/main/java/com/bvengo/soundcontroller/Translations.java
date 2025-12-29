package com.bvengo.soundcontroller;

import net.minecraft.network.chat.Component;

public abstract class Translations {
        // Text constants
        public static final Component SOUND_SCREEN_TITLE = translatableOf("title");
        public static final Component SEARCH_FIELD_PLACEHOLDER = translatableOf("search.placeholder");
        public static final Component SEARCH_FIELD_TITLE = translatableOf("search.title");
        public static final Component FILTER_BUTTON_TOOLTIP = translatableOf("filter.tooltip");
        public static final Component SUBTITLES_BUTTON_TOOLTIP = translatableOf("subtitles.tooltip");
        public static final Component RESET_BUTTON_TOOLTIP = translatableOf("reset.tooltip");
        public static final Component PLAY_BUTTON_TOOLTIP = translatableOf("play.tooltip");

        public static Component translatableOf(String key) {
            return Component.translatable(getTranslationKey(key));
        }

        public static String getTranslationKey(String key) {
            return SoundController.MOD_ID + ".options." + key;
        }
}
