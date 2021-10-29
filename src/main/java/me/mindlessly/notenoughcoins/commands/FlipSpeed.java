package me.mindlessly.notenoughcoins.commands;

import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;

public class FlipSpeed extends CommandBase {

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public String getCommandName() {
		return "flipspeed";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/flipspeed <speed>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		ChatComponentText error = new ChatComponentText(
				EnumChatFormatting.GOLD + ("NEC ") + EnumChatFormatting.RED + ("Usage: /flipspeed <speed>"));
		if (args.length > 0 ) {
			try {
				int speed = Integer.valueOf(args[0]);
				if(speed > 10) {
					sender.addChatMessage(error);
					return;
				}
			} catch (Exception e) {
				sender.addChatMessage(error);
				return;
			}
			ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL, "FlipSpeed", args[0]);
			ChatComponentText runtext = new ChatComponentText(
					EnumChatFormatting.GOLD + ("NEC ") + EnumChatFormatting.GREEN
							+ ("Flip speed set to " + args[0]));
			sender.addChatMessage(runtext);
		} else {
			sender.addChatMessage(error);
		}
	}
}
