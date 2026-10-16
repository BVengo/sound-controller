package com.bvengo.soundcontroller.mixin;

import com.bvengo.soundcontroller.SoundController;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
//? if >=1.21.6
//import net.minecraft.client.sounds.SoundEngine.PlayResult;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    //? if >=1.21.6 {
    /*@Inject(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)Lnet/minecraft/client/sounds/SoundEngine$PlayResult;", at = @At("HEAD"), cancellable = true)
    private void skipMutedSounds(SoundInstance sound, CallbackInfoReturnable<PlayResult> cir) {
        if (SoundController.getConfig().shouldSkipStarting(sound)) {
            cir.setReturnValue(PlayResult.NOT_STARTED);
        }
    }

    @WrapOperation(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)Lnet/minecraft/client/sounds/SoundEngine$PlayResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundEngine;calculateVolume(FLnet/minecraft/sounds/SoundSource;)F"))
    private float adjustInitialVolume(SoundEngine instance, float volume, SoundSource category,
            Operation<Float> original, SoundInstance sound) {
        float baseVolume = original.call(instance, volume, category);
        return SoundController.getConfig().getAdjustedVolume(sound, baseVolume);
    }

    @WrapOperation(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)Lnet/minecraft/client/sounds/SoundEngine$PlayResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/sounds/SoundInstance;canStartSilent()Z"))
    private boolean allowRegionMutedMovingSoundToStart(SoundInstance sound, Operation<Boolean> original) {
        return original.call(sound) || SoundController.getConfig().shouldStartSilently(sound);
    }
    *///?} else {
    @WrapOperation(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundEngine;calculateVolume(FLnet/minecraft/sounds/SoundSource;)F"))
    private float modifyH(SoundEngine instance, float volume, SoundSource category, Operation<Float> original, SoundInstance sound) {
        // h comes from getAdjustedVolume(float volume, Category category) - we can't inject there, because no ID is available
        float h = original.call(instance, volume, category);
        return SoundController.getConfig().getAdjustedVolume(sound, h);
    }
    //?}


    @WrapMethod(method = "calculateVolume(Lnet/minecraft/client/resources/sounds/SoundInstance;)F")
    private float adjustUpdatedVolume(SoundInstance sound, Operation<Float> original) {
        float volume = original.call(sound);
        return SoundController.getConfig().getAdjustedVolume(sound, volume);
    }
}
