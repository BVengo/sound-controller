package com.bvengo.soundcontroller.gui.buttons;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.VolumeData;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.sounds.SoundManager;

/**
 * Custom button widget that is used to trigger audio events.
 */
public class AudioButtonWidget extends TriggerButtonWidget {
    public AudioButtonWidget(int x, int y, int width, int height, SoundManager soundManager, VolumeData volumeData) {
        super("audio", x, y, width, height, (button) -> volumeData.playSound(soundManager));
        setTooltip(Tooltip.create(Translations.PLAY_BUTTON_TOOLTIP));
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        // Do not play the button sound - we only want to hear the provided sound.
    }
}
