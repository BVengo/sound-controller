package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.SoundController;
import com.bvengo.soundcontroller.config.VolumeConfig;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.*;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
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

    public RawSubtitlesHud(MinecraftClient client) {
        this.client = client;
    }

    private boolean checkEnabled(SoundManager soundManager) {
        // Check if subtitles are enabled in the config and register a listener if so.
        if (config.areSubtitlesEnabled()) {
            soundManager.registerListener(this);
            return true;
        } else {
            soundManager.unregisterListener(this);
            return false;
        }
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

        Iterator<SubtitleEntry> iterator = entries.iterator();
        while (iterator.hasNext()) {
            SubtitleEntry subtitleEntry = iterator.next();
            if (subtitleEntry.timeExpired(displayTime)) {
                iterator.remove();
                continue;
            }

            if (subtitleEntry.canHearFrom(position)) {
                audibleEntries.add(subtitleEntry);

                int entryWidth = client.textRenderer.getWidth(subtitleEntry.getText());
                subtitlesWidth = Math.max(subtitlesWidth, entryWidth);
            }
        }

        subtitlesWidth += client.textRenderer.getWidth("<") + this.client.textRenderer.getWidth(" ")
                + this.client.textRenderer.getWidth(">") + this.client.textRenderer.getWidth(" ");
    }

    private void renderEntries(DrawContext context, SoundListenerTransform transform) {
        int scaledWindowWidth = context.getScaledWindowWidth();
        int scaledWindowHeight = context.getScaledWindowHeight();
        int subtitlesMidPoint = subtitlesWidth / 2;
        int textBackgroundColor = client.options.getTextBackgroundColor(0.8F);
        TextRenderer textRenderer = Objects.requireNonNull(client.textRenderer);

        double displayTime = client.options.getNotificationDisplayTime().getValue();

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
            Vec3d audioDirection = subtitleEntry.getDirection(position);
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
        int opacity = MathHelper.floor(MathHelper.clampedLerp(255.0F, 75.0F, (float) subtitleEntry.timeRatioExpired(displayTime)));
        return (opacity << 16 | opacity << 8 | opacity) + Colors.BLACK; // Direct addition of alpha value
    }

    public void onSoundPlayed(SoundInstance sound, WeightedSoundSet soundSet, float range) {
        if (sound.getSound() == SoundManager.MISSING_SOUND) {
            return;
        }

        Text soundId = Text.of(sound.getId().toString());

        for (SubtitleEntry subtitleEntry : entries) {
            if (subtitleEntry.getText().equals(soundId)) {
                subtitleEntry.reset(sound.getX(), sound.getY(), sound.getZ());
                return;
            }
        }

        entries.add(new SubtitleEntry(soundId, range, new Vec3d(sound.getX(), sound.getY(), sound.getZ())));
    }

    @Environment(value=EnvType.CLIENT)
    static class SubtitleEntry {
        private final Text text;
        private final float range;
        private long time;
        private Vec3d pos;

        public SubtitleEntry(Text text, float range, Vec3d pos) {
            this.text = text;
            this.range = range;
            this.pos = pos;
            this.time = Util.getMeasuringTimeMs();
        }

        public Text getText() {
            return this.text;
        }

        public boolean timeExpired(double displayTime) {
            return (double)(Util.getMeasuringTimeMs() - time) > (displayTime * 3000);
        }

        public double timeRatioExpired(double displayTime) {
            return (Util.getMeasuringTimeMs() - time) / (3000.0 * displayTime);
        }

        public boolean canHearFrom(Vec3d pos) {
            if (Float.isInfinite(this.range)) {
                return true;
            }
            return pos.isInRange(this.pos, this.range);
        }

        public Vec3d getDirection(Vec3d pos) {
            return this.pos.subtract(pos).normalize();
        }

        public void reset(double x, double y, double z) {
            this.time = Util.getMeasuringTimeMs();
            this.pos = new Vec3d(x, y, z);
        }
    }
}
