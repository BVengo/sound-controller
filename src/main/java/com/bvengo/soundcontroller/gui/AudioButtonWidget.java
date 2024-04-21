package com.bvengo.soundcontroller.gui;

import net.minecraft.client.sound.SoundManager;

public class AudioButtonWidget extends HoverableButtonWidget {
    public AudioButtonWidget(int x, int y, int width, int height, PressAction pressAction) {
        // TODO: Draw audio buttons
        super("filter", x, y, width, height, pressAction);
    }

    @Override
    public void onPress() {
        isToggled = true;
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        isToggled = false;
        onPress.onPress(this);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        // Do nothing so other sounds are heard instead.
    }
}
