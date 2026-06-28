package com.bvengo.soundcontroller.gui.regions;

import com.bvengo.soundcontroller.SoundController;
import com.bvengo.soundcontroller.VolumeData;
import com.bvengo.soundcontroller.config.RegionConfig;
import com.bvengo.soundcontroller.region.RegionData;
import com.bvengo.soundcontroller.region.RegionGeometry;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.tabs.MenuTabBar;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.bvengo.soundcontroller.Translations.translatableOf;

public class RegionEditScreen extends Screen {
    private final Screen parent;
    private final RegionData existingRegion;
    private final Options options;

    private final String serverKey;
    private final String worldKey;

    private String workingName;

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final TabManager tabManager = new TabManager(this::addRenderableWidget, this::removeWidget);

    private MenuTabBar tabNavigationBar;
    private RegionGeneralTab generalTab;
    private RegionSoundsTab soundsTab;

    public RegionEditScreen(Screen parent, RegionData existingRegion) {
        super(existingRegion == null
            ? translatableOf("region.create")
            : translatableOf("region.edit.title"));
        this.parent = parent;
        this.existingRegion = existingRegion;
        this.options = net.minecraft.client.Minecraft.getInstance().options;

        if (existingRegion != null) {
            serverKey = existingRegion.getServerKey();
            worldKey = existingRegion.getWorldKey();
            workingName = existingRegion.getName();
        } else {
            serverKey = SoundController.getCurrentServerKey();
            worldKey = SoundController.getCurrentWorldKey();
            workingName = "";
        }
    }

    @Override
    protected void init() {
        generalTab = new RegionGeneralTab(existingRegion, serverKey, worldKey, workingName);
        soundsTab = new RegionSoundsTab(this, options, existingRegion);

        tabNavigationBar = MenuTabBar.builder(tabManager, width)
            .addTab(generalTab)
            .addTab(soundsTab)
            .build();
        addRenderableWidget(tabNavigationBar);

        LinearLayout footerButtons = LinearLayout.horizontal().spacing(8);
        footerButtons.addChild(Button.builder(translatableOf("region.save"),
            b -> { if (validateAndSave()) onClose(); }).build());
        footerButtons.addChild(Button.builder(CommonComponents.GUI_CANCEL,
            b -> onClose()).build());
        layout.addToFooter(footerButtons);

        tabNavigationBar.selectTab(0, false);
        repositionElements();
        layout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    protected void repositionElements() {
        if (tabNavigationBar == null) return;

        tabNavigationBar.arrangeElements(width);
        int tabBottom = tabNavigationBar.getRectangle().bottom();

        ScreenRectangle contentArea = new ScreenRectangle(
            0, tabBottom, width, height - layout.getFooterHeight() - tabBottom
        );
        tabManager.setTabArea(contentArea);

        layout.setHeaderHeight(tabBottom);
        layout.arrangeElements();
    }

    @Override
    public void resize(int width, int height) {
        workingName = generalTab != null ? generalTab.getCurrentName() : workingName;
        String search = soundsTab != null ? soundsTab.getSearchValue() : "";
        super.resize(width, height);
        if (soundsTab != null) soundsTab.setSearchValue(search);
    }

    private boolean validateAndSave() {
        Optional<String> name = generalTab.extractName();
        Optional<RegionGeometry> geometry = generalTab.extractGeometry();

        if (name.isEmpty() || geometry.isEmpty()) {
            tabNavigationBar.selectTab(0, true);
            return false;
        }

        Map<Identifier, VolumeData> overrides = soundsTab.getSoundOverrides();

        RegionConfig regionConfig = RegionConfig.getInstance();
        boolean enabled = generalTab.extractEnabled();
        if (existingRegion != null) {
            existingRegion.setName(name.get());
            existingRegion.setEnabled(enabled);
            existingRegion.setGeometry(geometry.get());
            existingRegion.setSoundOverrides(new HashMap<>(overrides));
        } else {
            RegionData newRegion = new RegionData(name.get(), serverKey, worldKey, geometry.get());
            newRegion.setEnabled(enabled);
            newRegion.setSoundOverrides(new HashMap<>(overrides));
            regionConfig.addRegion(newRegion);
        }
        regionConfig.save();
        return true;
    }

    @Override
    public void onClose() {
        minecraft.setScreenAndShow(parent);
    }
}
