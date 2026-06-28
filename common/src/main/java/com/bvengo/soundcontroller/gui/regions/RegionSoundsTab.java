package com.bvengo.soundcontroller.gui.regions;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.VolumeData;
import com.bvengo.soundcontroller.config.VolumeConfig;
import com.bvengo.soundcontroller.gui.buttons.ToggleButtonWidget;
import com.bvengo.soundcontroller.gui.components.VolumeListWidget;
import com.bvengo.soundcontroller.gui.components.VolumeWidgetEntry;
import com.bvengo.soundcontroller.region.RegionData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.bvengo.soundcontroller.Translations.FILTER_BUTTON_TOOLTIP;
import static com.bvengo.soundcontroller.Translations.SEARCH_FIELD_PLACEHOLDER;
import static com.bvengo.soundcontroller.Translations.SEARCH_FIELD_TITLE;

public class RegionSoundsTab implements Tab {

    private final Screen screen;
    private final Options options;
    private final Layout layout = LinearLayout.vertical();

    private final StringWidget searchLabel;
    private final EditBox searchField;
    private final ToggleButtonWidget filterButton;
    private final VolumeListWidget volumeListWidget;

    private final HashMap<Identifier, VolumeData> workingSounds;
    private boolean showModifiedOnly = false;

    public RegionSoundsTab(Screen screen, Options options, RegionData existingRegion) {
        this.screen = screen;
        this.options = options;

        workingSounds = new HashMap<>();
        for (Identifier soundId : VolumeConfig.getInstance().getVolumes().keySet()) {
            float vol = existingRegion != null
                ? existingRegion.getVolumeForSound(soundId)
                : VolumeData.DEFAULT_VOLUME;
            workingSounds.put(soundId, new VolumeData(soundId, vol));
        }

        Font font = Minecraft.getInstance().font;

        searchLabel = new StringWidget(SEARCH_FIELD_TITLE, font);

        searchField = new EditBox(font, 0, 0, 200, 20, SEARCH_FIELD_PLACEHOLDER);
        searchField.setResponder(s -> loadSoundOptions());

        filterButton = new ToggleButtonWidget("filter", 0, 0, 20, 20, b -> {
            showModifiedOnly = !showModifiedOnly;
            loadSoundOptions();
        }, false);
        filterButton.setTooltip(Tooltip.create(FILTER_BUTTON_TOOLTIP));

        volumeListWidget = new VolumeListWidget(Minecraft.getInstance(), 200, 100, 0);
        loadSoundOptions();
    }

    @Override
    public Component getTabTitle() {
        return Translations.translatableOf("tab.sounds");
    }

    @Override
    public Component getTabExtraNarration() {
        return Component.empty();
    }

    @Override
    public void visitChildren(Consumer<AbstractWidget> consumer) {
        consumer.accept(searchLabel);
        consumer.accept(searchField);
        consumer.accept(filterButton);
        consumer.accept(volumeListWidget);
    }

    @Override
    public void doLayout(ScreenRectangle rect) {
        Font font = Minecraft.getInstance().font;
        int top = rect.top() + 8;
        int left = rect.left() + 8;

        int labelWidth = font.width(SEARCH_FIELD_TITLE);
        searchLabel.setPosition(left, top + (20 - searchLabel.getHeight()) / 2);
        searchField.setPosition(left + labelWidth + 4, top);
        searchField.setWidth(rect.width() - labelWidth - 20 - 4 - 4 - 16);
        filterButton.setPosition(searchField.getRight() + 4, top);

        int listTop = top + 28;
        volumeListWidget.updateSizeAndPosition(rect.width(), rect.height() - (listTop - rect.top()), listTop);

        loadSoundOptions();
    }

    @Override
    public Layout getLayout() {
        return layout;
    }

    public String getSearchValue() {
        return searchField.getValue();
    }

    public void setSearchValue(String value) {
        searchField.setValue(value);
    }

    public Map<Identifier, VolumeData> getSoundOverrides() {
        Map<Identifier, VolumeData> overrides = new HashMap<>();
        for (var entry : workingSounds.entrySet()) {
            if (entry.getValue().isModified()) {
                overrides.put(entry.getKey(), entry.getValue());
            }
        }
        return overrides;
    }

    private void loadSoundOptions() {
        if (volumeListWidget == null) return;
        volumeListWidget.clearEntries();
        volumeListWidget.setScrollAmount(0);
        String search = searchField != null ? searchField.getValue().toLowerCase() : "";

        workingSounds.values().stream()
            .filter(v -> v.inFilter(search, showModifiedOnly))
            .sorted(Comparator.comparing(v -> v.getId().toString()))
            .forEach(v -> volumeListWidget.addWidgetEntry(new VolumeWidgetEntry(v, screen, options)));
    }
}
