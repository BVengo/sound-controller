package com.bvengo.soundcontroller.mixin;

import com.bvengo.soundcontroller.SoundController;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SubtitleOverlay.class)
public class SubtitleOverlayMixin {
	@WrapOperation(method = "onPlaySound", at=@At(value = "INVOKE", target="Lnet/minecraft/client/sounds/WeighedSoundEvents;getSubtitle()Lnet/minecraft/network/chat/Component;"))
	private Component replaceSubtitleText(WeighedSoundEvents instance, Operation<Component> original, SoundInstance sound) {
		return SoundController.CONFIG.areSubtitlesEnabled() ? Component.translationArg(sound.getIdentifier()) : original.call(instance);
	}

	@WrapOperation(
			method = "render",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;",
					ordinal = 0) // Targets the first getShowSubtitles().getValue() call
	)
	private Object modifyFirstShowSubtitlesCheck(OptionInstance instance, Operation<Object> original) {
		return (Boolean)(original.call(instance)) || SoundController.CONFIG.areSubtitlesEnabled();
	}

	@WrapOperation(
			method = "render",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;",
					ordinal = 1) // Targets the first getShowSubtitles().getValue() call
	)
	private Object modifySecondShowSubtitlesCheck(OptionInstance instance, Operation<Object> original) {
		return (Boolean)(original.call(instance)) || SoundController.CONFIG.areSubtitlesEnabled();
	}
}
