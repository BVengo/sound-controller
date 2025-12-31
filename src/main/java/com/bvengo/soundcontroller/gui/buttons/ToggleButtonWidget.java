package com.bvengo.soundcontroller.gui.buttons;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.InputWithModifiers;

/**
 * Custom button widget that is used as a trigger rather than a toggle.
 * i.e. it is only active while the button is being pressed.
 */
@Environment(EnvType.CLIENT)
public class ToggleButtonWidget extends HoverableButtonWidget {

    public ToggleButtonWidget(String buttonId, int x, int y, int width, int height, OnPress pressAction, boolean isToggled) {
        super(buttonId, x, y, width, height, pressAction);
        this.isPressed = isToggled;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        super.onPress(input);
        this.isPressed = !this.isPressed;
    }
}
