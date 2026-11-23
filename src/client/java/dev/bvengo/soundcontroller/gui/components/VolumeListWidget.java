package dev.bvengo.soundcontroller.gui.components;

import dev.bvengo.modcore.client.ui.ScrollableList;
import net.minecraft.client.Minecraft;

public final class VolumeListWidget extends ScrollableList<VolumeWidgetEntry> {
    public VolumeListWidget(Minecraft minecraft, int width, int height, int y) {
        super(minecraft, width, height, y, 25, VolumeWidgetEntry.totalWidth, 0, 8);
    }

    @Override
    public int getRowWidth() {
        return VolumeWidgetEntry.totalWidth;
    }
}
