package com.bvengo.soundcontroller.gui.regions;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.config.RegionConfig;
import com.bvengo.soundcontroller.region.RegionData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
        Minecraft.getInstance().setScreenAndShow(new RegionEditScreen(parentScreen, region));
    }

    private void confirmDelete() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreenAndShow(new ConfirmScreen(
            confirmed -> {
                if (confirmed) {
                    RegionConfig.getInstance().removeRegion(region);
                    RegionConfig.getInstance().save();
                }
                mc.setScreenAndShow(parentScreen);
            },
            Translations.translatableOf("region.delete.confirm.title"),
            Component.literal("\"" + region.getName() + "\"")
        ));
    }

    @Override
    public void extractContent(GuiGraphicsExtractor context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int entryLeft = getX() + 8;
        int entryRight = getX() + getWidth() - 8;
        int centerY = getY() + (getHeight() - BUTTON_HEIGHT) / 2;

        int deleteX = entryRight - DELETE_WIDTH;
        int editX = deleteX - 4 - EDIT_WIDTH;
        int nameWidth = (entryRight - entryLeft) / 3;
        int geoWidth = editX - 8 - (entryLeft + nameWidth + 8);

        String name = font.plainSubstrByWidth(region.getName(), nameWidth);
        String geo = font.plainSubstrByWidth(region.getGeometry().getDescription(), Math.max(0, geoWidth));

        context.text(font, name, entryLeft, centerY + 1, 0xFFFFFFFF, true);
        context.text(font, geo, entryLeft + nameWidth + 8, centerY + 1, 0xFFAAAAAA, true);

        this.editButton.setPosition(editX, centerY);
        this.editButton.extractRenderState(context, mouseX, mouseY, tickDelta);

        this.deleteButton.setPosition(deleteX, centerY);
        this.deleteButton.extractRenderState(context, mouseX, mouseY, tickDelta);
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
