package com.bvengo.soundcontroller.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;


@Mixin(GameOptionsScreen.class)
public interface GameOptionsScreenAccessor {
    @Accessor("parent")
    Screen getParent();

    @Accessor("gameOptions")
    GameOptions getOptions();
}
