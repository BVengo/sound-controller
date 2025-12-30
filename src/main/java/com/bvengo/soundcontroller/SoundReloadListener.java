package com.bvengo.soundcontroller;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

public class SoundReloadListener implements ResourceManagerReloadListener {
	public static final Identifier ID = Identifier.fromNamespaceAndPath(SoundController.MOD_ID, "reload_listener");

	/**
	 * Invoked every time client resources are reloaded
	 * (e.g. F3+T, resource pack change, initial load).
	 *
	 * @param resourceManager the active resource manager
	 */
	@Override
	public void onResourceManagerReload(final @NotNull ResourceManager resourceManager) {
		if (SoundController.CONFIG == null) {
			return;
		}

		SoundController.CONFIG.updateVolumes();
	}
}
