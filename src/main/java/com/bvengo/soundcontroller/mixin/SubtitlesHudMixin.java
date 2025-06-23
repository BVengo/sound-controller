package com.bvengo.soundcontroller.mixin;

import com.bvengo.soundcontroller.SoundController;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.hud.SubtitlesHud;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SubtitlesHud.class)
public class SubtitlesHudMixin {
	@WrapOperation(method = "onSoundPlayed", at=@At(value = "INVOKE", target="Lnet/minecraft/client/sound/WeightedSoundSet;getSubtitle()Lnet/minecraft/text/Text;"))
	private Text replaceSubtitleText(WeightedSoundSet instance, Operation<Text> original, SoundInstance sound) {
		return SoundController.CONFIG.areSubtitlesEnabled() ? Text.of(sound.getId()) : original.call(instance);
	}

	@WrapOperation(
			method = "render",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;",
					ordinal = 0) // Targets the first getShowSubtitles().getValue() call
	)
	private Object modifyFirstShowSubtitlesCheck(SimpleOption instance, Operation<Object> original) {
		return (Boolean)(original.call(instance)) || SoundController.CONFIG.areSubtitlesEnabled();
	}

	@WrapOperation(
			method = "render",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;",
					ordinal = 1) // Targets the first getShowSubtitles().getValue() call
	)
	private Object modifySecondShowSubtitlesCheck(SimpleOption instance, Operation<Object> original) {
		return (Boolean)(original.call(instance)) || SoundController.CONFIG.areSubtitlesEnabled();
	}
}
