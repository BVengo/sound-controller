package com.bvengo.soundcontroller.mixin;

import com.bvengo.soundcontroller.Constants;
import com.bvengo.soundcontroller.ui.AllSoundOptionsScreen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.SoundOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.screen.ScreenTexts;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundOptionsScreen.class)
public abstract class SoundOptionsScreenMixin {
    @Inject(method = "init", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/option/SoundOptionsScreen;addSelectableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;",
            shift = At.Shift.AFTER), cancellable = true)
    private void replaceDoneButton(CallbackInfo ci) {
        // Set up accessors and variables
        ScreenAccessor screenAccessor = (ScreenAccessor) (Object) this;
        GameOptionsScreenAccessor gameOptionsScreenAccessor = (GameOptionsScreenAccessor) (Object) this;

        MinecraftClient client = screenAccessor.getClient();
        Screen parent = gameOptionsScreenAccessor.getParent();
        GameOptions options = gameOptionsScreenAccessor.getOptions();

        int width = screenAccessor.getWidth();
        int height = screenAccessor.getHeight();

        // Button to sounds volume list
        screenAccessor.invokeAddDrawableChild(ButtonWidget.builder(Constants.SOUND_SCREEN_TITLE, (button) -> {
            client.options.write();
            client.setScreen(new AllSoundOptionsScreen((Screen)(Object)this, options));
        }).dimensions(width / 2 - 155, height - 27, 150, 20).build());

        // DONE button
        screenAccessor.invokeAddDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
            client.options.write();
            client.setScreen(parent);
        }).dimensions(width / 2 + 5, height - 27, 150, 20).build());

        ci.cancel();
    }
}
