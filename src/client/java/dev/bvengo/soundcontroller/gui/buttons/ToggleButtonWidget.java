package dev.bvengo.soundcontroller.gui.buttons;

import dev.bvengo.modcore.client.ui.ButtonSprites;
import dev.bvengo.modcore.client.ui.SpriteToggleButton;
import dev.bvengo.soundcontroller.SoundController;
import dev.bvengo.soundcontroller.Translations;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.resources.Identifier;

public final class ToggleButtonWidget extends SpriteToggleButton {
    private final OnPress action;

    public ToggleButtonWidget(String buttonId, int x, int y, int width, int height, OnPress action, boolean selected) {
        super(x, y, width, height, Translations.translatableOf(buttonId + ".tooltip"),
                ButtonSprites.forButton(Identifier.fromNamespaceAndPath(SoundController.MOD_ID, buttonId)),
                selected, value -> {});
        this.action = action;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        action.onPress(this);
        super.onPress(input);
    }
}
