package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.VolumeData;
import com.bvengo.soundcontroller.mixin.SoundManagerAccessor;
import com.bvengo.soundcontroller.mixin.SoundSystemAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;

import static com.bvengo.soundcontroller.SoundController.MOD_ID;

public class VolumeEntry {
    private final VolumeData volumeData;

    public SimpleOption<Double> volumeOption;
    public SimpleOption<Boolean> overrideOption;

    public VolumeEntry(VolumeData volumeData) {
        this.volumeData = volumeData;
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

        // Override mode button
        this.overrideOption = SimpleOption.ofBoolean(
                MOD_ID + ".options.override",
                false,
                volumeData::setOverride
        );
        this.overrideOption.setValue(volumeData.getOverride());
    }
}
