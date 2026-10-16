package com.bvengo.soundcontroller;

//? if neoforge {
//~ if <26.1 'AddClientReloadListenersEvent' -> 'RegisterClientReloadListenersEvent' {
import com.bvengo.soundcontroller.gui.AllSoundOptionsScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(SoundController.MOD_ID)
public final class NeoForgeSoundController {
	public NeoForgeSoundController(IEventBus modEventBus, ModContainer modContainer) {
		SoundController.bootstrap(FMLPaths.CONFIGDIR.get());
		modContainer.registerExtensionPoint(IConfigScreenFactory.class,
			(IConfigScreenFactory) (container, modListScreen) -> new AllSoundOptionsScreen(modListScreen, Minecraft.getInstance().options));
		modEventBus.addListener(this::onClientSetup);
		modEventBus.addListener(this::registerReloadListeners);
	}

	private void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(SoundController::onClientStarted);
	}

	private void registerReloadListeners(RegisterClientReloadListenersEvent event) {
		//? if >=26.1 {
		/*event.addListener(SoundReloadListener.ID, new SoundReloadListener());
		*///?} else {
		event.registerReloadListener(new SoundReloadListener());
		//?}
	}
}
//~}
//?}
