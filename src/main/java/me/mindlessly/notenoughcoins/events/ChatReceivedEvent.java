package me.mindlessly.notenoughcoins.events;

import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

public class ChatReceivedEvent {
    @SubscribeEvent
    public void chat(@Nonnull ClientChatReceivedEvent event) {
        if(!event.message.getUnformattedText().startsWith("Your new API key is ")) return;
        String key = event.message.getChatStyle().getChatClickEvent().getValue();
        ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL, "APIKey", key);
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + ("NEC ") + "§aAPI Key set to " + key));
    }
}
