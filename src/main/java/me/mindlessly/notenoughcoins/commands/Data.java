package me.mindlessly.notenoughcoins.commands;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class Data extends CommandBase {

	public static HashMap<String, Double> auctionData = new HashMap<>();

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public String getCommandName() {
		return "necdata";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/necdata";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (auctionData.size() > 0) {
			for (Map.Entry<String, Double> entry : auctionData.entrySet()) {
				sender.addChatMessage(new ChatComponentText(entry.getKey() + "" + entry.getValue().toString()));
			}
		} else {
			ChatComponentText errorText = new ChatComponentText(
				EnumChatFormatting.GOLD + ("NEC ") + EnumChatFormatting.RED + ("No Auction Data Found.")
			);
			sender.addChatMessage(errorText);
		}
	}
}
