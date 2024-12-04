package com.bvengo.soundcontroller;

import com.bvengo.soundcontroller.mixin.SoundManagerAccessor;
import com.bvengo.soundcontroller.mixin.SoundSystemAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;

public class Utils {
	public static void updateExistingSounds() {
		SoundSystemAccessor soundSystem = (SoundSystemAccessor) ((SoundManagerAccessor) MinecraftClient.getInstance().getSoundManager()).getSoundSystem();
		// Trigger updates for all existing sounds. AMBIENT is an arbitrary category - as long as it isn't MASTER, all existing volumes will be updated.
		soundSystem.invokeUpdateSoundVolume(SoundCategory.AMBIENT, 1.0f);
	}
}
