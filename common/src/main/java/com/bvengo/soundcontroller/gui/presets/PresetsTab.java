package com.bvengo.soundcontroller.gui.presets;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.config.PresetConfig;
import com.bvengo.soundcontroller.config.PresetData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class PresetsTab implements Tab {
    private final Screen screen;
    private final Layout layout = LinearLayout.vertical();
    private final Runnable onSelected;

    private final PresetListWidget presetListWidget;

    public PresetsTab(Screen screen, Runnable onSelected) {
        this.screen = screen;
        this.onSelected = onSelected;
        this.presetListWidget = new PresetListWidget(Minecraft.getInstance(), 200, 100, 0);
    }

    @Override
    public Component getTabTitle() {
        return Translations.translatableOf("tab.presets");
    }

    @Override
    public Component getTabExtraNarration() {
        return Component.empty();
    }

    @Override
    public void visitChildren(Consumer<AbstractWidget> consumer) {
        consumer.accept(presetListWidget);
    }

    @Override
    public void doLayout(ScreenRectangle rect) {
        this.onSelected.run();
        presetListWidget.updateSizeAndPosition(rect.width(), rect.height(), rect.top());
        loadPresets();
    }

    @Override
    public Layout getLayout() {
        return layout;
    }

    private void loadPresets() {
        presetListWidget.clearEntries();
        Font font = Minecraft.getInstance().font;
        for (PresetData preset : PresetConfig.getInstance().getPresets()) {
            presetListWidget.addWidgetEntry(new PresetListEntry(preset, screen, font));
        }
    }
}
