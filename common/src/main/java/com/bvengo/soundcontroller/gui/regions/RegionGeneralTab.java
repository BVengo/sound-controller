package com.bvengo.soundcontroller.gui.regions;

import com.bvengo.soundcontroller.SoundController;
import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.region.BoxGeometry;
import com.bvengo.soundcontroller.region.RegionData;
import com.bvengo.soundcontroller.region.RegionGeometry;
import com.bvengo.soundcontroller.region.SphereGeometry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

import java.util.Optional;
import java.util.function.Consumer;

import static com.bvengo.soundcontroller.Translations.translatableOf;

public class RegionGeneralTab implements Tab {

    enum GeometryType {
        SPHERE, BOX;

        Component getDisplayComponent() {
            return Translations.translatableOf("region.geometry." + name().toLowerCase());
        }
    }

    private final Layout layout = LinearLayout.vertical();
    private final String worldKey;

    private GeometryType geometryType;

    private final EditBox nameField;
    private final Checkbox enabledCheckbox;
    private final CycleButton<GeometryType> geometryTypeButton;

    private final StringWidget sphereGroupLabel;
    private final EditBox sxField, syField, szField;
    private final Button spherePosButton;
    private final StringWidget radiusGroupLabel;
    private final EditBox radiusField;

    private final StringWidget box1GroupLabel;
    private final EditBox bx1Field, by1Field, bz1Field;
    private final Button corner1PosButton;
    private final StringWidget box2GroupLabel;
    private final EditBox bx2Field, by2Field, bz2Field;
    private final Button corner2PosButton;

    private final StringWidget worldInfoLabel;

    public RegionGeneralTab(RegionData existingRegion, String worldKey, String initialName) {
        this.worldKey = worldKey;
        Font font = Minecraft.getInstance().font;

        double initSx = 0, initSy = 0, initSz = 0, initRadius = 16;
        double initBx1 = 0, initBy1 = 0, initBz1 = 0, initBx2 = 0, initBy2 = 0, initBz2 = 0;
        boolean initEnabled = true;

        if (existingRegion != null) {
            initEnabled = existingRegion.isEnabled();
            RegionGeometry geo = existingRegion.getGeometry();
            if (geo instanceof BoxGeometry box) {
                geometryType = GeometryType.BOX;
                initBx1 = box.x1; initBy1 = box.y1; initBz1 = box.z1;
                initBx2 = box.x2; initBy2 = box.y2; initBz2 = box.z2;
            } else if (geo instanceof SphereGeometry sphere) {
                geometryType = GeometryType.SPHERE;
                initSx = sphere.x; initSy = sphere.y; initSz = sphere.z;
                initRadius = sphere.radius;
            } else {
                geometryType = GeometryType.SPHERE;
            }
        } else {
            geometryType = GeometryType.SPHERE;
            var player = Minecraft.getInstance().player;
            if (player != null) {
                initSx = Math.round(player.getX());
                initSy = Math.round(player.getY());
                initSz = Math.round(player.getZ());
            }
        }

        nameField = new EditBox(font, 0, 0, 200, 20, translatableOf("region.name.hint"));
        nameField.setMaxLength(64);
        nameField.setHint(translatableOf("region.name.hint"));
        nameField.setValue(initialName);

        enabledCheckbox = Checkbox.builder(translatableOf("region.enabled"), font)
            .selected(initEnabled)
            .build();

        geometryTypeButton = CycleButton.<GeometryType>builder(GeometryType::getDisplayComponent, geometryType)
            .withValues(GeometryType.SPHERE, GeometryType.BOX)
            .create(0, 0, 200, 20, translatableOf("region.geometry.type"),
                (btn, val) -> { geometryType = val; updateGeometryVisibility(); });

        sphereGroupLabel = new StringWidget(translatableOf("region.sphere.center"), font);
        sxField = coordField(font, "X", initSx);
        syField = coordField(font, "Y", initSy);
        szField = coordField(font, "Z", initSz);
        spherePosButton = Button.builder(translatableOf("region.use_position"),
            b -> fillPosition(sxField, syField, szField)).size(200, 20).build();
        radiusGroupLabel = new StringWidget(translatableOf("region.sphere.radius"), font);
        radiusField = new EditBox(font, 0, 0, 200, 20, Component.literal("16"));
        radiusField.setValue(fmt(initRadius));

        box1GroupLabel = new StringWidget(translatableOf("region.box.corner1"), font);
        bx1Field = coordField(font, "X", initBx1);
        by1Field = coordField(font, "Y", initBy1);
        bz1Field = coordField(font, "Z", initBz1);
        corner1PosButton = Button.builder(translatableOf("region.use_position"),
            b -> fillPosition(bx1Field, by1Field, bz1Field)).size(200, 20).build();
        box2GroupLabel = new StringWidget(translatableOf("region.box.corner2"), font);
        bx2Field = coordField(font, "X", initBx2);
        by2Field = coordField(font, "Y", initBy2);
        bz2Field = coordField(font, "Z", initBz2);
        corner2PosButton = Button.builder(translatableOf("region.use_position"),
            b -> fillPosition(bx2Field, by2Field, bz2Field)).size(200, 20).build();

        worldInfoLabel = new StringWidget(
            Component.literal(worldKey).withStyle(s -> s.withColor(0x888888)), font);

        updatePositionButtonState();
        updateGeometryVisibility();
    }

