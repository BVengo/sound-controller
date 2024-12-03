package com.bvengo.soundcontroller.gui;

import net.minecraft.client.sound.SoundManager;

/**
 * Custom button widget that is used to trigger audio events.
 */
public class AudioButtonWidget extends TriggerButtonWidget {
    public AudioButtonWidget(int x, int y, int width, int height, PressAction pressAction) {
        super("audio", x, y, width, height, pressAction);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        // Do not play the button sound - we only want to hear the provided sound.
    }
}
