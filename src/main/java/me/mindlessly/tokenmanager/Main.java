package me.mindlessly.tokenmanager;

import me.mindlessly.tokenmanager.events.GuiEvents;
import me.mindlessly.tokenmanager.handlers.ConfigHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class Main {
	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new GuiEvents());

		// If the user has previously entered a token then we validate with that token
		// If anyone can be bothered to go through the code and add the validation checks I couldn't be bothered to add that would be great!
		if (!ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "token").equals(null)) {
			Authenticator.reAuth(ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "name"),
					ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "uuid"),
					ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "token"));
		}
	}
}
