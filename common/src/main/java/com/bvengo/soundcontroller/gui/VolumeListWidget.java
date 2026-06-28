package com.bvengo.soundcontroller.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

/**
 * The list widget that contains all the individual records. Contains a list of {@link VolumeWidgetEntry}.
 */
public class VolumeListWidget extends ContainerObjectSelectionList<VolumeWidgetEntry> {
    private static final int rowWidth = VolumeWidgetEntry.totalWidth;
    private static final int rowHeight = 25;

    public VolumeListWidget(Minecraft client, int width, int height, int y) {
        super(client, width, height, y, rowHeight);
        this.centerListVertically = false;
    }

    public void addWidgetEntry(VolumeWidgetEntry widget) {
        this.addEntry(widget);
    }

    @Override
    public int getRowWidth() {
        return rowWidth;
    }

    @Override
    public void clearEntries() {
        super.clearEntries();
    }
}
