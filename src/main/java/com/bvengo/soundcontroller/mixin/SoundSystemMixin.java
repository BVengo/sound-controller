package com.bvengo.soundcontroller.mixin;

import com.bvengo.soundcontroller.SoundController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @ModifyVariable(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At(value = "STORE"), index = 8)
    private float modifyH(float h, SoundInstance sound) {
        // h comes from getAdjustedVolume(float volume, Category category) - we can't inject there, because no ID is available
        return SoundController.CONFIG.getAdjustedVolume(sound, (SoundSystemAccessor) this);
    }

    @Inject(method = "getAdjustedVolume(Lnet/minecraft/client/sound/SoundInstance;)F", at = @At("HEAD"), cancellable = true)
    private void modifyGetAdjustedVolume(SoundInstance sound, CallbackInfoReturnable<Float> ci) {
        float volume = SoundController.CONFIG.getAdjustedVolume(sound, (SoundSystemAccessor) this);
        ci.setReturnValue(volume);
        ci.cancel();
    }
}