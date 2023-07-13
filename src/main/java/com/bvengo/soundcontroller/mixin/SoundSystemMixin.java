package com.bvengo.soundcontroller.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.bvengo.soundcontroller.config.SoundConfig;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public abstract class SoundSystemMixin {
    @Unique
    private SoundInstance currentSoundInstance;

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At(value = "HEAD"))
    private void onPlay(SoundInstance sound, CallbackInfo ci) {
        // Store the current sound instance it can be used when adjusting the volume
        this.currentSoundInstance = sound;
    }

    @ModifyVariable(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At(value = "STORE"), index = 8)
    private float modifyH(float h) {
        // Adjust the volume based on the individual sound config
        Identifier soundId = this.currentSoundInstance.getId();
        float volumeMultiplier = SoundConfig.getInstance().getVolumeMultiplier(soundId.toString());
        return MathHelper.clamp(h * volumeMultiplier, 0.0F, 1.0F);
    }
}