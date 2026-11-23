package dev.bvengo.soundcontroller.gui.buttons;

import dev.bvengo.modcore.client.ui.ButtonSprites;
import dev.bvengo.modcore.client.ui.SpriteTriggerButton;
import dev.bvengo.soundcontroller.SoundController;
import dev.bvengo.soundcontroller.Translations;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class TriggerButtonWidget extends SpriteTriggerButton {
    public TriggerButtonWidget(String buttonId, int x, int y, int width, int height, OnPress action) {
        super(x, y, width, height, labelFor(buttonId),
                ButtonSprites.forButton(Identifier.fromNamespaceAndPath(SoundController.MOD_ID, buttonId)), action);
    }

    private static Component labelFor(String buttonId) {
        String translation = switch (buttonId) {
            case "audio" -> "play.tooltip";
            case "preset" -> "preset.load";
            default -> buttonId + ".tooltip";
        };

        return Translations.translatableOf(translation);
    }
}
