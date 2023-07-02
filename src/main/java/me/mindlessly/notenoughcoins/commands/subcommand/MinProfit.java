package me.mindlessly.notenoughcoins.commands.subcommand;

import me.mindlessly.notenoughcoins.configuration.ConfigHandler;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class MinProfit implements Subcommand {
	public MinProfit() {

	}

	public static void updateConfig() {

	}

	@Override
	public String getCommandName() {
		return "minprofit";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public String getCommandUsage() {
		return "";
	}

	@Override
	public String getCommandDescription() {
		return "Set your minimum profit";
	}

	@Override
	public boolean processCommand(ICommandSender sender, String[] args) {
		if (args.length != 1) {
			return false;
		}

		try {
			int minProfit = Integer.parseInt(args[0]);
			if (minProfit < 0 || minProfit > 50000000) {
				sender.addChatMessage(
						new ChatComponentText(EnumChatFormatting.RED + "Only accepting values between 0 and 5000000!"));
				return false;
			}
			ConfigHandler.write("minProfit", Utils.gson.toJsonTree(minProfit));
			sender.addChatMessage(new ChatComponentText(
					EnumChatFormatting.GREEN + "Successfully updated Minimum Profit to " + String.valueOf(minProfit)));
			return true;
		} catch (Exception e) {
			sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "That is not a valid integer!"));
			return false;
		}

	}
}