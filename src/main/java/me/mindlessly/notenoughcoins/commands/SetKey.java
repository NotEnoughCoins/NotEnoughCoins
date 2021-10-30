package me.mindlessly.notenoughcoins.commands;

import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;

public class SetKey extends CommandBase {

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public String getCommandName() {
		return "neckey";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/neckey <key>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length > 0) {
			ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL, "APIKey", args[0]);
			ChatComponentText runtext = new ChatComponentText(
					EnumChatFormatting.GOLD + ("[NEC] ") + EnumChatFormatting.GREEN + ("API Key set to " + args[0]));
			sender.addChatMessage(runtext);
		} else {
			ChatComponentText error = new ChatComponentText(
					EnumChatFormatting.GOLD + ("[NEC] ") + EnumChatFormatting.RED + ("Usage: /neckey <key>"));
			sender.addChatMessage(error);
		}
	}
}
