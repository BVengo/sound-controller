package com.bvengo.soundcontroller.ui;

import com.bvengo.soundcontroller.config.SoundConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import static com.bvengo.soundcontroller.SoundControllerTexts.SOUND_SCREEN_TITLE;
import static com.bvengo.soundcontroller.SoundControllerTexts.SEARCH_FIELD_TITLE;
import static com.bvengo.soundcontroller.SoundControllerTexts.SEARCH_FIELD_PLACEHOLDER;

public class AllSoundOptionsScreen extends GameOptionsScreen {
    SoundConfig config = SoundConfig.getInstance();

    private OptionListWidget optionButtons;
    private TextFieldWidget searchField;

    public AllSoundOptionsScreen(Screen parent, GameOptions options) {
        super(parent, options, SOUND_SCREEN_TITLE);
    }

    @Override
    protected void init() {

        // Add search field - x, y, width, height
        this.searchField = new TextFieldWidget(this.textRenderer, 80, 35, this.width - 112, 20, SEARCH_FIELD_PLACEHOLDER);
        this.searchField.setChangedListener(serverName -> this.loadOptions());
        this.addSelectableChild(this.searchField);

        SoundConfig config = SoundConfig.getInstance();

        // Add options - width, height, top, bottom, itemHeight (decompiled code isn't very helpful here)
        this.optionButtons = new OptionListWidget(this.client, this.width, this.height-32, 64, this.height - 32, 25);
        loadOptions();

        this.addSelectableChild(this.optionButtons);
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> {
            config.save();
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 100, this.height - 27, 200, 20).build());

        this.setInitialFocus(this.searchField);
    }

    private void loadOptions() {
        this.optionButtons.children().clear();
        this.optionButtons.setScrollAmount(0.0f);

        // Replace the option buttons to only show options that match the search field
        String search = this.searchField.getText().toLowerCase();

        for (String id : config.soundVolumes.keySet()) {
            if (!id.toLowerCase().contains(search)) {
                continue;
            }

            SimpleOption<Double> option = new SimpleOption<>(
                    id,
                    SimpleOption.emptyTooltip(),
                    (prefix, value) -> {
                        if (value == 0.0) {
                            return Text.translatable("options.generic_value", prefix, ScreenTexts.OFF);
                        }
                        return Text.translatable("options.percent_value", prefix, (int) (value * 100.0));
                    },
                    SimpleOption.DoubleSliderCallbacks.INSTANCE,
                    1.0,
                    (value) -> {
                        config.setVolumeMultiplier(id, value.floatValue());
                    });

            double initialValue = config.getVolumeMultiplier(id).doubleValue();
            option.setValue(initialValue);

            this.optionButtons.addSingleOptionEntry(option);
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
        this.renderBackground(context);
        optionButtons.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, SEARCH_FIELD_TITLE, 32, 40, 0xA0A0A0);
        this.searchField.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }
}
