package com.bvengo.soundcontroller.ui;

import com.bvengo.soundcontroller.Utils;
import com.bvengo.soundcontroller.config.SoundConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class AllSoundOptionsScreen extends GameOptionsScreen {
    private OptionListWidget optionButtons;

    public AllSoundOptionsScreen(Screen parent, GameOptions options) {
        super(parent, options, Utils.soundScreenTitle);
    }

    @Override
    protected void init() {
        SoundConfig config = SoundConfig.getInstance();

        this.optionButtons = new OptionListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);

        for (String id : config.soundVolumes.keySet()) {
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
                        SoundConfig.getInstance().setVolumeMultiplier(id, value.floatValue());
                    });

            double initialValue = config.getVolumeMultiplier(id).doubleValue();
            option.setValue(initialValue);

            this.optionButtons.addSingleOptionEntry(option);
        }

        this.addSelectableChild(this.optionButtons);
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> {
            config.save();
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.render(context, this.optionButtons, mouseX, mouseY, delta);
    }
}
