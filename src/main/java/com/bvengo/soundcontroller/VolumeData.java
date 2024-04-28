package com.bvengo.soundcontroller;

import com.bvengo.soundcontroller.mixin.SoundSystemAccessor;

import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class VolumeData {
    private final String soundId;
    private Float volume;
//    private boolean shouldOverride;

    public VolumeData(String id, float volume, boolean shouldOverride) {
        this.soundId = id;
        this.volume = volume;
//        this.shouldOverride = shouldOverride;
    }

    public VolumeData(String id) {
        this(id, 1.0f, false);
    }

    public String getId() {
        return soundId;
    }

    public Float getVolume() {
        return volume;
    }

    public Float getAdjustedVolume(SoundInstance sound, SoundSystemAccessor soundSystem) {
        float adjustment = this.getVolume();
        float soundVolume = sound.getVolume();

        float categoryVolume = soundSystem.invokeGetSoundVolume(sound.getCategory());
        adjustment *= categoryVolume;

        return MathHelper.clamp(adjustment * soundVolume, 0.0F, 1.0F);
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

//    public boolean getOverride() {
//        return shouldOverride;
//    }

//    public void setOverride(boolean shouldOverride) {
//        this.shouldOverride = shouldOverride;
//    }

    public boolean isModified() {
//        return this.volume != 1.0f || this.shouldOverride;
        return this.volume != 1.0f;
    }

    public boolean inFilter(String search, boolean showModifiedOnly) {
        return (this.getId().toLowerCase().contains(search) &&
                (!showModifiedOnly || this.isModified()));
    }

    public void playSound(SoundManager soundManager) {
        SoundEvent soundEvent = SoundEvent.of(new Identifier(soundId));
        soundManager.play(PositionedSoundInstance.master(soundEvent, 1.0f));
    }
}