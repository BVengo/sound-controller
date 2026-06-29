package com.bvengo.soundcontroller.gui.presets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

public class PresetListWidget extends ContainerObjectSelectionList<PresetListEntry> {
    private static final int ROW_HEIGHT = 30;

    public PresetListWidget(Minecraft client, int width, int height, int y) {
        super(client, width, height, y, ROW_HEIGHT);
        this.centerListVertically = false;
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    public void clearEntries() {
        super.clearEntries();
    }

    public void addWidgetEntry(PresetListEntry entry) {
        this.addEntry(entry);
    }
}
