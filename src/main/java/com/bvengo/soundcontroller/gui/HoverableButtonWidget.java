package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.SoundController;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class HoverableButtonWidget extends ButtonWidget {
    protected boolean isToggled = false;
    protected boolean isHovered = false;

    private Identifier ON_TEXTURE;
    private Identifier OFF_TEXTURE;
    private Identifier ON_HOVER_TEXTURE;
    private Identifier OFF_HOVER_TEXTURE;

    String buttonId;

    public HoverableButtonWidget(String buttonId, int x, int y, int width, int height, PressAction pressAction) {
        super(x, y, width, height, ScreenTexts.EMPTY, pressAction, DEFAULT_NARRATION_SUPPLIER);

        this.buttonId = buttonId;

        ON_TEXTURE = new Identifier(SoundController.MOD_ID, buttonId + "_button_on");
        OFF_TEXTURE = new Identifier(SoundController.MOD_ID, buttonId + "_button_off");
        ON_HOVER_TEXTURE = new Identifier(SoundController.MOD_ID, buttonId + "_button_on_hovered");
        OFF_HOVER_TEXTURE = new Identifier(SoundController.MOD_ID, buttonId + "_button_off_hovered");
    }

    protected void updateHovered(int mouseX, int mouseY) {
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

        Identifier texture = isToggled ? (isHovered ? ON_HOVER_TEXTURE : ON_TEXTURE)
                : (isHovered ? OFF_HOVER_TEXTURE : OFF_TEXTURE);

        context.drawGuiTexture(texture, getX(), getY(), width, height);
    }

    public void onPress() {
        super.onPress();
        isToggled = !isToggled;
    }
}
