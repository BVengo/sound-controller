package com.bvengo.soundcontroller;

import com.bvengo.soundcontroller.mixin.SoundManagerAccessor;
import com.bvengo.soundcontroller.mixin.SoundSystemAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.AggressiveBeeSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class Utils {
	public static void updateExistingSounds() {
		SoundSystemAccessor soundSystem = (SoundSystemAccessor) ((SoundManagerAccessor) MinecraftClient.getInstance().getSoundManager()).getSoundSystem();
		// Trigger updates for all existing sounds. AMBIENT is an arbitrary category - as long as it isn't MASTER, all existing volumes will be updated.
		soundSystem.invokeUpdateSoundVolume(PositionedSoundInstance.ambient(SoundEvents.AMBIENT_UNDERWATER_ENTER), 1.0f);
	}
}
