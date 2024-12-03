package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.VolumeData;
import com.bvengo.soundcontroller.mixin.SoundManagerAccessor;
import com.bvengo.soundcontroller.mixin.SoundSystemAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget.Entry;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import java.util.List;

public class VolumeWidgetEntry extends Entry<VolumeWidgetEntry> {
    private final VolumeData volumeData;
    private final SoundManager soundManager;
    private final Screen screen;
    private final GameOptions gameOptions;

    private static final int sliderWidth = 310;
    private static final int buttonWidth = 20;
    private static final int padding = 8;
    public static final int totalWidth = sliderWidth + buttonWidth + padding;

    public SimpleOption<Double> volumeOption;

    public ClickableWidget volumeSlider;
    public TriggerButtonWidget playSoundButton;

    public VolumeWidgetEntry(VolumeData volumeData, Screen screen, GameOptions gameOptions) {
        this.volumeData = volumeData;
        this.screen = screen;
        this.gameOptions = gameOptions;
        this.soundManager = MinecraftClient.getInstance().getSoundManager();

        init();
    }

    private void init() {
        // Volume slider
        this.volumeOption = new SimpleOption<>(
                volumeData.getId(),
                SimpleOption.emptyTooltip(),
                (prefix, value) -> {
                    if (Math.round(value.floatValue() * 100f) < 1) {
                        return Text.translatable("options.generic_value", prefix, ScreenTexts.OFF);
                    }
                    return Text.translatable("options.percent_value", prefix, Math.round(value.floatValue() * 100f));
                },
                SimpleOption.DoubleSliderCallbacks.INSTANCE,
                1.0,
                value -> {
                    volumeData.setVolume(Math.round(value.floatValue() * 100f) / 100f);
                    SoundSystemAccessor soundSystem = (SoundSystemAccessor) ((SoundManagerAccessor) MinecraftClient.getInstance().getSoundManager()).getSoundSystem();
                    // Dummy values to trigger updates for all sounds, Can use anything but MASTER
                    soundSystem.invokeUpdateSoundVolume(SoundCategory.AMBIENT, 1.0f);
                });
        this.volumeOption.setValue(volumeData.getVolume().doubleValue());
        this.volumeSlider = volumeOption.createWidget(gameOptions, 0, 0, sliderWidth);
        
        this.playSoundButton = new AudioButtonWidget(0, 0, buttonWidth, buttonWidth,
            (button) -> {
                volumeData.playSound(soundManager);
            });
        }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int leftSide = this.screen.width / 2 - totalWidth / 2;

        this.volumeSlider.setPosition(leftSide, y);
        this.volumeSlider.render(context, mouseX, mouseY, tickDelta);
        
        this.playSoundButton.setPosition(volumeSlider.getRight() + padding, y);
        this.playSoundButton.render(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public List<? extends Element> children() {
        return List.of(volumeSlider, playSoundButton);
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return List.of(volumeSlider, playSoundButton);
    }
}
