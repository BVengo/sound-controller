package com.bvengo.soundcontroller.gui.regions;

import com.bvengo.soundcontroller.SoundController;
import com.bvengo.soundcontroller.config.RegionConfig;
import com.bvengo.soundcontroller.region.RegionData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class RegionsTab implements Tab {
    private final Screen screen;
    private final Layout layout = LinearLayout.vertical();
    private final Runnable onSelected;

    private final RegionListWidget regionListWidget;

    public RegionsTab(Screen screen, Options options, Runnable onSelected) {
        this.screen = screen;
        this.onSelected = onSelected;
        this.regionListWidget = new RegionListWidget(Minecraft.getInstance(), 200, 100, 0);
    }

    @Override
    public Component getTabTitle() {
        return com.bvengo.soundcontroller.Translations.translatableOf("tab.regions");
    }

    @Override
    public Component getTabExtraNarration() {
        return Component.empty();
    }

    @Override
    public void visitChildren(Consumer<AbstractWidget> consumer) {
        consumer.accept(regionListWidget);
    }

    @Override
    public void doLayout(ScreenRectangle rect) {
        this.onSelected.run();
        regionListWidget.updateSizeAndPosition(rect.width(), rect.height(), rect.top());
        loadRegions();
    }

    @Override
    public Layout getLayout() {
        return layout;
    }

    public void openAddScreen() {
        Minecraft.getInstance().setScreenAndShow(new RegionEditScreen(screen, null));
    }

    private void loadRegions() {
        regionListWidget.clearEntries();
        Font font = Minecraft.getInstance().font;
        String serverKey = SoundController.getCurrentServerKey();
        for (RegionData region : RegionConfig.getInstance().getRegions()) {
            if (region.getServerKey().equals(serverKey)) {
                regionListWidget.addWidgetEntry(new RegionListEntry(region, screen, font));
            }
        }
    }
}
