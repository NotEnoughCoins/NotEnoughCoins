package me.mindlessly.notenoughcoins.events;

import gg.essential.universal.UChat;
import me.mindlessly.notenoughcoins.config.Config;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatReceivedEvent {
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void chat(ClientChatReceivedEvent event) {
		if (!event.message.getUnformattedText().startsWith("Your new API key is "))
			return;
		String key = event.message.getUnformattedText().split("key is ")[1];
		Config.apiKey = key;
		UChat.chat(EnumChatFormatting.GOLD + ("NEC ") + "§aAPI Key set to " + key);
	}
}
