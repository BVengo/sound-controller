package com.bvengo.soundcontroller.gui;

import net.minecraft.client.sound.SoundManager;

public class AudioButtonWidget extends HoverableButtonWidget {
    public AudioButtonWidget(int x, int y, int width, int height, PressAction pressAction) {
        super("audio", x, y, width, height, pressAction);
    }

    @Override
    public void onPress() {
        super.onPress();
        isToggled = true;
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        // Release toggle texture on release (this is a trigger, nto a toggle)
        if(isToggled) {
            isToggled = false;
        }
    }

    @Override
    protected void updateHovered(int mouseX, int mouseY) {
        super.updateHovered(mouseX, mouseY);

        if(!isHovered && isToggled) {
            isToggled = false;
        }
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        // Do nothing so other sounds are heard instead.
    }
}
