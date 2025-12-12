package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.config.VolumeConfig;
import com.bvengo.soundcontroller.gui.buttons.ToggleButtonWidget;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import java.util.Comparator;

import static com.bvengo.soundcontroller.Translations.SOUND_SCREEN_TITLE;
import static com.bvengo.soundcontroller.Translations.SEARCH_FIELD_TITLE;
import static com.bvengo.soundcontroller.Translations.SEARCH_FIELD_PLACEHOLDER;
import static com.bvengo.soundcontroller.Translations.FILTER_BUTTON_TOOLTIP;
import static com.bvengo.soundcontroller.Translations.SUBTITLES_BUTTON_TOOLTIP;

/**
 * Screen that displays all sound options.
 */
public class AllSoundOptionsScreen extends OptionsSubScreen {
    VolumeConfig config = VolumeConfig.getInstance();

    protected final Screen parent;

    private VolumeListWidget volumeListWidget;
    private EditBox searchField;

    private ToggleButtonWidget filterButton;
    private ToggleButtonWidget subtitlesButton;

    private boolean showModifiedOnly = false;

    public AllSoundOptionsScreen(Screen parent, Options options) {
        super(parent, options, SOUND_SCREEN_TITLE);
        this.parent = parent;

        // Increase header height to make room for search field. Includes 8 extra padding below.
        layout.setHeaderHeight(layout.getHeaderHeight() + 28);
    }

    @Override
    protected void init() {
        addSearchField();
        addFilterButton();
        addSubtitlesButton();
        addVolumeList();
        addDoneButton();

        this.setInitialFocus(this.searchField);
    }

    @Override
    protected void addOptions() {}

    private void addSearchField() {
        // Add search field - x, y, width, height
        this.searchField = new EditBox(this.font, 80, 35, this.width - 167, 20,
                SEARCH_FIELD_PLACEHOLDER);
        this.searchField.setResponder(serverName -> this.loadOptions());
        this.addWidget(this.searchField);
    }

    private void addFilterButton() {
        // Add filter button - x, y, width, height, textures, pressAction
        this.filterButton = new ToggleButtonWidget("filter",
                this.searchField.getRight() + 8, 35, 20, 20,
                (button) -> {
                    showModifiedOnly = !showModifiedOnly;
                    loadOptions();
                },
                false
        );

        this.filterButton.setTooltip(Tooltip.create(FILTER_BUTTON_TOOLTIP));
        this.addRenderableWidget(this.filterButton);
    }

    private void addSubtitlesButton() {
        // Add subtitles button - x, y, width, height, textures, pressAction
        this.subtitlesButton = new ToggleButtonWidget("subtitles",
                this.filterButton.getRight() + 4, 35, 20, 20,
                (button) -> {
                    config.toggleSubtitles();
                },
                config.areSubtitlesEnabled());

        this.subtitlesButton.setTooltip(Tooltip.create(SUBTITLES_BUTTON_TOOLTIP));
        this.addRenderableWidget(this.subtitlesButton);
    }

    private void addVolumeList() {
        this.volumeListWidget = new VolumeListWidget(this.minecraft, this.width, this.searchField.getBottom() + 32, this);
        loadOptions();
        this.addRenderableWidget(this.volumeListWidget);
    }

    private void addDoneButton() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    private void loadOptions() {
        this.volumeListWidget.clearEntries();
        this.volumeListWidget.setScrollAmount(0);

        String search = this.searchField.getValue().toLowerCase();

        // Update all buttons
        config.getVolumes().values().stream()
            .filter(volumeData -> volumeData.inFilter(search, showModifiedOnly))
            .sorted(Comparator.comparing(v -> v.getId().toString()))
            .forEach(volumeData -> {
                VolumeWidgetEntry volumeEntry = new VolumeWidgetEntry(volumeData, this, this.options);
                this.volumeListWidget.addWidgetEntry(volumeEntry);
            });
    }

    @Override
    public void removed() {
        config.save();
        this.searchField.setValue("");  // Clear search field
    }

    @Override
    public void resize(int width, int height) {
        // Cache search before clearing
        String search = this.searchField.getValue();

        this.width = width;
        this.height = height;

        this.clearWidgets();
        this.clearFocus();
        this.init();

        this.searchField.setValue(search);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawString(this.font, SEARCH_FIELD_TITLE, 32, 40, 0xA0A0A0);
        this.searchField.render(context, mouseX, mouseY, delta);
    }
}
