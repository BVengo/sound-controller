package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.VolumeData;
import com.bvengo.soundcontroller.gui.buttons.AudioButtonWidget;
import com.bvengo.soundcontroller.gui.buttons.TriggerButtonWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList.Entry;
import net.minecraft.client.gui.components.ResettableOptionWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;

import java.util.List;

/**
 * A widget entry allowing control of a single volume. Should be used in a {@link VolumeListWidget}.
 */
public class VolumeWidgetEntry extends Entry<VolumeWidgetEntry> {
    private final VolumeData volumeData;
    private final OptionsSoundManager optionsSoundManager;
    private final Options gameOptions;

    private static final int BUTTON_SIZE = 20;
    private static final int PADDING_AFTER_SLIDER = 6;
    private static final int PADDING_BETWEEN_BUTTONS = 4;
    private static final int NON_VARIABLE_END_WIDTH =
            BUTTON_SIZE * 2 + PADDING_AFTER_SLIDER + PADDING_BETWEEN_BUTTONS;

    private static final float MAX_VOLUME = 2.0f;

    private final OptionInstance<Double> volumeOption;

    private final AbstractWidget volumeSlider;
    private final TriggerButtonWidget playSoundButton;
    private final TriggerButtonWidget resetButton;

    public VolumeWidgetEntry(VolumeData volumeData, Options gameOptions, OptionsSoundManager optionsSoundManager) {
        this.volumeData = volumeData;
        this.gameOptions = gameOptions;
        this.optionsSoundManager = optionsSoundManager;

        // Volume slider (options)
        this.volumeOption = new OptionInstance<>(
                this.volumeData.getId().toString(),
                OptionInstance.noTooltip(),
                (prefix, value) -> {
                    // Use volumeData instead of value, noting that it gets updated immediately by the slider as well.
                    // This allows us to list the actual volume (which may be over/under set), not whatever the slider
                    // is clamped to.
                    int volume = this.getPercentageValue(this.volumeData.getVolume());

                    if (volume == 0) {
                        return Component.translatable("options.generic_value", prefix, CommonComponents.OPTION_OFF);
                    }

                    if (volume > MAX_VOLUME * 100 || volume < 0) {
                        // Make the value red if it's over the max
                        return Component.translatable("options.generic_value",
                                prefix,
                                Component.literal(volume + "%").withStyle(style -> style.withColor(0xFF5555))
                        );
                    }

                    return Component.translatable("options.percent_value", prefix, volume);
                },
                OptionInstance.UnitDouble.INSTANCE,
                this.calcSliderValue(this.volumeData.getVolume()),
                value -> {
                    this.volumeData.setVolume(this.getVolumeFromSlider(value));
                    updateExistingSounds();
                });

        // Volume slider (widget, created from options)
        this.volumeSlider = this.volumeOption.createButton(this.gameOptions, 0, 0, 1);

        this.playSoundButton = new AudioButtonWidget(0, 0, BUTTON_SIZE, BUTTON_SIZE, this.optionsSoundManager, this.volumeData);

        this.resetButton = new TriggerButtonWidget("reset", 0, 0, BUTTON_SIZE, BUTTON_SIZE,
                (button) -> {
                    this.volumeOption.set(this.calcSliderValue(VolumeData.DEFAULT_VOLUME));
                    ((ResettableOptionWidget) this.volumeSlider).resetValue();
                });
        this.resetButton.setTooltip(Tooltip.create(Translations.RESET_BUTTON_TOOLTIP));
    }

    private double calcSliderValue(float volume) {
        return Math.clamp(volume / MAX_VOLUME, 0.0, 1.0);
    }

    private int getPercentageValue(double value) {
        return (int) Math.round(value * 100);
    }

    private float getVolumeFromSlider(double value) {
        // 1 -> MAX_VALUE
        // Requires multiplying by 100 to round the value to 2dp.
        return Math.round(value * MAX_VOLUME * 100) / 100.0f;
    }

    @Override
    public void setX(int i) {
        super.setX(i);
        this.updateElementPositions();
    }

    @Override
    public void setY(int i) {
        super.setY(i);
        this.updateElementPositions();
    }

    private void updateElementPositions() {
        this.volumeSlider.setPosition(this.getContentX(), this.getY());
        this.playSoundButton.setPosition(this.volumeSlider.getRight() + PADDING_AFTER_SLIDER, this.getY());
        this.resetButton.setPosition(this.playSoundButton.getRight() + PADDING_BETWEEN_BUTTONS, this.getY());
    }

    @Override
    public void setWidth(int i) {
        super.setWidth(i);
        this.updateElementWidths();
    }

    private void updateElementWidths() {
        this.volumeSlider.setWidth(this.getWidth() - NON_VARIABLE_END_WIDTH);
    }

    @Override
    public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        this.volumeSlider.render(context, mouseX, mouseY, tickDelta);
        this.playSoundButton.render(context, mouseX, mouseY, tickDelta);
        this.resetButton.render(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return List.of(this.volumeSlider, this.playSoundButton, this.resetButton);
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return List.of(this.volumeSlider, this.playSoundButton, this.resetButton);
    }

    private static void updateExistingSounds() {
        Minecraft.getInstance().getSoundManager().refreshCategoryVolume(SoundSource.AMBIENT);
    }
}
