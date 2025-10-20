package com.bvengo.soundcontroller.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameOptionsScreen.class)
public class ExtendGameOptionsScreenMixin {
    @Shadow @Final public ThreePartsLayoutWidget layout;

    @WrapOperation(method = "initFooter", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ThreePartsLayoutWidget;addFooter(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;", ordinal = 0))
    protected Widget replaceDoneButton(ThreePartsLayoutWidget instance, Widget widget, Operation<Widget> original) {
        return original.call(instance, widget);
    }
}