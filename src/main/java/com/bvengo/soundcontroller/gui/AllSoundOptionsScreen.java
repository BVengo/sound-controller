package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.config.VolumeConfig;
import com.bvengo.soundcontroller.gui.buttons.ToggleButtonWidget;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;

import java.util.Comparator;

import static com.bvengo.soundcontroller.Translations.FILTER_BUTTON_TOOLTIP;
import static com.bvengo.soundcontroller.Translations.SEARCH_FIELD_PLACEHOLDER;
import static com.bvengo.soundcontroller.Translations.SEARCH_FIELD_TITLE;
import static com.bvengo.soundcontroller.Translations.SOUND_SCREEN_TITLE;
import static com.bvengo.soundcontroller.Translations.SUBTITLES_BUTTON_TOOLTIP;

/**
 * Screen that displays all sound options.
 */
public class AllSoundOptionsScreen extends OptionsSubScreen {
    private final VolumeConfig config = VolumeConfig.getInstance();

    private final EditBox searchField;
    private final ToggleButtonWidget filterButton;
    private final ToggleButtonWidget subtitlesButton;
    private final VolumeListWidget volumeListWidget;

    private boolean showModifiedOnly = false;

    private final OptionsSoundManager optionsSoundManager = new OptionsSoundManager();

    public AllSoundOptionsScreen(Screen parent, Options options) {
        super(parent, options, SOUND_SCREEN_TITLE);

        this.searchField = new EditBox(this.font, 80, 35, 1, 20, SEARCH_FIELD_PLACEHOLDER);
        this.searchField.setResponder(serverName -> this.loadOptions());

        this.filterButton = new ToggleButtonWidget("filter",
                1, 35, 20, 20,
                btn -> {
                    this.showModifiedOnly = !this.showModifiedOnly;
                    this.loadOptions();
                },
                false);
        this.filterButton.setTooltip(Tooltip.create(FILTER_BUTTON_TOOLTIP));

        this.subtitlesButton = new ToggleButtonWidget("subtitles",
                1, 35, 20, 20,
                btn -> this.config.toggleSubtitles(),
                this.config.areSubtitlesEnabled());
        this.subtitlesButton.setTooltip(Tooltip.create(SUBTITLES_BUTTON_TOOLTIP));

        this.volumeListWidget = new VolumeListWidget(this.minecraft);
    }

    @Override
    protected void addContents() {
        this.addRenderableWidget(this.searchField);
        this.addRenderableWidget(this.filterButton);
        this.addRenderableWidget(this.subtitlesButton);

        this.addRenderableWidget(this.volumeListWidget);
        this.loadOptions();

        this.setInitialFocus(this.searchField);
    }

    @Override
    protected void addOptions() {
    }

    private void loadOptions() {
        this.volumeListWidget.clearEntries();
        this.volumeListWidget.setScrollAmount(0);

        String search = this.searchField.getValue().toLowerCase();

        // Update all buttons
        this.config.getVolumes().values().stream()
                .filter(volumeData -> volumeData.inFilter(search, this.showModifiedOnly))
                .sorted(Comparator.comparing(v -> v.getId().toString()))
                .map(volumeData -> new VolumeWidgetEntry(volumeData, this.options, this.optionsSoundManager))
                .forEach(this.volumeListWidget::addWidgetEntry);
    }

    @Override
    public void removed() {
        this.optionsSoundManager.close();
        this.config.saveAsync();
        this.searchField.setValue("");  // Clear search field
    }

    @Override
    protected void repositionElements() {
        super.repositionElements();

        this.searchField.setWidth(this.width - 167);

        this.filterButton.setX(this.searchField.getRight() + 8);
        this.subtitlesButton.setX(this.filterButton.getRight() + 4);

        this.volumeListWidget.updateSizeAndPosition(
                this.width,
                Math.max(this.layout.getContentHeight() - 28, 0),
                this.volumeListWidget.getX(),
                this.layout.getHeaderHeight() + 28);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.textRenderer().accept(33, 41, SEARCH_FIELD_TITLE);
    }
}