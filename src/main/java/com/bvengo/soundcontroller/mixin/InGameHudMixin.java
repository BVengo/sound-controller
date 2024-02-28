package com.bvengo.soundcontroller.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bvengo.soundcontroller.config.VolumeConfig;
import com.bvengo.soundcontroller.gui.RawSubtitlesHud;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.SubtitlesHud;
import net.minecraft.client.render.item.ItemRenderer;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    private RawSubtitlesHud rawSubtitlesHud;

    // inject at end of constructor
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(MinecraftClient client, ItemRenderer itemRenderer, CallbackInfo ci) {
        rawSubtitlesHud = new RawSubtitlesHud(client);
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/SubtitlesHud;render(Lnet/minecraft/client/gui/DrawContext;)V"))
    private void renderSubtitles(SubtitlesHud subtitlesHud, DrawContext context, Operation<Void> original) {
        VolumeConfig config = VolumeConfig.getInstance();

        if (config.areSubtitlesEnabled()) {
            rawSubtitlesHud.render(context);
        } else {
            original.call(subtitlesHud, context);
        }
    }
}
