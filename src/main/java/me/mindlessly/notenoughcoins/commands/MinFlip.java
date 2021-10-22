package me.mindlessly.notenoughcoins.commands;

import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;

public class MinFlip extends CommandBase {

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public String getCommandName() {
		return "minflip";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/minflip <key>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		ChatComponentText error = new ChatComponentText(
				EnumChatFormatting.GOLD + ("NEC ") + EnumChatFormatting.RED + ("Usage: /minflip <Minimum price you would like flips to be>"));
		if (args.length > 0) {
			try {
				Long.valueOf(args[0]);
			}catch(Exception e) {
				sender.addChatMessage(error);
				return;
			}
			ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL, "MinFlip", args[0]);
			ChatComponentText runtext = new ChatComponentText(
				EnumChatFormatting.GOLD + ("NEC ") + EnumChatFormatting.GREEN + ("Minimum flip price set to " + Utils.formatValue(Long.valueOf(args[0])))
			);
			sender.addChatMessage(runtext);
		} else {
			sender.addChatMessage(error);
		}
	}
}
