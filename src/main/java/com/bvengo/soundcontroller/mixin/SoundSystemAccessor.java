package com.bvengo.soundcontroller.mixin;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SoundSystem.class)
public interface SoundSystemAccessor {
    @Invoker("setVolume")
    void invokeUpdateSoundVolume(SoundInstance sound, float volume);
}
