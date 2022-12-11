package com.bvengo.soundcontroller.ui;

import com.bvengo.soundcontroller.SoundControllerMod;
import com.bvengo.soundcontroller.config.SoundControllerConfigs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;



@Environment(EnvType.CLIENT)
public class CategorySoundOptionsScreen extends Screen {
    private static final Text TITLE_TEXT = Text.translatable("Sound Categories"); //TODO: Change to translatable
    private ButtonListWidget buttonList;

    private final Screen parent;
    public CategorySoundOptionsScreen(Screen parent) {
        super(TITLE_TEXT);
        this.parent = parent;
    }

    @Override
    protected void init() {
        // Back button
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> close())
                .dimensions(16, 16, 32, 32).build());

        // Title
        // For some reason the constructor takes width, height instead of x, y. It may be worth adding that later
        TextWidget titleWidget = new TextWidget(TITLE_TEXT, this.textRenderer);
        titleWidget.setX(64);
        titleWidget.setY((int)(32 - 0.5 * titleWidget.getHeight()));
        this.addDrawableChild(titleWidget);

        // Categories
        this.buttonList = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);



//        this.buttonList.addSingleOptionEntry(...); // Master
//        this.buttonList.addAll(...); // Category Options

//        this.addSelectableChild(this.buttonList);

//        Testing updating values (changes are written to file when exiting the options)
//        SoundControllerConfigs.update("master", 2.0f);
//        SoundControllerConfigs.update("minecraft:ambient", 5.0f);

    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        super.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        assert this.client != null;
        SoundControllerConfigs.save();
        this.client.setScreen(this.parent);
    }
}
