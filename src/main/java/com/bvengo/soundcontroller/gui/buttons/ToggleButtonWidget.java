package com.bvengo.soundcontroller.gui.buttons;

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
public class ToggleButtonWidget extends HoverableButtonWidget {

    public ToggleButtonWidget(String buttonId, int x, int y, int width, int height, PressAction pressAction, boolean isToggled) {
        super(buttonId, x, y, width, height, pressAction);
        this.isPressed = isToggled;
    }

    @Override
    public void onPress() {
        // Natural toggle when button is pressed
        this.onPress.onPress(this);
        isPressed = !isPressed;
    }
}
