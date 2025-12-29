package com.bvengo.soundcontroller;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;

public class Utils {
	public static void updateExistingSounds() {
        Minecraft.getInstance().getSoundManager().refreshCategoryVolume(SoundSource.AMBIENT);
	}
}
