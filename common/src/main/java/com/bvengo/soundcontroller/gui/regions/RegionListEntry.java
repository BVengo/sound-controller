package com.bvengo.soundcontroller.gui.regions;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.config.RegionConfig;
import com.bvengo.soundcontroller.region.RegionData;
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

public class RegionListEntry extends Entry<RegionListEntry> {
    private static final int BUTTON_HEIGHT = 20;
    private static final int EDIT_WIDTH = 50;
    private static final int DELETE_WIDTH = 50;

    private final RegionData region;
    private final Screen parentScreen;
    private final Font font;
    private final Button editButton;
    private final Button deleteButton;

    public RegionListEntry(RegionData region, Screen parentScreen, Font font) {
        this.region = region;
        this.parentScreen = parentScreen;
        this.font = font;

        this.editButton = Button.builder(Translations.translatableOf("region.edit"), b -> openEditScreen())
            .size(EDIT_WIDTH, BUTTON_HEIGHT)
            .build();

        this.deleteButton = Button.builder(Translations.translatableOf("region.delete"), b -> confirmDelete())
            .size(DELETE_WIDTH, BUTTON_HEIGHT)
            .tooltip(Tooltip.create(Translations.translatableOf("region.delete.tooltip")))
            .build();
    }

    private void openEditScreen() {
        Minecraft.getInstance().setScreen(new RegionEditScreen(parentScreen, region));
    }

    private void confirmDelete() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new ConfirmScreen(
            confirmed -> {
                if (confirmed) {
                    RegionConfig.getInstance().removeRegion(region);
                    RegionConfig.getInstance().save();
                }
                mc.setScreen(parentScreen);
            },
            Translations.translatableOf("region.delete.confirm.title"),
            Component.literal("\"" + region.getName() + "\"")
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
        int nameWidth = (entryRight - entryLeft) / 3;
        int geoWidth = editX - 8 - (entryLeft + nameWidth + 8);

        String name = font.plainSubstrByWidth(region.getName(), nameWidth);
        String geo = font.plainSubstrByWidth(region.getGeometry().getDescription(), Math.max(0, geoWidth));

        context.drawString(font, name, entryLeft, centerY + 1, 0xFFFFFFFF, true);
        context.drawString(font, geo, entryLeft + nameWidth + 8, centerY + 1, 0xFFAAAAAA, true);

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
