package com.bvengo.soundcontroller.mixin;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.gui.AllSoundOptionsScreen;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SoundOptionsScreen.class)
public abstract class SoundOptionsScreenMixin extends OptionsSubScreen {
    public SoundOptionsScreenMixin(Screen screen, Options options, Component component) {
        super(screen, options, component);
    }

    @Override
    protected void addFooter() {
        LinearLayout footerLayout = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));

        // To individual volume options screen
        addLayoutButton(
                footerLayout,
                Translations.SOUND_SCREEN_TITLE,
                b -> this.minecraft.setScreen(new AllSoundOptionsScreen(this, this.options)));
        addLayoutButton(footerLayout, CommonComponents.GUI_DONE, b -> this.onClose());
    }

    @Unique
    private static void addLayoutButton(LinearLayout layout, Component text, Button.OnPress onPress) {
        layout.addChild(Button.builder(text, onPress).build());
    }
}
