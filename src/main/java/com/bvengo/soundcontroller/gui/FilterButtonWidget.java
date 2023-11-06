//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.SoundController;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FilterButtonWidget extends ButtonWidget {
    private boolean isToggled = false;

    private static final Identifier ON_TEXTURE = new Identifier(SoundController.MOD_ID, "filter_button_on");
    private static final Identifier OFF_TEXTURE = new Identifier(SoundController.MOD_ID, "filter_button_off");
    private static final Identifier ON_HOVER_TEXTURE = new Identifier(SoundController.MOD_ID, "filter_button_on_hovered");
    private static final Identifier OFF_HOVER_TEXTURE = new Identifier(SoundController.MOD_ID, "filter_button_off_hovered");

    public FilterButtonWidget(int x, int y, int width, int height,ButtonWidget.PressAction pressAction) {
        super(x, y, width, height, ScreenTexts.EMPTY, pressAction, DEFAULT_NARRATION_SUPPLIER);
    }

    private boolean checkHovered(int mouseX, int mouseY) {
        return mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
    }

    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        // Check mouseX, mouseY for hover
        boolean isHovered = this.checkHovered(mouseX, mouseY);

        Identifier texture = this.isToggled ? (isHovered ? ON_HOVER_TEXTURE : ON_TEXTURE) : (isHovered ? OFF_HOVER_TEXTURE : OFF_TEXTURE);
        context.drawGuiTexture(texture, this.getX(), this.getY(), this.width, this.height);
    }

    public void onPress() {
        this.onPress.onPress(this);
        this.isToggled = !this.isToggled;
    }
}
