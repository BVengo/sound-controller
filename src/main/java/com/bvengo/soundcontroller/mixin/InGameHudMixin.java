package com.bvengo.soundcontroller.mixin;

import net.minecraft.client.gui.LayeredDrawer;
import net.minecraft.client.gui.hud.SubtitlesHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bvengo.soundcontroller.config.VolumeConfig;
import com.bvengo.soundcontroller.gui.RawSubtitlesHud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    private RawSubtitlesHud rawSubtitlesHud;

    @Shadow @Final private SubtitlesHud subtitlesHud;

    // inject at end of constructor
    @Inject(method = "<init>(Lnet/minecraft/client/MinecraftClient;)V", at = @At("RETURN"))
    private void init(MinecraftClient client, CallbackInfo ci) {
        rawSubtitlesHud = new RawSubtitlesHud(client);
    }

    @Redirect(method = "<init>(Lnet/minecraft/client/MinecraftClient;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LayeredDrawer;addLayer(Lnet/minecraft/client/gui/LayeredDrawer$Layer;)Lnet/minecraft/client/gui/LayeredDrawer;", ordinal = 13))
    private LayeredDrawer renderSubtitles(LayeredDrawer instance, LayeredDrawer.Layer layer) {
        return instance.addLayer((context, tickDelta) -> {
            VolumeConfig config = VolumeConfig.getInstance();

            if (config.areSubtitlesEnabled()) {
                rawSubtitlesHud.render(context);
            } else {
                subtitlesHud.render(context);
            }
        });
    }
}
