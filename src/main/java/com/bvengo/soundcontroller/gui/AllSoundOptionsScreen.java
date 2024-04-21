package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.VolumeData;
import com.bvengo.soundcontroller.config.VolumeConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.option.GameOptions;
import net.minecraft.screen.ScreenTexts;

import static com.bvengo.soundcontroller.Constants.SOUND_SCREEN_TITLE;
import static com.bvengo.soundcontroller.Constants.SEARCH_FIELD_TITLE;
import static com.bvengo.soundcontroller.Constants.SEARCH_FIELD_PLACEHOLDER;
import static com.bvengo.soundcontroller.Constants.FILTER_BUTTON_TOOLTIP;
import static com.bvengo.soundcontroller.Constants.SUBTITLES_BUTTON_TOOLTIP;

public class AllSoundOptionsScreen extends GameOptionsScreen {
    VolumeConfig config = VolumeConfig.getInstance();

    protected final Screen parent;

    private VolumeListWidget volumeListWidget;
    private TextFieldWidget searchField;

    private boolean showModifiedOnly = false;

    public AllSoundOptionsScreen(Screen parent, GameOptions options) {
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

    private void addSearchField() {
        // Add search field - x, y, width, height
        this.searchField = new TextFieldWidget(this.textRenderer, 80, 35, this.width - 167, 20,
                SEARCH_FIELD_PLACEHOLDER);
        this.searchField.setChangedListener(serverName -> this.loadOptions());
        this.addSelectableChild(this.searchField);
    }

    private void addFilterButton() {
        // Add filter button - x, y, width, height, textures, pressAction
        HoverableButtonWidget filterButton = new HoverableButtonWidget("filter",
                this.width - 77, 35, 20, 20,
                (button) -> {
                    showModifiedOnly = !showModifiedOnly;
                    loadOptions();
                });

        filterButton.setTooltip(Tooltip.of(FILTER_BUTTON_TOOLTIP));

        this.addDrawableChild(filterButton);
    }

    private void addSubtitlesButton() {
        // Add subtitles button - x, y, width, height, textures, pressAction
        HoverableButtonWidget subtitlesButton = new HoverableButtonWidget("subtitles",
                this.width - 52, 35, 20, 20,
                (button) -> {
                    config.toggleSubtitles();
                });

        subtitlesButton.setTooltip(Tooltip.of(SUBTITLES_BUTTON_TOOLTIP));
        subtitlesButton.isToggled = config.areSubtitlesEnabled();

        this.addDrawableChild(subtitlesButton);
    }

    private void addVolumeList() {
        this.volumeListWidget = new VolumeListWidget(this.client, this.width, this.height - 96, this);
        loadOptions();
        this.addDrawableChild(this.volumeListWidget);
    }

    private void addDoneButton() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close())
                .dimensions(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    private void loadOptions() {
        this.volumeListWidget.children().clear();
        this.volumeListWidget.setScrollAmount(0.0f);

        String search = this.searchField.getText().toLowerCase();

        // Update all buttons
        for (VolumeData volumeData : config.getVolumes().values()) {
            if (!volumeData.inFilter(search, showModifiedOnly)) {
                continue;
            }

            VolumeWidgetEntry volumeEntry = new VolumeWidgetEntry(volumeData, this, this.gameOptions);
            this.volumeListWidget.addWidgetEntry(volumeEntry);
        }
    }

    @Override
    public void removed() {
        config.save();
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
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, SEARCH_FIELD_TITLE, 32, 40, 0xA0A0A0);
        this.searchField.render(context, mouseX, mouseY, delta);
    }
}
