package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.Translations;
import com.bvengo.soundcontroller.Utils;
import com.bvengo.soundcontroller.VolumeData;
import com.bvengo.soundcontroller.gui.buttons.AudioButtonWidget;
import com.bvengo.soundcontroller.gui.buttons.TriggerButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ElementListWidget.Entry;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * A widget entry allowing control of a single volume. Should be used in a {@link VolumeListWidget}.
 */
public class VolumeWidgetEntry extends Entry<VolumeWidgetEntry> {
    private final VolumeData volumeData;
    private final SoundManager soundManager;
    private final Screen screen;
    private final GameOptions gameOptions;

    private static final int sliderWidth = 310;
    private static final int buttonSize = 20;
    private static final int paddingAfterSearch = 8;
    private static final int paddingBetweenButtons = 4;
    public static final int totalWidth = sliderWidth + buttonSize * 2 + paddingAfterSearch + paddingBetweenButtons;

    public SimpleOption<Double> volumeOption;

    public ClickableWidget volumeSlider;
    public TriggerButtonWidget playSoundButton;
    public TriggerButtonWidget resetButton;
    private TextFieldWidget manualInputField;
    private boolean manualEditActive;
    private Float manualOriginalVolume;

    private static final float MAX_VOLUME = 2.0f;
    private static final int ERROR_COLOR = 0xFF5555;
    private static final int[] BLOCKED_NAV_KEYS = new int[] {
            GLFW.GLFW_KEY_UP,
            GLFW.GLFW_KEY_DOWN,
            GLFW.GLFW_KEY_LEFT,
            GLFW.GLFW_KEY_RIGHT,
            GLFW.GLFW_KEY_TAB
    };

    public VolumeWidgetEntry(VolumeData volumeData, Screen screen, GameOptions gameOptions) {
        this.volumeData = volumeData;
        this.screen = screen;
        this.gameOptions = gameOptions;
        this.soundManager = MinecraftClient.getInstance().getSoundManager();

        init();
    }

    private int getPercentageValue(double value) {
        return (int) Math.round(value * 100);
    }

    private float getVolumeFromSlider(double value) {
        // 1 -> MAX_VALUE
        // Requires multiplying by 100 to round the value to 2dp.
        return Math.round(value * MAX_VOLUME * 100) / 100.0f;
    }

    private void addSlider() {
        // Volume slider (options)
        this.volumeOption = new SimpleOption<>(
                volumeData.getId().toString(),
                SimpleOption.emptyTooltip(),
                (prefix, value) -> {
                    // Use volumeData instead of value, noting that it gets updated immediately by the slider as well.
                    // This allows us to list the actual volume (which may be over/under set), not whatever the slider
                    // is clamped to.
                    int volume = getPercentageValue(volumeData.getVolume());

                    if (volume == 0) {
                        return Text.translatable("options.generic_value", prefix, ScreenTexts.OFF);
                    }

                    if (volume > MAX_VOLUME * 100 || volume < 0) {
                        // Make the value red if it's over the max
                        return Text.translatable("options.generic_value",
                                prefix,
                                Text.literal(volume + "%").styled(style -> style.withColor(ERROR_COLOR))
                        );
                    }

                    return Text.translatable("options.percent_value", prefix, volume);
                },
                SimpleOption.DoubleSliderCallbacks.INSTANCE,
                Math.clamp(volumeData.getVolume().doubleValue() / MAX_VOLUME, 0.0, 1.0),
                value -> {
                    volumeData.setVolume(getVolumeFromSlider(value));
                    Utils.updateExistingSounds();
                });

        // Volume slider (widget, created from options)
        this.volumeSlider = volumeOption.createWidget(gameOptions, 0, 0, sliderWidth);
    }

    public void rebuildSlider() {
        addSlider();
    }

    private void addPlayButton() {
        this.playSoundButton = new AudioButtonWidget(0, 0, buttonSize, buttonSize, soundManager, volumeData);
    }

    private void addResetButton() {

        this.resetButton = new TriggerButtonWidget("reset", 0, 0, buttonSize, buttonSize,
                (button) -> {
                    volumeData.setVolume(VolumeData.DEFAULT_VOLUME);
                    this.addSlider();  // Update the slider to reflect the new volume
                    Utils.updateExistingSounds();
                });

        this.resetButton.setTooltip(Tooltip.of(Translations.RESET_BUTTON_TOOLTIP));
    }

