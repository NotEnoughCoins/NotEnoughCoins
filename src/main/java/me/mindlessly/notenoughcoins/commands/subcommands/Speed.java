package me.mindlessly.notenoughcoins.commands.subcommands;

import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;

public class Speed implements Subcommand {
    public Speed() {
    }

    @Override
    public String getCommandName() {
        return "speed";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "<speed>";
    }

    @Override
    public boolean processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0) {
            try {
                int speed = Integer.parseInt(args[0]);
                if (speed > Runtime.getRuntime().availableProcessors() || speed < 1) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
            ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL, "FlipSpeed", args[0]);
            Utils.sendMessageWithPrefix("&aFlip speed set to " + args[0], sender);
            return true;
        } else {
            return false;
        }
    }
}
