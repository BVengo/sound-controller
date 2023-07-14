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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(SoundSystem.class)
public abstract class SoundSystemMixin {
    @Unique private SoundInstance currentSoundInstance;
    SoundConfig config = SoundConfig.getInstance();

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At(value = "HEAD"))
    private void onPlay(SoundInstance sound, CallbackInfo ci) {
        // Store the current sound instance it can be used when adjusting the volume
        this.currentSoundInstance = sound;
    }

    @ModifyVariable(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At(value = "STORE"), index = 8)
    private float modifyH(float h) {
        // Adjust the volume based on the individual sound config
        Identifier soundId = this.currentSoundInstance.getId();
        float volumeMultiplier = config.getVolumeMultiplier(soundId.toString());
        return MathHelper.clamp(h * volumeMultiplier, 0.0F, 1.0F);
    }

    @Inject(method = "getAdjustedVolume(Lnet/minecraft/client/sound/SoundInstance;)F", at = @At("HEAD"), cancellable = true)
    private void modifyGetAdjustedVolume(SoundInstance sound, CallbackInfoReturnable<Float> ci) {
        float volume = ((SoundSystemAccessor)(Object)this).invokeGetAdjustedVolume(sound.getVolume(), sound.getCategory());
        float volumeMultiplier = config.getVolumeMultiplier(sound.getId().toString());
        volume = MathHelper.clamp(volume * volumeMultiplier, 0.0F, 1.0F);

        ci.setReturnValue(volume);
        ci.cancel();
    }
}