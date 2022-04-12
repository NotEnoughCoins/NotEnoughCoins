package me.mindlessly.tokenmanager.events;

import me.mindlessly.tokenmanager.Authenticator;
import me.mindlessly.tokenmanager.gui.TokenManager;
import me.mindlessly.tokenmanager.handlers.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiEvents {
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void initGui(GuiScreenEvent.InitGuiEvent.Post event) {
		GuiScreen gui = event.gui;
		int xSize = gui.width - 10;
		int ySize = gui.height - 10;
		int guiLeft = (gui.width - xSize) / 2;
		int guiTop = (gui.height - ySize) / 2;

		// Inject token manager button into Main Menu
		if (event.gui instanceof GuiMainMenu) {
			GuiButton tm = new GuiButton(7, guiLeft, guiTop, "Token Manager");
			event.buttonList.add(tm);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	// Display the Token Manager Menu
	public void onButtonClick(GuiScreenEvent.ActionPerformedEvent event) {
		if (event.gui instanceof GuiMainMenu) {
			if (event.button.id == 7) {
				Minecraft.getMinecraft().displayGuiScreen(new TokenManager());
			}
		}
		// Save and exit the menu and load new session
		if (event.gui instanceof TokenManager) {
			TokenManager gui = (TokenManager) event.gui;
			if (event.button.id == 0) {
				ConfigHandler.writeStringConfig(Configuration.CATEGORY_GENERAL, "name", gui.name.getText());
				ConfigHandler.writeStringConfig(Configuration.CATEGORY_GENERAL, "uuid", gui.uuid.getText());
				ConfigHandler.writeStringConfig(Configuration.CATEGORY_GENERAL, "token", gui.token.getText());
				Authenticator.reAuth(gui.name.getText(), gui.uuid.getText(), gui.token.getText());
				Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());

			}
		}
	}
}
