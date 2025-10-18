package com.bvengo.soundcontroller.mixin;

import com.bvengo.soundcontroller.SoundController;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @WrapOperation(method = "play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundSystem;getAdjustedVolume(FLnet/minecraft/sound/SoundCategory;)F"))
    private float modifyH(SoundSystem instance, float volume, SoundCategory category, Operation<Float> original, SoundInstance sound) {
        // h comes from getAdjustedVolume(float volume, Category category) - we can't inject there, because no ID is available
        return SoundController.CONFIG.getAdjustedVolume(sound);
    }

    @Inject(method = "getAdjustedVolume(Lnet/minecraft/client/sound/SoundInstance;)F", at = @At("HEAD"), cancellable = true)
    private void modifyGetAdjustedVolume(SoundInstance sound, CallbackInfoReturnable<Float> ci) {
        float volume = SoundController.CONFIG.getAdjustedVolume(sound);
        ci.setReturnValue(volume);
        ci.cancel();
    }
}