    private void init() {
        addSlider();
        addPlayButton();
        addResetButton();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int leftSide = (this.screen.width - totalWidth) / 2;
        int top = getY();

        this.volumeSlider.setPosition(leftSide, top);
        if (this.manualEditActive && this.manualInputField != null) {
            this.manualInputField.setPosition(leftSide, top);
            this.manualInputField.setWidth(sliderWidth);
            this.manualInputField.render(context, mouseX, mouseY, tickDelta);
        } else {
            this.volumeSlider.render(context, mouseX, mouseY, tickDelta);
        }

        this.playSoundButton.setPosition(volumeSlider.getRight() + paddingAfterSearch, top);
        this.playSoundButton.render(context, mouseX, mouseY, tickDelta);

        this.resetButton.setPosition(playSoundButton.getRight() + paddingBetweenButtons, top);
        this.resetButton.render(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public List<? extends Element> children() {
        if (this.manualEditActive && this.manualInputField != null) {
            return List.of(manualInputField, playSoundButton, resetButton);
        }

        return List.of(volumeSlider, playSoundButton, resetButton);
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        if (this.manualEditActive && this.manualInputField != null) {
            return List.of(manualInputField, playSoundButton, resetButton);
        }

        return List.of(volumeSlider, playSoundButton, resetButton);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubleClick) {
        if (click.button() == 1 && isMouseWithinEntry(click.x(), click.y())) {
            startManualEdit();
            return true;
        }

        if (this.manualEditActive && this.manualInputField != null && this.manualInputField.mouseClicked(click, doubleClick)) {
            return true;
        }

        return super.mouseClicked(click, doubleClick);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (this.manualEditActive && this.manualInputField != null && this.manualInputField.mouseReleased(click)) {
            return true;
        }

        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        if (this.manualEditActive && this.manualInputField != null && this.manualInputField.mouseDragged(click, deltaX, deltaY)) {
            return true;
        }

        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (this.manualEditActive && this.manualInputField != null) {
            int key = input.key();
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                cancelManualEdit();
                return true;
            }

            if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
                return commitManualEdit();
            }

            if (isBlockedNavigationKey(key)) {
                return true;
            }

            if (this.manualInputField.keyPressed(input)) {
                return true;
            }
        }

        return super.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        if (this.manualEditActive && this.manualInputField != null && this.manualInputField.keyReleased(input)) {
            return true;
        }

        return super.keyReleased(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (this.manualEditActive && this.manualInputField != null && this.manualInputField.charTyped(input)) {
            return true;
        }

        return super.charTyped(input);
    }

    private boolean isMouseWithinEntry(double mouseX, double mouseY) {
        if (this.volumeSlider == null) {
            return false;
        }

        int leftSide = (this.screen.width - totalWidth) / 2;
        int rightSide = leftSide + totalWidth;
        int top = this.getY();
        int bottom = top + Math.max(this.volumeSlider.getHeight(), buttonSize);

        return mouseX >= leftSide && mouseX <= rightSide && mouseY >= top && mouseY <= bottom;
    }

    private void startManualEdit() {
        if (this.manualEditActive) {
            return;
        }

        this.manualOriginalVolume = this.volumeData.getVolume();
        this.manualInputField = new TextFieldWidget(
                MinecraftClient.getInstance().textRenderer,
                0,
                0,
                sliderWidth,
                this.volumeSlider.getHeight(),
                Text.empty()
        );
        this.manualInputField.setMaxLength(16);
        this.manualInputField.setDrawsBackground(true);
        this.manualInputField.setText("");
        this.manualInputField.setSuggestion(Translations.MANUAL_INPUT_PLACEHOLDER.getString());
        this.manualInputField.setPlaceholder(Translations.MANUAL_INPUT_PLACEHOLDER);
        this.manualInputField.setChangedListener(value -> resetManualInputFieldStyle());
        this.manualInputField.setFocused(true);
        this.setFocused(this.manualInputField);
        resetManualInputFieldStyle();
        this.manualEditActive = true;
    }

    private boolean commitManualEdit() {
        if (!this.manualEditActive || this.manualInputField == null) {
            return false;
        }

        Float parsedValue = parseManualInput(this.manualInputField.getText());
        if (parsedValue == null) {
            indicateManualInputError();
            return true;
        }

        this.volumeData.setVolume(parsedValue);
        Utils.updateExistingSounds();
        stopManualEdit();
        return true;
    }

    private void cancelManualEdit() {
        if (!this.manualEditActive) {
            return;
        }

        if (this.manualOriginalVolume != null) {
            this.volumeData.setVolume(this.manualOriginalVolume);
            Utils.updateExistingSounds();
        }

        stopManualEdit();
    }

    private void stopManualEdit() {
        this.manualEditActive = false;
        this.manualInputField = null;
        this.manualOriginalVolume = null;
        this.setFocused(null);
        rebuildSlider();
    }

    private Float parseManualInput(String input) {
        if (input == null) {
            return null;
        }

        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        boolean endsWithPercent = trimmed.endsWith("%");
        if (endsWithPercent) {
            trimmed = trimmed.substring(0, trimmed.length() - 1).trim();
            if (trimmed.isEmpty()) {
                return null;
            }
        }

        try {
            double value = Double.parseDouble(trimmed);
            value /= 100.0;

            if (Double.isNaN(value) || Double.isInfinite(value) || value < 0) {
                return null;
            }

            return (float) value;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void resetManualInputFieldStyle() {
        if (this.manualInputField != null) {
            this.manualInputField.setEditableColor(TextFieldWidget.DEFAULT_EDITABLE_COLOR);
        }
    }

    private void indicateManualInputError() {
        if (this.manualInputField != null) {
            this.manualInputField.setEditableColor(ERROR_COLOR);
        }
    }

    private boolean isBlockedNavigationKey(int key) {
        for (int blocked : BLOCKED_NAV_KEYS) {
            if (blocked == key) {
                return true;
            }
        }

        return false;
    }
}
