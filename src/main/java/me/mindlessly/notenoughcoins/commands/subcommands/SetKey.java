package me.mindlessly.notenoughcoins.commands.subcommands;

import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;

public class SetKey implements Subcommand {
    public SetKey() {
    }

    @Override
    public String getCommandName() {
        return "setkey";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "<api key>";
    }

    @Override
    public boolean processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0) {
            ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL, "APIKey", args[0]);
            Utils.sendMessageWithPrefix("&aAPI Key set to " + args[0], sender);
            return true;
        } else {
            return false;
        }
    }
}
