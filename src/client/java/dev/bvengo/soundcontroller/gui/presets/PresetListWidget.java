package dev.bvengo.soundcontroller.gui.presets;

import dev.bvengo.modcore.client.ui.ScrollableList;
import net.minecraft.client.Minecraft;

public final class PresetListWidget extends ScrollableList<PresetListEntry> {
    public PresetListWidget(Minecraft minecraft, int width, int height, int y) {
        super(minecraft, width, height, y, 30, 360, 32, 8);
    }
}
