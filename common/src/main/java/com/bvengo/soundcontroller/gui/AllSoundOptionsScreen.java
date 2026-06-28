package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.config.VolumeConfig;
import com.bvengo.soundcontroller.gui.regions.RegionsTab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.MenuTabBar;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;

import static com.bvengo.soundcontroller.Translations.SOUND_SCREEN_TITLE;

public class AllSoundOptionsScreen extends Screen {
    private final VolumeConfig config = VolumeConfig.getInstance();
    private final Screen parent;
    private final Options options;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final TabManager tabManager = new TabManager(this::addRenderableWidget, this::removeWidget);

    private MenuTabBar tabNavigationBar;
    private GlobalSoundTab globalTab;
    private RegionsTab regionsTab;
    private Button addRegionButton;

    public AllSoundOptionsScreen(Screen parent, Options options) {
        super(SOUND_SCREEN_TITLE);
        this.parent = parent;
        this.options = options;
    }

    @Override
    protected void init() {
        this.globalTab = new GlobalSoundTab(this, this.options);
        this.regionsTab = new RegionsTab(this, this.options);

        this.tabNavigationBar = MenuTabBar.builder(this.tabManager, this.width)
            .addTab(this.globalTab)
            .addTab(this.regionsTab)
            .build();
        this.addRenderableWidget(this.tabNavigationBar);

        this.addRegionButton = Button.builder(Translations.translatableOf("region.add"),
            b -> this.regionsTab.openAddScreen()).build();
        LinearLayout footerButtons = LinearLayout.horizontal().spacing(8);
        footerButtons.addChild(this.addRegionButton);
        footerButtons.addChild(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose()).build());
        this.layout.addToFooter(footerButtons);

        this.tabNavigationBar.selectTab(0, false);
        this.repositionElements();
        this.layout.visitWidgets(this::addRenderableWidget);

        this.setInitialFocus(this.globalTab.getSearchField());
    }

    @Override
    protected void repositionElements() {
        if (this.tabNavigationBar == null) return;

        this.tabNavigationBar.arrangeElements(this.width);
        int tabBottom = this.tabNavigationBar.getRectangle().bottom();

        ScreenRectangle contentArea = new ScreenRectangle(
            0, tabBottom, this.width, this.height - this.layout.getFooterHeight() - tabBottom
        );
        this.tabManager.setTabArea(contentArea);

        this.layout.setHeaderHeight(tabBottom);
        this.layout.arrangeElements();

        if (this.addRegionButton != null) {
            boolean inWorld = Minecraft.getInstance().level != null;
            this.addRegionButton.active = inWorld;
            this.addRegionButton.setTooltip(inWorld ? null
                : Tooltip.create(Translations.translatableOf("region.add.requires_world")));
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
