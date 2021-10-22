package me.mindlessly.notenoughcoins.commands;

import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;

public class MinProfit extends CommandBase {

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public String getCommandName() {
		return "minprofit";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/minprofit <key>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		ChatComponentText error = new ChatComponentText(
				EnumChatFormatting.GOLD + ("NEC ") + EnumChatFormatting.RED + ("Usage: /minprofit <Minimum profit>"));
		if (args.length > 0) {
			try {
				Long.valueOf(args[0]);
			}catch(Exception e) {
				sender.addChatMessage(error);
				return;
			}
			ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL, "MinProfit", args[0]);
			ChatComponentText runtext = new ChatComponentText(
				EnumChatFormatting.GOLD + ("NEC ") + EnumChatFormatting.GREEN + ("Minimum profit price set to " + Utils.formatValue(Long.valueOf(args[0])))
			);
			sender.addChatMessage(runtext);
		} else {
			sender.addChatMessage(error);
		}
	}
}
