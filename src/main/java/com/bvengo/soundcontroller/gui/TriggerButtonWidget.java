package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.SoundController;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;

/**
 * Custom button widget that is used as a trigger rather than a toggle.
 * i.e. it is only active while the button is being pressed.
 */
@Environment(EnvType.CLIENT)
public class TriggerButtonWidget extends ButtonWidget {
    protected boolean isPressed = false;
    protected boolean isHovered = false;

    private final Identifier ON_TEXTURE;
    private final Identifier OFF_TEXTURE;
    private final Identifier ON_HOVER_TEXTURE;
    private final Identifier OFF_HOVER_TEXTURE;

    String buttonId;

    public TriggerButtonWidget(String buttonId, int x, int y, int width, int height, PressAction pressAction) {
        super(x, y, width, height, ScreenTexts.EMPTY, pressAction, DEFAULT_NARRATION_SUPPLIER);

        this.buttonId = buttonId;

        ON_TEXTURE = Identifier.of(SoundController.MOD_ID, buttonId + "_button_on");
        OFF_TEXTURE = Identifier.of(SoundController.MOD_ID, buttonId + "_button_off");
        ON_HOVER_TEXTURE = Identifier.of(SoundController.MOD_ID, buttonId + "_button_on_hovered");
        OFF_HOVER_TEXTURE = Identifier.of(SoundController.MOD_ID, buttonId + "_button_off_hovered");
    }

    protected void updateHovered(double mouseX, double mouseY) {
        isHovered = (
            mouseX >= getX() &&
            mouseY >= getY() && 
            mouseX < getX() + width &&
            mouseY < getY() + height
        );
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        // Check mouseX, mouseY for hover
        updateHovered(mouseX, mouseY);

        Identifier texture = isPressed ? (isHovered ? ON_HOVER_TEXTURE : ON_TEXTURE)
                : (isHovered ? OFF_HOVER_TEXTURE : OFF_TEXTURE);

        context.drawGuiTexture(RenderLayer::getGuiTextured, texture, getX(), getY(), width, height);
    }

    @Override
    public void onPress() {
        isPressed = true;
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        // Release toggle texture on release, and perform press action.
        // Only perform press action if button remains hovered.
        updateHovered(mouseX, mouseY);

        if(isHovered) {
            this.onPress.onPress(this);
        }

        isPressed = false;
    }
}
