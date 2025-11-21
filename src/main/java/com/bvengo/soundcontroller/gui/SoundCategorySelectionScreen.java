package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.Translations;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.screen.ScreenTexts;

/**
 * Screen that displays the available sound categories before drilling down into
 * the per-sound configuration screen.
 */
public class SoundCategorySelectionScreen extends Screen {
    private final Screen parent;
    private final GameOptions options;

    public SoundCategorySelectionScreen(Screen parent, GameOptions options) {
        super(Translations.SOUND_SCREEN_TITLE);
        this.parent = parent;
        this.options = options;
    }

    @Override
    protected void init() {
        int buttonWidth = 160;
        int buttonHeight = 20;
        int spacing = 6;
        int columns = 2;

        int totalWidth = columns * buttonWidth + (columns - 1) * spacing;
        int startX = (this.width - totalWidth) / 2;
        int startY = 60;

        IndividualSoundCategory[] categories = IndividualSoundCategory.values();
        for (int index = 0; index < categories.length; index++) {
            IndividualSoundCategory category = categories[index];
            int column = index % columns;
            int row = index / columns;

            int x = startX + column * (buttonWidth + spacing);
            int y = startY + row * (buttonHeight + spacing);

            this.addDrawableChild(ButtonWidget.builder(category.getDisplayName(), button -> {
                this.client.setScreen(new AllSoundOptionsScreen(this, options, category));
            }).dimensions(x, y, buttonWidth, buttonHeight).build());
        }

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close())
                .dimensions(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
    }
}
