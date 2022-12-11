package com.bvengo.soundcontroller.mixin;

import com.bvengo.soundcontroller.ui.CategorySoundOptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OptionsScreen.class)
public class MixinOptionsScreen extends Screen {
    protected MixinOptionsScreen(Text title) {
        super(title);
    }

    @Dynamic
    @Inject(method = "method_19828", at = @At("HEAD"), cancellable = true)
    // Change to 19829 - using 'Video Settings' so I can disable sound while developing
    private void open(CallbackInfoReturnable<Screen> ci) {
        ci.setReturnValue(new CategorySoundOptionsScreen(this));
    }
}
