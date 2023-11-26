package me.mindlessly.notenoughcoins.commands.subcommand;
import me.mindlessly.notenoughcoins.configuration.ConfigHandler;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class MaxCost implements Subcommand {
	public MaxCost() {

	}

	public static void updateConfig() {

	}

	@Override
	public String getCommandName() {
		return "minpercent";
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
		return "Set the maximum buying price for a flip";
	}

	@Override
	public boolean processCommand(ICommandSender sender, String[] args) {
		if (args.length != 1) {
			return false;
		}

		try {
			int maxCost = Integer.parseInt(args[0]);
			if(maxCost < 0) {
				sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Only accepting integers greater than or equal to 0!"));
				return false;
			}
			ConfigHandler.write("maxcost", Utils.gson.toJsonTree(maxCost));
			sender.addChatMessage(new ChatComponentText(
					EnumChatFormatting.GREEN + "Successfully updated Minimum Percentage Profit to " + String.valueOf(maxCost)));
			return true;
		} catch (Exception e) {
			sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "That is not a valid integer!"));
			return false;
		}

	}
}