    @Override
    public Component getTabTitle() {
        return translatableOf("tab.general");
    }

    @Override
    public Component getTabExtraNarration() {
        return Component.empty();
    }

    @Override
    public void visitChildren(Consumer<AbstractWidget> consumer) {
        consumer.accept(nameField);
        consumer.accept(enabledCheckbox);
        consumer.accept(geometryTypeButton);
        consumer.accept(sphereGroupLabel);
        consumer.accept(sxField);
        consumer.accept(syField);
        consumer.accept(szField);
        consumer.accept(spherePosButton);
        consumer.accept(radiusGroupLabel);
        consumer.accept(radiusField);
        consumer.accept(box1GroupLabel);
        consumer.accept(bx1Field);
        consumer.accept(by1Field);
        consumer.accept(bz1Field);
        consumer.accept(corner1PosButton);
        consumer.accept(box2GroupLabel);
        consumer.accept(bx2Field);
        consumer.accept(by2Field);
        consumer.accept(bz2Field);
        consumer.accept(corner2PosButton);
        consumer.accept(worldInfoLabel);
    }

    @Override
    public void doLayout(ScreenRectangle rect) {
        int top = rect.top() + 8;
        int left = rect.left() + 8;
        int w = rect.width() - 16;
        int gap = 12;
        int lw = (w - gap) / 2;
        int rw = w - lw - gap;
        int rl = left + lw + gap;
        int cw = (rw - 8) / 3;

        // Left column: name + enabled checkbox
        nameField.setPosition(left, top);
        nameField.setWidth(lw);
        enabledCheckbox.setPosition(left, top + 28);

        // Right column: geometry type + sphere/box fields
        geometryTypeButton.setPosition(rl, top);
        geometryTypeButton.setWidth(rw);

        sphereGroupLabel.setPosition(rl, top + 28);
        sxField.setPosition(rl, top + 40);
        sxField.setWidth(cw);
        syField.setPosition(rl + cw + 4, top + 40);
        syField.setWidth(cw);
        szField.setPosition(rl + 2 * (cw + 4), top + 40);
        szField.setWidth(cw);
        spherePosButton.setPosition(rl, top + 68);
        spherePosButton.setWidth(rw);
        radiusGroupLabel.setPosition(rl, top + 96);
        radiusField.setPosition(rl, top + 108);
        radiusField.setWidth(rw);

        box1GroupLabel.setPosition(rl, top + 28);
        bx1Field.setPosition(rl, top + 40);
        bx1Field.setWidth(cw);
        by1Field.setPosition(rl + cw + 4, top + 40);
        by1Field.setWidth(cw);
        bz1Field.setPosition(rl + 2 * (cw + 4), top + 40);
        bz1Field.setWidth(cw);
        corner1PosButton.setPosition(rl, top + 68);
        corner1PosButton.setWidth(rw);
        box2GroupLabel.setPosition(rl, top + 96);
        bx2Field.setPosition(rl, top + 108);
        bx2Field.setWidth(cw);
        by2Field.setPosition(rl + cw + 4, top + 108);
        by2Field.setWidth(cw);
        bz2Field.setPosition(rl + 2 * (cw + 4), top + 108);
        bz2Field.setWidth(cw);
        corner2PosButton.setPosition(rl, top + 136);
        corner2PosButton.setWidth(rw);

        // World info pinned to bottom-right
        int bottom = rect.bottom() - 8;
        worldInfoLabel.setPosition(rl, bottom - 10);

        updatePositionButtonState();
        updateGeometryVisibility();
    }

