package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.Utils;
import com.bvengo.soundcontroller.VolumeData;
import com.bvengo.soundcontroller.gui.buttons.AudioButtonWidget;
import com.bvengo.soundcontroller.gui.buttons.TriggerButtonWidget;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList.Entry;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

/**
 * A widget entry allowing control of a single volume. Should be used in a {@link VolumeListWidget}.
 */
public class VolumeWidgetEntry extends Entry<VolumeWidgetEntry> {
    private final VolumeData volumeData;
    private final SoundManager soundManager;
    private final Screen screen;
    private final Options options;

    private static final int sliderWidth = 310;
    private static final int buttonSize = 20;
    private static final int paddingAfterSearch = 8;
    private static final int paddingBetweenButtons = 4;
    public static final int totalWidth = sliderWidth + buttonSize * 2 + paddingAfterSearch + paddingBetweenButtons;

    public OptionInstance<Double> volumeOption;

    public AbstractWidget volumeSlider;
    public TriggerButtonWidget playSoundButton;
    public TriggerButtonWidget resetButton;

    private static final float MAX_VOLUME = 2.0f;

    public VolumeWidgetEntry(VolumeData volumeData, Screen screen, Options options) {
        this.volumeData = volumeData;
        this.screen = screen;
        this.options = options;
        this.soundManager = Minecraft.getInstance().getSoundManager();

        init();
    }

    private int getPercentageValue(double value) {
        return (int) Math.round(value * 100);
    }

    private float getVolumeFromSlider(double value) {
        // 1 -> MAX_VALUE
        // Requires multiplying by 100 to round the value to 2dp.
        return Math.round(value * MAX_VOLUME * 100) / 100.0f;
    }

    private void addSlider() {
        // Volume slider (options)
        this.volumeOption = new OptionInstance<>(
                volumeData.getId().toString(),
                OptionInstance.noTooltip(),
                (prefix, value) -> {
                    // Use volumeData instead of value, noting that it gets updated immediately by the slider as well.
                    // This allows us to list the actual volume (which may be over/under set), not whatever the slider
                    // is clamped to.
                    int volume = getPercentageValue(volumeData.getVolume());

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
                Math.clamp(volumeData.getVolume().doubleValue() / MAX_VOLUME, 0.0, 1.0),
                value -> {
                    volumeData.setVolume(getVolumeFromSlider(value));
                    Utils.updateExistingSounds();
                });

        // Volume slider (widget, created from options)
        this.volumeSlider = volumeOption.createButton(options, 0, 0, sliderWidth);
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

        this.resetButton.setTooltip(Tooltip.create(Translations.RESET_BUTTON_TOOLTIP));
    }

    private void init() {
        addSlider();
        addPlayButton();
        addResetButton();
    }

    @Override
    public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int leftSide = (this.screen.width - totalWidth) / 2;

        this.volumeSlider.setPosition(leftSide, getY());
        this.volumeSlider.render(context, mouseX, mouseY, tickDelta);

        this.playSoundButton.setPosition(volumeSlider.getRight() + paddingAfterSearch, getY());
        this.playSoundButton.render(context, mouseX, mouseY, tickDelta);

        this.resetButton.setPosition(playSoundButton.getRight() + paddingBetweenButtons, getY());
        this.resetButton.render(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return List.of(volumeSlider, playSoundButton, resetButton);
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return List.of(volumeSlider, playSoundButton, resetButton);
    }
}
