package com.bvengo.soundcontroller.mixin;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.gui.AllSoundOptionsScreen;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsSubScreen.class)
public class OptionsSubScreenMixin {
    @Shadow @Final public HeaderAndFooterLayout layout;

    @Shadow @Final protected Screen lastScreen;

    @Shadow @Final protected Options options;

    @WrapMethod(method = "addFooter")
    private void replaceDoneButton(Operation<Void> original) {

		//noinspection ConstantValue
		if (!((Object)this instanceof SoundOptionsScreen)) {
            return;
        }


        Minecraft client = Minecraft.getInstance();

        LinearLayout directionalLayoutWidget = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));

        // To individual volume options screen
        AllSoundOptionsScreen volumeOptionsScreen = new AllSoundOptionsScreen((SoundOptionsScreen)(Object)this, this.options);
        soundcontroller$addLayoutButton(client, directionalLayoutWidget, Translations.SOUND_SCREEN_TITLE, volumeOptionsScreen);
        soundcontroller$addLayoutButton(client, directionalLayoutWidget, CommonComponents.GUI_DONE, this.lastScreen);
    }

    @Unique
    private void soundcontroller$addLayoutButton(Minecraft client, LinearLayout layout, Component text, Screen nextScreen) {
        layout.addChild(Button.builder(text, button -> {
            client.setScreen(nextScreen);
        }).build());
    }
}