package com.bvengo.soundcontroller.gui.buttons;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.VolumeData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.Identifier;

/**
 * Custom button widget that is used to trigger audio events.
 */
public class AudioButtonWidget extends TriggerButtonWidget {
    private final VolumeData volumeData;
    private final SoundManager soundManager;

    public AudioButtonWidget(int x, int y, int width, int height, SoundManager soundManager, VolumeData volumeData) {
        super("audio", x, y, width, height, (button) -> volumeData.toggleSound(soundManager));
        setTooltip(Tooltip.create(Translations.PLAY_BUTTON_TOOLTIP));

        this.soundManager = soundManager;
        this.volumeData = volumeData;
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        // Do not play the button sound - we only want to hear the provided sound.
    }

    @Override
    protected Identifier getTextureIdentifier() {
        // Always show active state if the sound is currently playing
        boolean isActive = isPressed || volumeData.isActive(soundManager);

        return (isPressed || isActive) ? (isHovered ? ON_HOVER_TEXTURE : ON_TEXTURE)
                : (isHovered ? OFF_HOVER_TEXTURE : OFF_TEXTURE);
    }
}
