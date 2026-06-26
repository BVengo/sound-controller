package com.bvengo.soundcontroller;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class VolumeData {
    public static final Float DEFAULT_VOLUME = 1.0f;

    private final Identifier soundId;
    private Float volume;

    private SimpleSoundInstance currentSoundInstance = null;

    public VolumeData(Identifier id, float volume) {
        this.soundId = id;
        this.volume = volume; // Removed clamping to allow manually setting over / under the slider
    }

    public VolumeData(Identifier id) {
        this(id, DEFAULT_VOLUME);
    }

    public Identifier getId() {
        return soundId;
    }

    public Float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public boolean isModified() {
        return !this.volume.equals(DEFAULT_VOLUME);
    }

    public boolean inFilter(String search, boolean showModifiedOnly) {
        return (this.soundId.toString().toLowerCase().contains(search) &&
                (!showModifiedOnly || this.isModified()));
    }

    public void playSound(SoundManager soundManager) {
        final LocalPlayer player = Minecraft.getInstance().player;
        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(soundId);

        if (player != null) {
            this.currentSoundInstance = new SimpleSoundInstance(
                soundEvent, SoundSource.MASTER,
                this.volume,  // Volume
                1.0f,  // Pitch
                RandomSource.create(),
                player.getX(), player.getY(), player.getZ()  // Position
            );
        } else {
            this.currentSoundInstance = SimpleSoundInstance.forUI(
                soundEvent,
                this.volume  // Volume
            );
        }

        soundManager.play(this.currentSoundInstance);
    }

    public void toggleSound(SoundManager soundManager) {
        if (isActive(soundManager)) {
            soundManager.stop(currentSoundInstance);
            currentSoundInstance = null;
        } else {
            playSound(soundManager);
        }
    }

    public boolean isActive(SoundManager soundManager) {
        return soundManager.isActive(currentSoundInstance);
    }
}