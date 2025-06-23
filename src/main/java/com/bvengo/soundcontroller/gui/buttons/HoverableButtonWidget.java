package com.bvengo.soundcontroller.gui.buttons;

import com.bvengo.soundcontroller.SoundController;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;

/**
 * Custom button widget that is used as a trigger rather than a toggle.
 * i.e. it is only active while the button is being pressed.
 */
@Environment(EnvType.CLIENT)
public class HoverableButtonWidget extends ButtonWidget {
    protected boolean isPressed = false;

    private final Identifier ON_TEXTURE;
    private final Identifier OFF_TEXTURE;
    private final Identifier ON_HOVER_TEXTURE;
    private final Identifier OFF_HOVER_TEXTURE;

    String buttonId;

    public HoverableButtonWidget(String buttonId, int x, int y, int width, int height, PressAction pressAction) {
        super(x, y, width, height, ScreenTexts.EMPTY, pressAction, DEFAULT_NARRATION_SUPPLIER);

        this.buttonId = buttonId;

        ON_TEXTURE = Identifier.of(SoundController.MOD_ID, buttonId + "_button_on");
        OFF_TEXTURE = Identifier.of(SoundController.MOD_ID, buttonId + "_button_off");
        ON_HOVER_TEXTURE = Identifier.of(SoundController.MOD_ID, buttonId + "_button_on_hovered");
        OFF_HOVER_TEXTURE = Identifier.of(SoundController.MOD_ID, buttonId + "_button_off_hovered");
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier texture = isPressed ? (hovered ? ON_HOVER_TEXTURE : ON_TEXTURE)
                : (hovered ? OFF_HOVER_TEXTURE : OFF_TEXTURE);

        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), width, height);
    }
}
