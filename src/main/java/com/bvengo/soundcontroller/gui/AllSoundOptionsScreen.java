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
import static com.bvengo.soundcontroller.Constants.SEARCH_FILTER_TOOLTIP;

public class AllSoundOptionsScreen extends GameOptionsScreen {
    VolumeConfig config = VolumeConfig.getInstance();

    private OptionListWidget optionButtons;
    private TextFieldWidget searchField;

    private boolean showModifiedOnly = false;

    public AllSoundOptionsScreen(Screen parent, GameOptions options) {
        super(parent, options, SOUND_SCREEN_TITLE);
    }

    @Override
    protected void init() {
        // Add search field - x, y, width, height
        this.searchField = new TextFieldWidget(this.textRenderer, 80, 35, this.width - 142, 20, SEARCH_FIELD_PLACEHOLDER);
        this.searchField.setChangedListener(serverName -> this.loadOptions());
        this.addSelectableChild(this.searchField);

        // Add filter button - x, y, width, height, textures, pressAction
        FilterButtonWidget toggleButton = new FilterButtonWidget(this.width - 52, 35, 20, 20,
                (button) -> {
                    showModifiedOnly = !showModifiedOnly;
                    loadOptions();
                });

        toggleButton.setTooltip(Tooltip.of(SEARCH_FILTER_TOOLTIP));

        this.addDrawableChild(toggleButton);

        this.optionButtons = new OptionListWidget(this.client, this.width, this.height - 96, 64, 25);
        loadOptions();
        this.addDrawableChild(this.optionButtons);

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close())
                .dimensions(this.width / 2 - 100, this.height - 27, 200, 20).build());

        this.setInitialFocus(this.searchField);
    }

    private void loadOptions() {
        this.optionButtons.children().clear();
        this.optionButtons.setScrollAmount(0.0f);

        // Replace the option buttons to only show options that match the search field
        String search = this.searchField.getText().toLowerCase();

        // Update all buttons
        for (VolumeData volumeData : config.getVolumes().values()) {
            if (!volumeData.inFilter(search, showModifiedOnly)) {
                continue;
            }

            VolumeEntry entry = new VolumeEntry(volumeData);
            this.optionButtons.addSingleOptionEntry(entry.volumeOption);
        }
    }

    @Override
    public void removed() {
        config.save();
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String search = this.searchField.getText();
        this.init(client, width, height);
        this.searchField.setText(search);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, SEARCH_FIELD_TITLE, 32, 40, 0xA0A0A0);
        this.searchField.render(context, mouseX, mouseY, delta);
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(context);
    }
}