    @Override
    public Layout getLayout() {
        return layout;
    }

    public String getCurrentName() {
        return nameField.getValue();
    }

    public boolean extractEnabled() {
        return enabledCheckbox.selected();
    }

    public Optional<String> extractName() {
        String name = nameField.getValue().trim();
        if (name.isEmpty()) {
            nameField.setTextColor(0xFF5555);
            return Optional.empty();
        }
        nameField.setTextColor(0xFFFFFF);
        return Optional.of(name);
    }

    public Optional<RegionGeometry> extractGeometry() {
        if (geometryType == GeometryType.SPHERE) {
            double sx = parseCoord(sxField);
            double sy = parseCoord(syField);
            double sz = parseCoord(szField);
            double r  = parseCoord(radiusField);
            if (Double.isNaN(sx) || Double.isNaN(sy) || Double.isNaN(sz) || Double.isNaN(r)) {
                return Optional.empty();
            }
            if (r <= 0) {
                radiusField.setTextColor(0xFF5555);
                return Optional.empty();
            }
            return Optional.of(new SphereGeometry(sx, sy, sz, r));
        } else {
            double x1 = parseCoord(bx1Field), y1 = parseCoord(by1Field), z1 = parseCoord(bz1Field);
            double x2 = parseCoord(bx2Field), y2 = parseCoord(by2Field), z2 = parseCoord(bz2Field);
            if (Double.isNaN(x1) || Double.isNaN(y1) || Double.isNaN(z1)
                    || Double.isNaN(x2) || Double.isNaN(y2) || Double.isNaN(z2)) {
                return Optional.empty();
            }
            return Optional.of(new BoxGeometry(x1, y1, z1, x2, y2, z2));
        }
    }

    private void updateGeometryVisibility() {
        boolean sphere = geometryType == GeometryType.SPHERE;
        sphereGroupLabel.visible = sphere;
        sxField.visible = sphere;
        syField.visible = sphere;
        szField.visible = sphere;
        spherePosButton.visible = sphere;
        radiusGroupLabel.visible = sphere;
        radiusField.visible = sphere;

        box1GroupLabel.visible = !sphere;
        bx1Field.visible = !sphere;
        by1Field.visible = !sphere;
        bz1Field.visible = !sphere;
        corner1PosButton.visible = !sphere;
        box2GroupLabel.visible = !sphere;
        bx2Field.visible = !sphere;
        by2Field.visible = !sphere;
        bz2Field.visible = !sphere;
        corner2PosButton.visible = !sphere;
    }

    private void fillPosition(EditBox x, EditBox y, EditBox z) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        if (!SoundController.getCurrentWorldKey().equals(worldKey)) return;
        x.setValue(fmt(Math.round(player.getX())));
        y.setValue(fmt(Math.round(player.getY())));
        z.setValue(fmt(Math.round(player.getZ())));
    }

    private void updatePositionButtonState() {
        Minecraft mc = Minecraft.getInstance();
        boolean inWorld = mc.player != null && mc.level != null;
        boolean sameWorld = inWorld && SoundController.getCurrentWorldKey().equals(worldKey);

        Tooltip tooltip = null;
        if (!inWorld) {
            tooltip = Tooltip.create(translatableOf("region.use_position.requires_world"));
        } else if (!sameWorld) {
            tooltip = Tooltip.create(translatableOf("region.use_position.different_world"));
        }

        setPositionButtonState(spherePosButton, sameWorld, tooltip);
        setPositionButtonState(corner1PosButton, sameWorld, tooltip);
        setPositionButtonState(corner2PosButton, sameWorld, tooltip);
    }

    private void setPositionButtonState(Button button, boolean active, Tooltip tooltip) {
        button.active = active;
        button.setTooltip(tooltip);
    }

    private double parseCoord(EditBox field) {
        try {
            double v = Double.parseDouble(field.getValue());
            field.setTextColor(0xFFFFFF);
            return v;
        } catch (NumberFormatException e) {
            field.setTextColor(0xFF5555);
            return Double.NaN;
        }
    }

    private EditBox coordField(Font font, String placeholder, double initial) {
        EditBox f = new EditBox(font, 0, 0, 60, 20, Component.literal(placeholder));
        f.setValue(fmt(initial));
        return f;
    }

    static String fmt(double v) {
        return v == Math.floor(v) ? String.valueOf((long) v) : String.valueOf(v);
    }
}
