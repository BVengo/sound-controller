package com.bvengo.soundcontroller.gui.presets;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.VolumeData;
import com.bvengo.soundcontroller.config.PresetConfig;
import com.bvengo.soundcontroller.config.PresetData;
import com.bvengo.soundcontroller.config.VolumeConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class PresetCreateScreen extends Screen {
    private final Screen parent;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

    private EditBox nameField;

    public PresetCreateScreen(Screen parent) {
        super(Translations.translatableOf("preset.create"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        nameField = new EditBox(font, 0, 0, 200, 20, Translations.translatableOf("preset.name.hint"));
        nameField.setMaxLength(64);

        LinearLayout center = LinearLayout.vertical().spacing(8);
        center.addChild(nameField);
        layout.addToContents(center);

        LinearLayout footerButtons = LinearLayout.horizontal().spacing(8);
        Button createButton = Button.builder(Translations.translatableOf("preset.create.confirm"), b -> savePreset()).build();
        footerButtons.addChild(createButton);
        footerButtons.addChild(Button.builder(CommonComponents.GUI_CANCEL, b -> onClose()).build());
        layout.addToFooter(footerButtons);

        layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
        setInitialFocus(nameField);
    }

    @Override
    protected void repositionElements() {
        layout.arrangeElements();
    }

    private void savePreset() {
        String name = nameField.getValue().trim();
        if (name.isEmpty()) return;

        HashMap<Identifier, Float> sounds = new HashMap<>();
        for (Map.Entry<Identifier, VolumeData> entry : VolumeConfig.getInstance().getVolumes().entrySet()) {
            if (entry.getValue().isModified()) {
                sounds.put(entry.getKey(), entry.getValue().getVolume());
            }
        }

        PresetConfig presetConfig = PresetConfig.getInstance();
        presetConfig.addPreset(new PresetData(name, sounds));
        presetConfig.save();

        onClose();
    }

    @Override
    public void onClose() {
        minecraft.setScreenAndShow(parent);
    }
}
