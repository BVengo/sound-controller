package com.bvengo.soundcontroller;

import net.minecraft.resources.Identifier;

import java.util.Objects;

public class VolumeData {
    public static final Float DEFAULT_VOLUME = 1.0f;

    private final Identifier soundId;
	private float volume;

    public VolumeData(Identifier id, float volume) {
        this.soundId = id;
        this.volume = volume; // Removed clamping to allow manually setting over / under the slider
    }

    public VolumeData(Identifier id) {
        this(id, DEFAULT_VOLUME);
    }

    public Identifier getId() {
		return this.soundId;
    }

	public float getVolume() {
		return this.volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public boolean isModified() {
		return !Objects.equals(DEFAULT_VOLUME, this.volume);
    }

    public boolean inFilter(String search, boolean showModifiedOnly) {
		return this.soundId.toString().toLowerCase().contains(search) &&
				(!showModifiedOnly || this.isModified());
    }

	@Override
	public final boolean equals(Object o) {
		if (!(o instanceof VolumeData that)) return false;

		return Objects.equals(this.soundId, that.soundId) && Objects.equals(this.getVolume(), that.getVolume());
    }

	@Override
	public int hashCode() {
		int result = Objects.hashCode(this.soundId);
		result = 31 * result + Objects.hashCode(this.getVolume());
		return result;
	}
}
