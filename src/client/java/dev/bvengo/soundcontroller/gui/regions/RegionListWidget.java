package dev.bvengo.soundcontroller.gui.regions;

import dev.bvengo.modcore.client.ui.ScrollableList;
import net.minecraft.client.Minecraft;

public final class RegionListWidget extends ScrollableList<RegionListEntry> {
    public RegionListWidget(Minecraft minecraft, int width, int height, int y) {
        super(minecraft, width, height, y, 30, 520, 32, 8);
    }
}
