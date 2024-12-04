package com.bvengo.soundcontroller.mixin;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.gui.AllSoundOptionsScreen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.SoundOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.screen.ScreenTexts;

import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptionsScreen.class)
public class GameOptionsScreenMixin {
    @Shadow @Final public ThreePartsLayoutWidget layout;

    @Inject(method = "initFooter", at = @At("HEAD"), cancellable = true)
    private void replaceDoneButton(CallbackInfo ci) {

        if (!((Object)this instanceof SoundOptionsScreen)) {
            return;
        }

        ScreenAccessor screenAccessor = (ScreenAccessor) (Object) this;
        GameOptionsScreenAccessor gameOptionsScreenAccessor = (GameOptionsScreenAccessor) (Object) this;

        MinecraftClient client = screenAccessor.getClient();
        Screen parent = gameOptionsScreenAccessor.getParent();
        GameOptions options = gameOptionsScreenAccessor.getOptions();

        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));

        // To individual volume options screen
        AllSoundOptionsScreen volumeOptionsScreen = new AllSoundOptionsScreen((SoundOptionsScreen)(Object)this, options);
        soundcontroller$addLayoutButton(client, directionalLayoutWidget, Translations.SOUND_SCREEN_TITLE, volumeOptionsScreen);
        soundcontroller$addLayoutButton(client, directionalLayoutWidget, ScreenTexts.DONE, parent);

        ci.cancel();
    }

    @Unique
    private void soundcontroller$addLayoutButton(MinecraftClient client, DirectionalLayoutWidget layout, Text text, Screen nextScreen) {
        layout.add(ButtonWidget.builder(text, button -> {
            client.setScreen(nextScreen);
        }).build());
    }
}