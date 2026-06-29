package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.config.VolumeConfig;
import com.bvengo.soundcontroller.gui.presets.PresetEditScreen;
import com.bvengo.soundcontroller.gui.presets.PresetsTab;
import com.bvengo.soundcontroller.gui.regions.RegionsTab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.MenuTabBar;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;

import java.util.ArrayList;
import java.util.List;

import static com.bvengo.soundcontroller.Translations.SOUND_SCREEN_TITLE;

public class AllSoundOptionsScreen extends Screen {
    private final VolumeConfig config = VolumeConfig.getInstance();
    private final Screen parent;
    private final Options options;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final TabManager tabManager = new TabManager(this::addRenderableWidget, this::removeWidget);

    private int currentTabIndex = 0;

    private MenuTabBar tabNavigationBar;
    private GlobalSoundTab globalTab;
    private RegionsTab regionsTab;
    private PresetsTab presetsTab;

    private Button doneButton;
    private Button addRegionButton;
    private Button addPresetButton;

    public AllSoundOptionsScreen(Screen parent, Options options) {
        super(SOUND_SCREEN_TITLE);
        this.parent = parent;
        this.options = options;
    }

    @Override
    protected void init() {
        this.globalTab = new GlobalSoundTab(this, this.options, () -> updateFooterButtons(0, false, false));
        this.regionsTab = new RegionsTab(this, this.options, () -> updateFooterButtons(1, true, false));
        this.presetsTab = new PresetsTab(this, () -> updateFooterButtons(2, false, true));

        this.tabNavigationBar = MenuTabBar.builder(this.tabManager, this.width)
            .addTab(this.globalTab)
            .addTab(this.regionsTab)
            .addTab(this.presetsTab)
            .build();
        this.addRenderableWidget(this.tabNavigationBar);

        // Only Done goes in the layout footer — conditional buttons are positioned manually
        this.doneButton = Button.builder(CommonComponents.GUI_DONE, b -> this.onClose()).build();
        this.layout.addToFooter(this.doneButton);

        this.addRegionButton = Button.builder(Translations.translatableOf("region.add"),
            b -> this.regionsTab.openAddScreen()).build();
        this.addRegionButton.visible = false;

        this.addPresetButton = Button.builder(Translations.translatableOf("preset.add"),
            b -> Minecraft.getInstance().setScreenAndShow(new PresetEditScreen(this, null))).build();
        this.addPresetButton.visible = false;

        this.tabNavigationBar.selectTab(this.currentTabIndex, false);
        this.repositionElements();
        this.layout.visitWidgets(this::addRenderableWidget);
        this.addRenderableWidget(this.addRegionButton);
        this.addRenderableWidget(this.addPresetButton);

        this.setInitialFocus(this.globalTab.getSearchField());
    }

    @Override
    protected void repositionElements() {
        if (this.tabNavigationBar == null) return;

        this.tabNavigationBar.arrangeElements(this.width);
        int tabBottom = this.tabNavigationBar.getRectangle().bottom();

        // Arrange layout (positions doneButton) BEFORE setting tab area so
        // updateFooterButtons() can read doneButton.getY() when doLayout() fires.
        this.layout.setHeaderHeight(tabBottom);
        this.layout.arrangeElements();

        ScreenRectangle contentArea = new ScreenRectangle(
            0, tabBottom, this.width, this.height - this.layout.getFooterHeight() - tabBottom
        );
        this.tabManager.setTabArea(contentArea);
    }

    // Called by each tab's doLayout() — sets conditional button visibility and re-centres the footer row.
    public void updateFooterButtons(int tabIndex, boolean showRegion, boolean showPreset) {
        if (this.doneButton == null) return;

        this.currentTabIndex = tabIndex;
        this.addRegionButton.visible = showRegion;
        this.addPresetButton.visible = showPreset;

        if (showRegion) {
            boolean inWorld = Minecraft.getInstance().level != null;
            this.addRegionButton.active = inWorld;
            this.addRegionButton.setTooltip(inWorld ? null
                : Tooltip.create(Translations.translatableOf("region.add.requires_world")));
        }

        List<Button> row = new ArrayList<>();
        if (showRegion) row.add(this.addRegionButton);
        if (showPreset) row.add(this.addPresetButton);
        row.add(this.doneButton);

        int spacing = 8;
        int totalWidth = row.stream().mapToInt(AbstractWidget::getWidth).sum()
            + spacing * (row.size() - 1);
        int x = (this.width - totalWidth) / 2;
        int y = this.doneButton.getY();
        for (Button b : row) {
            b.setPosition(x, y);
            x += b.getWidth() + spacing;
        }
    }

    @Override
    public void resize(int width, int height) {
        String search = this.globalTab != null ? this.globalTab.getSearchValue() : "";
        super.resize(width, height);
        if (this.globalTab != null) {
            this.globalTab.setSearchValue(search);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreenAndShow(this.parent);
    }

    @Override
    public void removed() {
        config.save();
    }
}
