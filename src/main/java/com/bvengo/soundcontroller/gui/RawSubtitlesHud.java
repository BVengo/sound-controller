package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.config.VolumeConfig;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.SubtitlesHud.SubtitleEntry;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.SoundListenerTransform;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class RawSubtitlesHud implements SoundInstanceListener {
    private final MinecraftClient client;
    private final VolumeConfig config = VolumeConfig.getInstance();

    private final List<SubtitleEntry> entries = Lists.newArrayList();
    private final List<SubtitleEntry> audibleEntries = new ArrayList<>();

    private int subtitlesWidth = 0;

    private boolean enabled;

    public RawSubtitlesHud(MinecraftClient client) {
        this.client = client;
    }

    private boolean checkEnabled(SoundManager soundManager) {
        // Check if subtitles are enabled in the config and register a listener if so.
        if (config.areSubtitlesEnabled()) {
            soundManager.registerListener(this);
            enabled = true;
        } else {
            soundManager.unregisterListener(this);
            enabled = false;
        }

        return (enabled);
    }

    public void render(DrawContext context) {
        SoundManager soundManager = client.getSoundManager();
        SoundListenerTransform transform = soundManager.getListenerTransform();

        if (!checkEnabled(soundManager)) {
            return;
        }

        updateAudibleEntries(transform);
        if (audibleEntries.isEmpty()) {
            return;
        }

        renderEntries(context, transform);
    }

    private void updateAudibleEntries(SoundListenerTransform transform) {
        audibleEntries.clear();
        subtitlesWidth = 0;

        Vec3d position = transform.position();

        double displayTime = client.options.getNotificationDisplayTime().getValue();
        for (SubtitleEntry subtitleEntry : entries) {
            if (subtitleEntry.canHearFrom(position) && entryShouldBeDisplayed(subtitleEntry, displayTime)) {
                audibleEntries.add(subtitleEntry);

                int entryWidth = client.textRenderer.getWidth(subtitleEntry.getText());
                subtitlesWidth = Math.max(subtitlesWidth, entryWidth);
            }
        }

        subtitlesWidth += client.textRenderer.getWidth("<") + this.client.textRenderer.getWidth(" ")
                + this.client.textRenderer.getWidth(">") + this.client.textRenderer.getWidth(" ");
    }

    private boolean entryShouldBeDisplayed(SubtitleEntry entry, double displayTime) {
        double endTime = entry.getTime() + 3000.0 * displayTime;
        return endTime > (double) Util.getMeasuringTimeMs();
    }

    private void renderEntries(DrawContext context, SoundListenerTransform transform) {
        int scaledWindowWidth = context.getScaledWindowWidth();
        int scaledWindowHeight = context.getScaledWindowHeight();
        int subtitlesMidPoint = subtitlesWidth / 2;
        int textBackgroundColor = client.options.getTextBackgroundColor(0.8F);
        TextRenderer textRenderer = Objects.requireNonNull(client.textRenderer);

        double displayTime = (Double) client.options.getNotificationDisplayTime().getValue();

        // Audio position
        Vec3d position = transform.position();
        Vec3d right = transform.right();
        Vec3d forward = transform.forward();

        // Shared text coords
        int textHeight = 9;
        int halfHeight = textHeight / 2;

        float xPos = scaledWindowWidth - subtitlesMidPoint - 2.0F;

        for (int i = 0; i < audibleEntries.size(); i++) {
            SubtitleEntry subtitleEntry = audibleEntries.get(i);

            // Audio direction
            Vec3d audioDirection = subtitleEntry.getPosition().subtract(position).normalize();
            double rightDot = audioDirection.dotProduct(right);
            double forwardDot = audioDirection.dotProduct(forward);

            // Text details
            Text subtitleText = subtitleEntry.getText();
            int textColour = calculateSubtitleColour(subtitleEntry, displayTime);

            // Text position
            int textWidth = textRenderer.getWidth(subtitleText);
            int halfWidth = -textWidth / 2;

            float yPos = scaledWindowHeight - 35 - i * (textHeight + 1);

            // Text placement
            context.getMatrices().push();
            context.getMatrices().translate(xPos, yPos, 0.0F);
            context.fill(-subtitlesMidPoint - 1, -halfHeight - 1, subtitlesMidPoint + 1, halfHeight + 1,
                    textBackgroundColor);

            // Draw the direction indicators if audio is behind the player
            if (forwardDot < 0.5) {
                if (rightDot > 0.0) {
                    context.drawTextWithShadow(this.client.textRenderer, ">",
                            subtitlesMidPoint - this.client.textRenderer.getWidth(">"), -halfHeight, textColour);
                } else if (rightDot < 0.0) {
                    context.drawTextWithShadow(this.client.textRenderer, "<", -subtitlesMidPoint, -halfHeight,
                            textColour);
                }
            }

            // Draw the text
            context.drawTextWithShadow(textRenderer, subtitleText, halfWidth, -halfHeight, textColour);
            context.getMatrices().pop();
        }
    }

    private int calculateSubtitleColour(SubtitleEntry subtitleEntry, double displayTime) {
        int opacity = MathHelper.floor(MathHelper.clampedLerp(255.0F, 75.0F,
                (float) (Util.getMeasuringTimeMs() - subtitleEntry.getTime()) / (float) (3000.0 * displayTime)));

        int colour = (opacity << 16 | opacity << 8 | opacity) + 0xFF000000; // Direct addition of alpha value
        return colour;
    }

    public void onSoundPlayed(SoundInstance sound, WeightedSoundSet soundSet, float range) {
        if (sound.getSound() == SoundManager.MISSING_SOUND) {
            return;
        }

        Text soundId = Text.of(sound.getId().toString());

        if (!entries.isEmpty()) {
            for (SubtitleEntry subtitleEntry : entries) {
                if (subtitleEntry.getText().equals(soundId)) {
                    subtitleEntry.reset(new Vec3d(sound.getX(), sound.getY(), sound.getZ()));
                    return;
                }
            }
        }

        entries.add(new SubtitleEntry(soundId, range, new Vec3d(sound.getX(), sound.getY(), sound.getZ())));
    }
}
