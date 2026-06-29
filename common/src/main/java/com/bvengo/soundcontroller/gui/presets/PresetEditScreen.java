package com.bvengo.soundcontroller.gui.presets;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.VolumeData;
import com.bvengo.soundcontroller.config.PresetConfig;
import com.bvengo.soundcontroller.config.PresetData;
import com.bvengo.soundcontroller.config.VolumeConfig;
import com.bvengo.soundcontroller.gui.buttons.ToggleButtonWidget;
import com.bvengo.soundcontroller.gui.components.VolumeListWidget;
import com.bvengo.soundcontroller.gui.components.VolumeWidgetEntry;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static com.bvengo.soundcontroller.Translations.FILTER_BUTTON_TOOLTIP;
import static com.bvengo.soundcontroller.Translations.SEARCH_FIELD_PLACEHOLDER;
import static com.bvengo.soundcontroller.Translations.SEARCH_FIELD_TITLE;

public class PresetEditScreen extends Screen {
    private final Screen parent;
    private final PresetData preset;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

    private String workingName;
    private HashMap<Identifier, VolumeData> workingSounds;
    private boolean showModifiedOnly = false;

    private EditBox nameField;
    private StringWidget searchLabel;
    private EditBox searchField;
    private ToggleButtonWidget filterButton;
    private VolumeListWidget volumeListWidget;

    public PresetEditScreen(Screen parent, PresetData preset) {
        super(preset == null
            ? Translations.translatableOf("preset.create")
            : Translations.translatableOf("preset.edit.title"));
        this.parent = parent;
        this.preset = preset;
        this.workingName = preset != null ? preset.getName() : "";
    }

    @Override
    protected void init() {
        if (workingSounds == null) {
            workingSounds = new HashMap<>();
            for (Identifier soundId : VolumeConfig.getInstance().getVolumes().keySet()) {
                float vol = preset != null && preset.getSounds().containsKey(soundId)
                    ? preset.getSounds().get(soundId)
                    : VolumeData.DEFAULT_VOLUME;
                workingSounds.put(soundId, new VolumeData(soundId, vol));
            }
        }

        nameField = new EditBox(font, 0, 0, 200, 20, Translations.translatableOf("preset.name.hint"));
        nameField.setMaxLength(64);
        nameField.setValue(workingName);

        searchLabel = new StringWidget(SEARCH_FIELD_TITLE, font);

        searchField = new EditBox(font, 0, 0, 200, 20, SEARCH_FIELD_PLACEHOLDER);
        searchField.setResponder(s -> loadSoundOptions());

        filterButton = new ToggleButtonWidget("filter", 0, 0, 20, 20, b -> {
            showModifiedOnly = !showModifiedOnly;
            loadSoundOptions();
        }, false);
        filterButton.setTooltip(Tooltip.create(FILTER_BUTTON_TOOLTIP));

        volumeListWidget = new VolumeListWidget(minecraft, 200, 100, 0);

        LinearLayout footerButtons = LinearLayout.horizontal().spacing(8);
        footerButtons.addChild(Button.builder(Translations.translatableOf("preset.save"), b -> savePreset()).build());
        footerButtons.addChild(Button.builder(CommonComponents.GUI_CANCEL, b -> onClose()).build());
        layout.addToFooter(footerButtons);

        addRenderableWidget(nameField);
        addRenderableWidget(searchLabel);
        addRenderableWidget(searchField);
        addRenderableWidget(filterButton);
        addRenderableWidget(volumeListWidget);
        layout.visitWidgets(this::addRenderableWidget);

        repositionElements();
        setInitialFocus(nameField);
        loadSoundOptions();
    }

    @Override
    protected void repositionElements() {
        layout.arrangeElements();

        int footerTop = this.height - layout.getFooterHeight();
        int top = 16;

        int nameFieldWidth = Math.min(300, this.width - 32);
        nameField.setWidth(nameFieldWidth);
        nameField.setPosition((this.width - nameFieldWidth) / 2, top);
        top = nameField.getBottom() + 8;

        int labelWidth = font.width(SEARCH_FIELD_TITLE);
        int searchLeft = 32;
        searchLabel.setPosition(searchLeft, top + (20 - searchLabel.getHeight()) / 2);
        searchField.setPosition(searchLeft + labelWidth + 4, top);
        searchField.setWidth(this.width - searchLeft - labelWidth - 4 - 20 - 8 - 8);
        filterButton.setPosition(searchField.getRight() + 4, top);
        top += 28;

        volumeListWidget.updateSizeAndPosition(this.width, footerTop - top, top);
    }

    @Override
    public void resize(int width, int height) {
        workingName = nameField != null ? nameField.getValue() : workingName;
        String search = searchField != null ? searchField.getValue() : "";
        super.resize(width, height);
        if (searchField != null) searchField.setValue(search);
    }

    private void savePreset() {
        String name = nameField.getValue().trim();
        if (name.isEmpty()) return;

        HashMap<Identifier, Float> sounds = new HashMap<>();
        for (Map.Entry<Identifier, VolumeData> entry : workingSounds.entrySet()) {
            if (entry.getValue().isModified()) {
                sounds.put(entry.getKey(), entry.getValue().getVolume());
            }
        }

        PresetConfig config = PresetConfig.getInstance();
        if (preset != null) {
            preset.setName(name);
            preset.getSounds().clear();
            preset.getSounds().putAll(sounds);
        } else {
            config.addPreset(new PresetData(name, sounds));
        }
        config.save();
        onClose();
    }

    @Override
    public void onClose() {
        minecraft.setScreenAndShow(parent);
    }

    private void loadSoundOptions() {
        if (volumeListWidget == null) return;
        volumeListWidget.clearEntries();
        volumeListWidget.setScrollAmount(0);
        String search = searchField != null ? searchField.getValue().toLowerCase() : "";

        workingSounds.values().stream()
            .filter(v -> v.inFilter(search, showModifiedOnly))
            .sorted(Comparator.comparing(v -> v.getId().toString()))
            .forEach(v -> volumeListWidget.addWidgetEntry(new VolumeWidgetEntry(v, this, minecraft.options)));
    }
}
