package me.mindlessly.notenoughcoins.commands.subcommands;

import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
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
            ChatComponentText runtext = new ChatComponentText(
                    EnumChatFormatting.GOLD + ("NEC ") + EnumChatFormatting.GREEN
                            + ("Minimum profit price set to " + Utils.formatValue(Long.parseLong(args[0]))));
            sender.addChatMessage(runtext);
            return true;
        } else {
            return false;
        }
    }
}
