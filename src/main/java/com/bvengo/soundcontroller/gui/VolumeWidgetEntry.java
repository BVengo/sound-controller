package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.Utils;
import com.bvengo.soundcontroller.VolumeData;
import com.bvengo.soundcontroller.gui.buttons.AudioButtonWidget;
import com.bvengo.soundcontroller.gui.buttons.TriggerButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget.Entry;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import java.util.List;

/**
 * A widget entry allowing control of a single volume. Should be used in a {@link VolumeListWidget}.
 */
public class VolumeWidgetEntry extends Entry<VolumeWidgetEntry> {
    private final VolumeData volumeData;
    private final SoundManager soundManager;
    private final Screen screen;
    private final GameOptions gameOptions;

    private static final int sliderWidth = 310;
    private static final int buttonSize = 20;
    private static final int paddingAfterSearch = 8;
    private static final int paddingBetweenButtons = 4;
    public static final int totalWidth = sliderWidth + buttonSize * 2 + paddingAfterSearch + paddingBetweenButtons;

    public SimpleOption<Double> volumeOption;

    public ClickableWidget volumeSlider;
    public TriggerButtonWidget playSoundButton;
    public TriggerButtonWidget resetButton;

    public VolumeWidgetEntry(VolumeData volumeData, Screen screen, GameOptions gameOptions) {
        this.volumeData = volumeData;
        this.screen = screen;
        this.gameOptions = gameOptions;
        this.soundManager = MinecraftClient.getInstance().getSoundManager();

        init();
    }

    private int getPercentageValue(double value) {
        return (int)Math.round(value * VolumeData.MAX_VOLUME * 100);
    }

    private float getVolumeFromSlider(double value) {
        // 1 -> MAX_VALUE
        // Requires multiplying by 100 to round the value to 2dp.
        return Math.round(value * VolumeData.MAX_VOLUME * 100) / 100.0f;
    }

    private void addSlider() {
        // Volume slider (options)
        this.volumeOption = new SimpleOption<>(
                volumeData.getId().toString(),
                SimpleOption.emptyTooltip(),
                (prefix, value) -> {
                    if (value < 0.01) {
                        return Text.translatable("options.generic_value", prefix, ScreenTexts.OFF);
                    }
                    return Text.translatable("options.percent_value", prefix, getPercentageValue(value));
                },
                SimpleOption.DoubleSliderCallbacks.INSTANCE,
                0.5,
                value -> {
                    volumeData.setVolume(getVolumeFromSlider(value));
                    Utils.updateExistingSounds();
                });
        this.volumeOption.setValue(volumeData.getVolume().doubleValue() / VolumeData.MAX_VOLUME);

        // Volume slider (widget, created from options)
        this.volumeSlider = volumeOption.createWidget(gameOptions, 0, 0, sliderWidth);
    }

    private void addPlayButton() {
        this.playSoundButton = new AudioButtonWidget(0, 0, buttonSize, buttonSize, soundManager, volumeData);
    }

    private void addResetButton() {

        this.resetButton = new TriggerButtonWidget("reset", 0, 0, buttonSize, buttonSize,
                (button) -> {
                    volumeData.setVolume(VolumeData.DEFAULT_VOLUME);
                    this.addSlider();  // Update the slider to reflect the new volume
                    Utils.updateExistingSounds();
                });

        this.resetButton.setTooltip(Tooltip.of(Translations.RESET_BUTTON_TOOLTIP));
    }

    private void init() {
        addSlider();
        addPlayButton();
        addResetButton();
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int leftSide = (this.screen.width - totalWidth) / 2;

        this.volumeSlider.setPosition(leftSide, y);
        this.volumeSlider.render(context, mouseX, mouseY, tickDelta);
        
        this.playSoundButton.setPosition(volumeSlider.getRight() + paddingAfterSearch, y);
        this.playSoundButton.render(context, mouseX, mouseY, tickDelta);

        this.resetButton.setPosition(playSoundButton.getRight() + paddingBetweenButtons, y);
        this.resetButton.render(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public List<? extends Element> children() {
        return List.of(volumeSlider, playSoundButton, resetButton);
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return List.of(volumeSlider, playSoundButton, resetButton);
    }
}
