package me.mindlessly.notenoughcoins.commands.subcommands;

import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;

public class AlertSound implements Subcommand {
	public AlertSound() {
	}

	@Override
	public String getCommandName() {
		return "alertsound";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "";
	}

	@Override
	public boolean processCommand(ICommandSender sender, String[] args) {
		if (ConfigHandler.hasKey(Configuration.CATEGORY_GENERAL, "alertsound")) {
			if (ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "alertsound").equals("true")) {
				Toggle.alertSound = false;
				ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL, "alertsound", "false");
				Utils.sendMessageWithPrefix("&cAlert sound disabled", sender);
			} else {
				Toggle.alertSound = true;
				ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL, "alertsound", "true");
				Utils.sendMessageWithPrefix("&aAlert sound enabled", sender);
			}
		} else {
			ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL, "alertsound", "true");
			Utils.sendMessageWithPrefix("&aAlert sound enabled", sender);
		}
		return true;
	}
}
