package com.bvengo.soundcontroller;

import com.bvengo.soundcontroller.mixin.SoundSystemAccessor;

import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class VolumeData {
    public static final Float DEFAULT_VOLUME = 1.0f;
    public static final Float MAX_VOLUME = 2.0f;

    private final String soundId;
    private Float volume;

    public VolumeData(String id, float volume) {
        this.soundId = id;
        this.volume = MathHelper.clamp(volume, 0.0f, MAX_VOLUME);
    }

    public VolumeData(String id) {
        this(id, DEFAULT_VOLUME);
    }

    public boolean isValid() {
        return (this.soundId != null && Identifier.tryParse(this.soundId) != null);
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

        return MathHelper.clamp(adjustment * soundVolume, 0.0F, MAX_VOLUME);
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public boolean isModified() {
        return !this.volume.equals(DEFAULT_VOLUME);
    }

    public boolean inFilter(String search, boolean showModifiedOnly) {
        return (this.getId().toLowerCase().contains(search) &&
                (!showModifiedOnly || this.isModified()));
    }

    public void playSound(SoundManager soundManager) {
        SoundEvent soundEvent = SoundEvent.of(Identifier.of(soundId));
        soundManager.play(PositionedSoundInstance.master(soundEvent, 1.0f));
    }
}