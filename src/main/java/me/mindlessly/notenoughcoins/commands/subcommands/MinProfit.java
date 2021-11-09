package me.mindlessly.notenoughcoins.commands.subcommands;

import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;

public class MinProfit implements Subcommand {
    public MinProfit() {
    }

    @Override
    public String getCommandName() {
        return "minprofit";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "<minimum profit>";
    }

    @Override
    public boolean processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0) {
            try {
                Long.valueOf(args[0]);
            } catch (Exception e) {
                return false;
            }
            ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL, "MinProfit", args[0]);
            Utils.sendMessageWithPrefix("&aMinimum profit price set to " + Utils.formatValue(Long.parseLong(args[0])), sender);
            return true;
        } else {
            return false;
        }
    }
}
