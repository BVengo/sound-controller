package com.bvengo.soundcontroller.mixin;

import com.bvengo.soundcontroller.VolumeData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.bvengo.soundcontroller.config.VolumeConfig;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(SoundSystem.class)
public abstract class SoundSystemMixin {
    @ModifyVariable(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At(value = "STORE"), index = 8)
    private float modifyH(float h, SoundInstance sound) {
        VolumeData volumeData = VolumeConfig.getInstance().getVolumeData(sound.getId().toString());
        return volumeData.getAdjustedVolume(sound, (SoundSystemAccessor)(Object)this);
    }

    @Inject(method = "getAdjustedVolume(Lnet/minecraft/client/sound/SoundInstance;)F", at = @At("HEAD"), cancellable = true)
    private void modifyGetAdjustedVolume(SoundInstance sound, CallbackInfoReturnable<Float> ci) {
        VolumeData volumeData = VolumeConfig.getInstance().getVolumeData(sound.getId().toString());
        float volume = volumeData.getAdjustedVolume(sound, (SoundSystemAccessor)(Object)this);

        ci.setReturnValue(volume);
        ci.cancel();
    }
}