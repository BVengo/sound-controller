package com.bvengo.soundcontroller.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ElementListWidget;

/**
 * The list widget that contains all the individual records. Contains a list of {@link VolumeWidgetEntry}.
 */
@Environment(value=EnvType.CLIENT)
public class VolumeListWidget extends ElementListWidget<VolumeWidgetEntry> {
    private static final int rowWidth = VolumeWidgetEntry.totalWidth;
    private static final int rowHeight = 25;

    public VolumeListWidget(MinecraftClient client, int width, int i, GameOptionsScreen optionsScreen) {
        super(client, width, optionsScreen.layout.getContentHeight(), optionsScreen.layout.getHeaderHeight(), rowHeight);
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

