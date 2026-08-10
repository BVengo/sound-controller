package com.bvengo.soundcontroller.gui.presets;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.config.PresetConfig;
import com.bvengo.soundcontroller.config.PresetData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList.Entry;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class PresetListEntry extends Entry<PresetListEntry> {
    private static final int BUTTON_HEIGHT = 20;
    private static final int EDIT_WIDTH = 50;
    private static final int DELETE_WIDTH = 50;

    private final PresetData preset;
    private final Screen parentScreen;
    private final Font font;
    private final Button editButton;
    private final Button deleteButton;

    public PresetListEntry(PresetData preset, Screen parentScreen, Font font) {
        this.preset = preset;
        this.parentScreen = parentScreen;
        this.font = font;

        this.editButton = Button.builder(Translations.translatableOf("preset.edit"), b -> openEditScreen())
            .size(EDIT_WIDTH, BUTTON_HEIGHT)
            .build();

        this.deleteButton = Button.builder(Translations.translatableOf("preset.delete"), b -> confirmDelete())
            .size(DELETE_WIDTH, BUTTON_HEIGHT)
            .tooltip(Tooltip.create(Translations.translatableOf("preset.delete.tooltip")))
            .build();
    }

    private void openEditScreen() {
        Minecraft.getInstance().setScreen(new PresetEditScreen(parentScreen, preset));
    }

    private void confirmDelete() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new ConfirmScreen(
            confirmed -> {
                if (confirmed) {
                    PresetConfig.getInstance().removePreset(preset);
                    PresetConfig.getInstance().save();
                }
                mc.setScreen(parentScreen);
            },
            Translations.translatableOf("preset.delete.confirm.title"),
            Component.literal("\"" + preset.getName() + "\"")
        ));
    }

    @Override
    public void render(GuiGraphics context, int index, int top, int left, int entryWidth, int entryHeight,
                       int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int entryLeft = left + 8;
        int entryRight = left + entryWidth - 8;
        int centerY = top + (entryHeight - BUTTON_HEIGHT) / 2;

        int deleteX = entryRight - DELETE_WIDTH;
        int editX = deleteX - 4 - EDIT_WIDTH;
        int nameWidth = editX - 8 - entryLeft;

        String name = font.plainSubstrByWidth(preset.getName(), Math.max(0, nameWidth));
        context.drawString(font, name, entryLeft, centerY + 1, 0xFFFFFFFF, true);

        this.editButton.setPosition(editX, centerY);
        this.editButton.render(context, mouseX, mouseY, tickDelta);

        this.deleteButton.setPosition(deleteX, centerY);
        this.deleteButton.render(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public List<Button> children() {
        return List.of(editButton, deleteButton);
    }

    @Override
    public List<Button> narratables() {
        return List.of(editButton, deleteButton);
    }
}
