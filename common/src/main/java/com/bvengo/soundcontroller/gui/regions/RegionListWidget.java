package com.bvengo.soundcontroller.gui.regions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

public class RegionListWidget extends ContainerObjectSelectionList<RegionListEntry> {
    private static final int ROW_HEIGHT = 30;

    public RegionListWidget(Minecraft client, int width, int height, int y) {
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

    public void addWidgetEntry(RegionListEntry entry) {
        this.addEntry(entry);
    }
}
