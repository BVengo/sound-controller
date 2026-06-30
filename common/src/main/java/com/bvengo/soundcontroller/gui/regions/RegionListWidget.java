package com.bvengo.soundcontroller.gui.regions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

public class RegionListWidget extends ContainerObjectSelectionList<RegionListEntry> {
    private static final int ROW_HEIGHT = 30;
    private static final int ROW_WIDTH = 520;
    private static final int HORIZONTAL_MARGIN = 32;
    private static final int SCROLLBAR_RIGHT_PADDING = 8;

    public RegionListWidget(Minecraft client, int width, int height, int y) {
        super(client, width, height, y, ROW_HEIGHT);
        this.centerListVertically = false;
    }

    @Override
    public int getRowWidth() {
        return Math.min(ROW_WIDTH, Math.max(0, this.width - HORIZONTAL_MARGIN));
    }

    @Override
    protected int scrollBarX() {
        return Math.min(super.scrollBarX(), getRight() - SCROLLBAR_RIGHT_PADDING);
    }

    @Override
    public void clearEntries() {
        super.clearEntries();
    }

    public void addWidgetEntry(RegionListEntry entry) {
        this.addEntry(entry);
    }
}
