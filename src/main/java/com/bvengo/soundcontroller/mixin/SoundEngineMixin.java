package com.bvengo.soundcontroller.mixin;

import com.bvengo.soundcontroller.SoundController;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    @WrapOperation(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)Lnet/minecraft/client/sounds/SoundEngine$PlayResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundEngine;calculateVolume(FLnet/minecraft/sounds/SoundSource;)F"))
    private float modifyH(SoundEngine instance, float volume, SoundSource category, Operation<Float> original, SoundInstance sound) {
        // h comes from getAdjustedVolume(float volume, Category category) - we can't inject there, because no ID is available
        float h = original.call(instance, volume, category);
        return SoundController.CONFIG.getAdjustedVolume(sound, h);
    }

    @WrapMethod(method = "calculateVolume(Lnet/minecraft/client/resources/sounds/SoundInstance;)F")
    private float modifyGetAdjustedVolume(SoundInstance sound, Operation<Float> original) {
        float volume = original.call(sound);
        return SoundController.CONFIG.getAdjustedVolume(sound, volume);
    }
}