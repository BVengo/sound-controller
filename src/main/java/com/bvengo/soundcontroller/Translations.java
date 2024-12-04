package com.bvengo.soundcontroller;

import net.minecraft.text.Text;

public abstract class Translations {
        // Text constants
        public static final Text SOUND_SCREEN_TITLE = translatableOf("title");
        public static final Text SEARCH_FIELD_PLACEHOLDER = translatableOf("search.placeholder");
        public static final Text SEARCH_FIELD_TITLE = translatableOf("search.title");
        public static final Text FILTER_BUTTON_TOOLTIP = translatableOf("filter.tooltip");
        public static final Text SUBTITLES_BUTTON_TOOLTIP = translatableOf("subtitles.tooltip");
        public static final Text RESET_BUTTON_TOOLTIP = translatableOf("reset.tooltip");
        public static final Text PLAY_BUTTON_TOOLTIP = translatableOf("play.tooltip");

        public static Text translatableOf(String key) {
            return Text.translatable(getTranslationKey(key));
        }

        public static String getTranslationKey(String key) {
            return SoundController.MOD_ID + ".options." + key;
        }
}
