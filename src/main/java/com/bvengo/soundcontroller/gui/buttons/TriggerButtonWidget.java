package com.bvengo.soundcontroller.gui.buttons;

//? if >=1.21.9 {
/*import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
*///?}

/**
 * Custom button widget that is used as a trigger rather than a toggle.
 * i.e. it is only active while the button is being pressed.
 */
public class TriggerButtonWidget extends HoverableButtonWidget {

    public TriggerButtonWidget(String buttonId, int x, int y, int width, int height, OnPress pressAction) {
        super(buttonId, x, y, width, height, pressAction);
    }

    @Override
    public void onPress(/*? if >= 1.21.9 {*//*InputWithModifiers input*//*?}*/) {
        isPressed = true;
    }

    @Override
    public void onRelease(/*? if >= 1.21.9 {*//*MouseButtonEvent click*//*?} else {*/double mouseX, double mouseY/*?}*/) {
        // Release toggle texture on release, and perform press action.
        // Only perform press action if button remains hovered.
        if (isHovered) {
            this.onPress.onPress(this);
        }

        isPressed = false;
    }
}
