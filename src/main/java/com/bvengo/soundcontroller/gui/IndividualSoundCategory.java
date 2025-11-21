package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.VolumeData;
import net.minecraft.text.Text;

import java.util.Locale;
import java.util.Set;

/**
 * Logical groupings for the individual sound entries. These groups are derived
 * from the first segment of a sound's identifier (e.g. {@code entity.}).
 */
public enum IndividualSoundCategory {
    AMBIENT(0, Translations.CATEGORY_AMBIENT, Set.of("ambient")),
    BLOCK(1, Translations.CATEGORY_BLOCK, Set.of("block")),
    ENTITY(2, Translations.CATEGORY_ENTITY, Set.of("entity")),
    ITEM(3, Translations.CATEGORY_ITEM, Set.of("item")),
    MUSIC(4, Translations.CATEGORY_MUSIC, Set.of("music", "music_disc", "record", "records")),
    WEATHER(5, Translations.CATEGORY_WEATHER, Set.of("weather")),
    UI(6, Translations.CATEGORY_UI, Set.of("ui")),
    MISCELLANEOUS(7, Translations.CATEGORY_MISCELLANEOUS, Set.of("misc", "miscellaneous"));

    private final int sortOrder;
    private final Text displayName;
    private final Set<String> matchKeys;

    IndividualSoundCategory(int sortOrder, Text displayName, Set<String> matchKeys) {
        this.sortOrder = sortOrder;
        this.displayName = displayName;
        this.matchKeys = matchKeys;
    }

    public Text getDisplayName() {
        return displayName;
    }

    public Text getScreenTitle() {
        return Translations.categoryScreenTitle(displayName);
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public boolean matches(VolumeData volumeData) {
        return from(volumeData) == this;
    }

    public static IndividualSoundCategory from(VolumeData volumeData) {
        String path = volumeData.getId().getPath();
        String leadingSegment = extractLeadingSegment(path);

        for (IndividualSoundCategory category : values()) {
            if (category.matchKeys.contains(leadingSegment)) {
                return category;
            }
        }

        return MISCELLANEOUS;
    }

    private static String extractLeadingSegment(String path) {
        String normalized = path.toLowerCase(Locale.ROOT);

        int slashIndex = normalized.lastIndexOf('/');
        if (slashIndex >= 0) {
            normalized = normalized.substring(slashIndex + 1);
        }

        int dotIndex = normalized.indexOf('.');
        if (dotIndex >= 0) {
            normalized = normalized.substring(0, dotIndex);
        }

        return normalized;
    }
}
