package com.bvengo.soundcontroller.gui.regions;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.VolumeData;
import com.bvengo.soundcontroller.gui.buttons.ToggleButtonWidget;
import com.bvengo.soundcontroller.gui.buttons.TriggerButtonWidget;
import com.bvengo.soundcontroller.gui.components.VolumeListWidget;
import com.bvengo.soundcontroller.gui.components.VolumeWidgetEntry;
import com.bvengo.soundcontroller.gui.presets.PresetPickerScreen;
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
    private final TriggerButtonWidget loadPresetButton;
    private final VolumeListWidget volumeListWidget;

    private final HashMap<Identifier, VolumeData> workingSounds;
    private boolean showModifiedOnly = false;

    private static final int ROW_TOP_PADDING = 8;
    private static final int LEFT_PADDING = 8;
    private static final int LABEL_SEARCH_GAP = 4;
    private static final int BUTTON_GAP = 4;
    private static final int BUTTON_SIZE = 20;
    private static final int RIGHT_PADDING = 8;
    private static final int LIST_TOP_GAP = 8;

    public RegionSoundsTab(Screen screen, Options options, HashMap<Identifier, VolumeData> workingSounds) {
        this.screen = screen;
        this.options = options;
        this.workingSounds = workingSounds;

        Font font = Minecraft.getInstance().font;

        searchLabel = new StringWidget(SEARCH_FIELD_TITLE, font);

        searchField = new EditBox(font, 0, 0, 200, BUTTON_SIZE, SEARCH_FIELD_PLACEHOLDER);
        searchField.setResponder(s -> loadSoundOptions());

        filterButton = new ToggleButtonWidget("filter", 0, 0, BUTTON_SIZE, BUTTON_SIZE, b -> {
            showModifiedOnly = !showModifiedOnly;
            loadSoundOptions();
        }, false);
        filterButton.setTooltip(Tooltip.create(FILTER_BUTTON_TOOLTIP));

        loadPresetButton = new TriggerButtonWidget("preset", 0, 0, BUTTON_SIZE, BUTTON_SIZE,
            b -> Minecraft.getInstance().setScreenAndShow(new PresetPickerScreen(screen, preset -> {
                for (var entry : preset.getSounds().entrySet()) {
                    VolumeData vd = workingSounds.get(entry.getKey());
                    if (vd != null) vd.setVolume(entry.getValue());
                }
            }))
        );
        loadPresetButton.setTooltip(Tooltip.create(Translations.translatableOf("preset.load")));

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
        consumer.accept(loadPresetButton);
        consumer.accept(volumeListWidget);
    }

    @Override
    public void doLayout(ScreenRectangle rect) {
        Font font = Minecraft.getInstance().font;
        int top = rect.top() + ROW_TOP_PADDING;
        int left = rect.left() + LEFT_PADDING;

        int labelWidth = font.width(SEARCH_FIELD_TITLE);
        searchLabel.setPosition(left, top + (BUTTON_SIZE - searchLabel.getHeight()) / 2);

        int searchFieldX = left + labelWidth + LABEL_SEARCH_GAP;
        int buttonsWidth = BUTTON_SIZE * 2 + BUTTON_GAP * 2 + RIGHT_PADDING;
        int searchFieldWidth = Math.max(0, rect.left() + rect.width() - searchFieldX - buttonsWidth);

        searchField.setPosition(searchFieldX, top);
        searchField.setWidth(searchFieldWidth);
        filterButton.setPosition(searchField.getRight() + BUTTON_GAP, top);
        loadPresetButton.setPosition(filterButton.getRight() + BUTTON_GAP, top);

        int listTop = searchField.getBottom() + LIST_TOP_GAP;
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
