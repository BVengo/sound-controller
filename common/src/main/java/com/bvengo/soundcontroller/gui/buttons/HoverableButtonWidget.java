package com.bvengo.soundcontroller.gui.buttons;

import com.bvengo.soundcontroller.SoundController;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

/**
 * Custom button widget that is used as a trigger rather than a toggle.
 * i.e. it is only active while the button is being pressed.
 */
public class HoverableButtonWidget extends Button {
    protected boolean isPressed = false;

    protected final ResourceLocation ON_TEXTURE;
    protected final ResourceLocation OFF_TEXTURE;
    protected final ResourceLocation ON_HOVER_TEXTURE;
    protected final ResourceLocation OFF_HOVER_TEXTURE;

    String buttonId;

    public HoverableButtonWidget(String buttonId, int x, int y, int width, int height, OnPress onPress) {
        super(x, y, width, height, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);

        this.buttonId = buttonId;

        ON_TEXTURE = ResourceLocation.fromNamespaceAndPath(SoundController.MOD_ID, buttonId + "_button_on");
        OFF_TEXTURE = ResourceLocation.fromNamespaceAndPath(SoundController.MOD_ID, buttonId + "_button_off");
        ON_HOVER_TEXTURE = ResourceLocation.fromNamespaceAndPath(SoundController.MOD_ID, buttonId + "_button_on_hovered");
        OFF_HOVER_TEXTURE = ResourceLocation.fromNamespaceAndPath(SoundController.MOD_ID, buttonId + "_button_off_hovered");
    }

    protected ResourceLocation getTextureIdentifier() {
        return isPressed ? (isHovered ? ON_HOVER_TEXTURE : ON_TEXTURE)
                : (isHovered ? OFF_HOVER_TEXTURE : OFF_TEXTURE);
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        ResourceLocation texture = getTextureIdentifier();
        context.blitSprite(texture, getX(), getY(), width, height);
    }
}
