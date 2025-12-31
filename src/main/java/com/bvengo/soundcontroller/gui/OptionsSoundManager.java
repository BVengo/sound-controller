package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.VolumeData;
import com.google.common.cache.CacheBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

import java.util.Map;
import java.util.Optional;

public class OptionsSoundManager implements AutoCloseable {
    private final Map<Identifier, SimpleSoundInstance> idSoundInstances = CacheBuilder.newBuilder()
            .weakKeys()
            .weakValues()
            .<Identifier, SimpleSoundInstance>build()
            .asMap();

    private final SoundManager soundManager = Minecraft.getInstance().getSoundManager();

    public void toggleSound(VolumeData volumeData) {
        Optional.ofNullable(this.idSoundInstances.remove(volumeData.getId())).ifPresentOrElse(
                this::tryStopSound,
                () -> this.idSoundInstances.put(volumeData.getId(), this.createAndPlaySound(volumeData))
        );
    }

    private void tryStopSound(SimpleSoundInstance soundInstance) {
        if (this.soundManager.isActive(soundInstance)) {
            this.soundManager.stop(soundInstance);
        }
    }

    private SimpleSoundInstance createAndPlaySound(VolumeData volumeData) {
        final LocalPlayer player = Minecraft.getInstance().player;
        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(volumeData.getId());

        float volume = volumeData.getVolume();
        SimpleSoundInstance soundInstance = player != null
                ? new SimpleSoundInstance(
                soundEvent, SoundSource.MASTER,
                volume,
                1.0f, // Pitch
                RandomSource.create(),
                player.getX(), player.getY(), player.getZ())
                : SimpleSoundInstance.forUI(soundEvent, volume);

        this.soundManager.play(soundInstance);
        return soundInstance;
    }

    @Override
    public void close() {
        this.idSoundInstances.values().forEach(this::tryStopSound);
        this.idSoundInstances.clear();
    }
}
