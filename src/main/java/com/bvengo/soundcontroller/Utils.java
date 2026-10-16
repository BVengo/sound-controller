package com.bvengo.soundcontroller;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;

public class Utils {
    public static void updateExistingSounds() {
        //? >=26.1 {
        /*Minecraft.getInstance().getSoundManager().refreshCategoryVolume(SoundSource.AMBIENT);
        *///?} elif 1.21.1 {
        Minecraft.getInstance().getSoundManager().updateSourceVolume(
            SoundSource.AMBIENT,
            Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.AMBIENT)
        );
        //?}
    }
}
