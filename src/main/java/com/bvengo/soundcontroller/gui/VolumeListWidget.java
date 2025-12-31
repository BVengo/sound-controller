package com.bvengo.soundcontroller.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

/**
 * The list widget that contains all the individual records. Contains a list of {@link VolumeWidgetEntry}.
 */
@Environment(EnvType.CLIENT)
public class VolumeListWidget extends ContainerObjectSelectionList<VolumeWidgetEntry> {
    private static final int ROW_HEIGHT = 25;

    public VolumeListWidget(Minecraft client) {
        super(client, 1, 1, 1, ROW_HEIGHT);
        this.centerListVertically = false;
    }

    public void addWidgetEntry(VolumeWidgetEntry widget) {
        this.addEntry(widget);
    }

    @Override
    public int getRowWidth() {
        return Math.max(this.getWidth() - 50, 100);
    }

    @Override
    public void clearEntries() {
        super.clearEntries();
    }
}
