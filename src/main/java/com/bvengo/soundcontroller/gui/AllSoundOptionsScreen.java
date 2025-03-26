package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.config.VolumeConfig;
import com.bvengo.soundcontroller.gui.buttons.ToggleButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.option.GameOptions;
import net.minecraft.screen.ScreenTexts;

import java.util.Comparator;

import static com.bvengo.soundcontroller.Translations.SOUND_SCREEN_TITLE;
import static com.bvengo.soundcontroller.Translations.SEARCH_FIELD_TITLE;
import static com.bvengo.soundcontroller.Translations.SEARCH_FIELD_PLACEHOLDER;
import static com.bvengo.soundcontroller.Translations.FILTER_BUTTON_TOOLTIP;
import static com.bvengo.soundcontroller.Translations.SUBTITLES_BUTTON_TOOLTIP;

/**
 * Screen that displays all sound options.
 */
public class AllSoundOptionsScreen extends GameOptionsScreen {
    VolumeConfig config = VolumeConfig.getInstance();

    protected final Screen parent;

    private VolumeListWidget volumeListWidget;
    private TextFieldWidget searchField;

    private ToggleButtonWidget filterButton;
    private ToggleButtonWidget subtitlesButton;

    private boolean showModifiedOnly = false;

    public AllSoundOptionsScreen(Screen parent, GameOptions options) {
        super(parent, options, SOUND_SCREEN_TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        addSearchField();
        addFilterButton();
        addSubtitlesButton();
        addVolumeList();
        addDoneButton();

        this.addDrawableChild(this.volumeListWidget);  // For some reason has to be first or the header won't render
        this.addDrawableChild(this.searchField);
        this.addDrawableChild(this.filterButton);
        this.addDrawableChild(this.subtitlesButton);

        this.setInitialFocus(this.searchField);
    }

    private void addSearchField() {
        // Add search field - x, y, width, height
        this.searchField = new TextFieldWidget(this.textRenderer, 80, 35, this.width - 167, 20,
                SEARCH_FIELD_PLACEHOLDER);
        this.searchField.setChangedListener(serverName -> this.loadOptions());
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

        this.filterButton.setTooltip(Tooltip.of(FILTER_BUTTON_TOOLTIP));
    }

    private void addSubtitlesButton() {
        // Add subtitles button - x, y, width, height, textures, pressAction
        this.subtitlesButton = new ToggleButtonWidget("subtitles",
                this.filterButton.getRight() + 4, 35, 20, 20,
                (button) -> {
                    config.toggleSubtitles();
                },
                config.areSubtitlesEnabled());

        this.subtitlesButton.setTooltip(Tooltip.of(SUBTITLES_BUTTON_TOOLTIP));
    }

    private void addVolumeList() {
        int y = this.searchField.getY() + this.searchField.getHeight() + 8;
        int width = this.width;
        int height = this.height - y - 32;

        this.volumeListWidget = new VolumeListWidget(this.client, width, height, y);
        loadOptions();
    }

    private void addDoneButton() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close())
                .dimensions(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    private void loadOptions() {
        this.volumeListWidget.children().clear();
        this.volumeListWidget.setScrollAmount(0);

        String search = this.searchField.getText().toLowerCase();

        // Update all buttons
        config.getVolumes().values().stream()
            .filter(volumeData -> volumeData.inFilter(search, showModifiedOnly))
            .sorted(Comparator.comparing(v -> v.getId().toString()))
            .forEach(volumeData -> {
                VolumeWidgetEntry volumeEntry = new VolumeWidgetEntry(volumeData, this, this.gameOptions);
                this.volumeListWidget.addWidgetEntry(volumeEntry);
            });
    }

    @Override
    public void removed() {
        config.save();
        this.searchField.setText("");  // Clear search field
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        // Cache search before clearing
        String search = this.searchField.getText();

        this.width = width;
        this.height = height;

        this.clearChildren();
        this.blur();
        this.init();

        this.searchField.setText(search);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, SEARCH_FIELD_TITLE, 32, 40, 0xA0A0A0);
        super.render(context, mouseX, mouseY, delta);
    }
}
