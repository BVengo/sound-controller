package com.bvengo.soundcontroller.mixin;

import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;


@Mixin(SoundSystem.class)
public interface SoundSystemAccessor {
    @Invoker("getAdjustedVolume")
    float invokeGetAdjustedVolume(float volume, SoundCategory category);
}
