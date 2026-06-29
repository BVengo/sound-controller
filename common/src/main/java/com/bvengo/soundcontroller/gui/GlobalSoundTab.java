package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.VolumeData;
import com.bvengo.soundcontroller.config.VolumeConfig;
import com.bvengo.soundcontroller.gui.buttons.ToggleButtonWidget;
import com.bvengo.soundcontroller.gui.components.VolumeListWidget;
import com.bvengo.soundcontroller.gui.components.VolumeWidgetEntry;
import com.bvengo.soundcontroller.gui.presets.PresetPickerScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

import java.util.Comparator;
import java.util.function.Consumer;

import static com.bvengo.soundcontroller.Translations.FILTER_BUTTON_TOOLTIP;
import static com.bvengo.soundcontroller.Translations.SEARCH_FIELD_PLACEHOLDER;
import static com.bvengo.soundcontroller.Translations.SEARCH_FIELD_TITLE;
import static com.bvengo.soundcontroller.Translations.SUBTITLES_BUTTON_TOOLTIP;

public class GlobalSoundTab implements Tab {
    private final VolumeConfig config = VolumeConfig.getInstance();
    private final AllSoundOptionsScreen screen;
    private final Options options;
    private final Layout layout = LinearLayout.vertical();

    private final StringWidget searchLabel;
    private final EditBox searchField;
    private final ToggleButtonWidget filterButton;
    private final ToggleButtonWidget subtitlesButton;
    private final Button loadPresetButton;
    private final VolumeListWidget volumeListWidget;

    private final Runnable onSelected;
    private boolean showModifiedOnly = false;

    public GlobalSoundTab(AllSoundOptionsScreen screen, Options options, Runnable onSelected) {
        this.screen = screen;
        this.options = options;
        this.onSelected = onSelected;

        Font font = Minecraft.getInstance().font;

        this.searchLabel = new StringWidget(SEARCH_FIELD_TITLE, font);

        this.searchField = new EditBox(font, 0, 0, 200, 20, SEARCH_FIELD_PLACEHOLDER);
        this.searchField.setResponder(value -> loadOptions());

        this.filterButton = new ToggleButtonWidget("filter", 0, 0, 20, 20,
            button -> {
                showModifiedOnly = !showModifiedOnly;
                loadOptions();
            },
            false
        );
        this.filterButton.setTooltip(Tooltip.create(FILTER_BUTTON_TOOLTIP));

        this.subtitlesButton = new ToggleButtonWidget("subtitles", 0, 0, 20, 20,
            button -> config.toggleSubtitles(),
            config.areSubtitlesEnabled()
        );
        this.subtitlesButton.setTooltip(Tooltip.create(SUBTITLES_BUTTON_TOOLTIP));

        this.loadPresetButton = Button.builder(Translations.translatableOf("preset.load"),
            b -> Minecraft.getInstance().setScreenAndShow(new PresetPickerScreen(screen, preset -> {
                for (var entry : preset.getSounds().entrySet()) {
                    VolumeData vd = config.getVolumes().get(entry.getKey());
                    if (vd != null) vd.setVolume(entry.getValue());
                }
            }))
        ).size(90, 20).build();

        this.volumeListWidget = new VolumeListWidget(Minecraft.getInstance(), 200, 100, 0);
        loadOptions();
    }

    @Override
    public Component getTabTitle() {
        return Translations.translatableOf("tab.global");
    }

    @Override
    public Component getTabExtraNarration() {
        return Component.empty();
    }

    @Override
    public void visitChildren(Consumer<AbstractWidget> consumer) {
        consumer.accept(this.searchLabel);
        consumer.accept(this.searchField);
        consumer.accept(this.filterButton);
        consumer.accept(this.subtitlesButton);
        consumer.accept(this.loadPresetButton);
        consumer.accept(this.volumeListWidget);
    }

    @Override
    public void doLayout(ScreenRectangle rect) {
        this.onSelected.run();
        int top = rect.top() + 8;
        int searchFieldX = rect.left() + 80;
        int searchFieldWidth = rect.width() - 167;

        this.searchField.setPosition(searchFieldX, top);
        this.searchField.setWidth(searchFieldWidth);

        this.filterButton.setPosition(this.searchField.getRight() + 8, top);
        this.subtitlesButton.setPosition(this.filterButton.getRight() + 4, top);
        this.loadPresetButton.setPosition(this.subtitlesButton.getRight() + 4, top);

        int labelY = top + (this.searchField.getHeight() - this.searchLabel.getHeight()) / 2;
        this.searchLabel.setPosition(rect.left() + 32, labelY);

        int listTop = this.searchField.getBottom() + 8;
        int listHeight = rect.height() - (listTop - rect.top());
        this.volumeListWidget.updateSizeAndPosition(rect.width(), listHeight, listTop);
    }

    @Override
    public Layout getLayout() {
        return this.layout;
    }

    public EditBox getSearchField() {
        return this.searchField;
    }

    public String getSearchValue() {
        return this.searchField.getValue();
    }

    public void setSearchValue(String value) {
        this.searchField.setValue(value);
    }

    private void loadOptions() {
        this.volumeListWidget.clearEntries();
        this.volumeListWidget.setScrollAmount(0);

        String search = this.searchField.getValue().toLowerCase();

        config.getVolumes().values().stream()
            .filter(volumeData -> volumeData.inFilter(search, showModifiedOnly))
            .sorted(Comparator.comparing(v -> v.getId().toString()))
            .forEach(volumeData -> {
                VolumeWidgetEntry entry = new VolumeWidgetEntry(volumeData, this.screen, this.options);
                this.volumeListWidget.addWidgetEntry(entry);
            });
    }
}
