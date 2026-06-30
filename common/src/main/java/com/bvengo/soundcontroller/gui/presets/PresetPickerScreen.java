package com.bvengo.soundcontroller.gui.presets;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.config.PresetConfig;
import com.bvengo.soundcontroller.config.PresetData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;

import java.util.List;
import java.util.function.Consumer;

public class PresetPickerScreen extends Screen {
    private final Screen parent;
    private final Consumer<PresetData> onApply;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

    private PickerListWidget listWidget;

    public PresetPickerScreen(Screen parent, Consumer<PresetData> onApply) {
        super(Translations.translatableOf("preset.picker.title"));
        this.parent = parent;
        this.onApply = onApply;
    }

    @Override
    protected void init() {
        layout.removeChildren();

        LinearLayout header = LinearLayout.vertical().spacing(4);
        header.addChild(new StringWidget(this.title, font));
        header.addChild(new StringWidget(Translations.translatableOf("preset.picker.subtitle"), font));
        layout.addToHeader(header);

        listWidget = new PickerListWidget(minecraft, width, 0, 0);
        addRenderableWidget(listWidget);

        layout.addToFooter(Button.builder(CommonComponents.GUI_CANCEL, b -> closeToParent()).build());
        layout.visitWidgets(this::addRenderableWidget);

        repositionElements();
        loadPresets();
    }

    @Override
    protected void repositionElements() {
        layout.arrangeElements();
        if (listWidget != null) {
            int headerHeight = layout.getHeaderHeight();
            int listHeight = height - headerHeight - layout.getFooterHeight();
            listWidget.updateSizeAndPosition(width, listHeight, headerHeight);
        }
    }

    private void loadPresets() {
        listWidget.clearEntries();
        for (PresetData preset : PresetConfig.getInstance().getPresets()) {
            listWidget.addWidgetEntry(new PickerEntry(preset, font));
        }
    }

    private void applyPreset(PresetData preset) {
        onApply.accept(preset);
        closeToParent();
    }

    @Override
    public void onClose() {
        closeToParent();
    }

    private void closeToParent() {
        minecraft.setScreenAndShow(parent);
    }

    private class PickerEntry extends ContainerObjectSelectionList.Entry<PickerEntry> {
        private static final int BUTTON_HEIGHT = 20;
        private static final int LOAD_WIDTH = 50;

        private final PresetData preset;
        private final Font font;
        private final Button loadButton;

        PickerEntry(PresetData preset, Font font) {
            this.preset = preset;
            this.font = font;
            this.loadButton = Button.builder(Translations.translatableOf("preset.load.apply"),
                b -> applyPreset(preset))
                .size(LOAD_WIDTH, BUTTON_HEIGHT)
                .build();
        }

        @Override
        public void extractContent(GuiGraphicsExtractor context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int entryLeft = getX() + 8;
            int entryRight = getX() + getWidth() - 8;
            int centerY = getY() + (getHeight() - BUTTON_HEIGHT) / 2;

            int loadX = entryRight - LOAD_WIDTH;
            int nameWidth = loadX - 8 - entryLeft;

            String name = font.plainSubstrByWidth(preset.getName(), Math.max(0, nameWidth));
            context.text(font, name, entryLeft, centerY + 1, 0xFFFFFFFF, true);

            loadButton.setPosition(loadX, centerY);
            loadButton.extractRenderState(context, mouseX, mouseY, tickDelta);
        }

        @Override
        public List<Button> children() {
            return List.of(loadButton);
        }

        @Override
        public List<Button> narratables() {
            return List.of(loadButton);
        }
    }

    private static class PickerListWidget extends ContainerObjectSelectionList<PickerEntry> {
        private static final int ROW_HEIGHT = 30;
        private static final int ROW_WIDTH = 360;
        private static final int HORIZONTAL_MARGIN = 32;
        private static final int SCROLLBAR_RIGHT_PADDING = 8;

        PickerListWidget(Minecraft client, int width, int height, int y) {
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

        public void addWidgetEntry(PickerEntry entry) {
            this.addEntry(entry);
        }
    }
}
