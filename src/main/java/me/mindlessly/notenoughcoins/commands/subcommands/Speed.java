package me.mindlessly.notenoughcoins.commands.subcommands;

import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
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
                if (speed > 10) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
            ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL, "FlipSpeed", args[0]);
            ChatComponentText runtext = new ChatComponentText(
                    EnumChatFormatting.GOLD + ("NEC ") + EnumChatFormatting.GREEN
                            + ("Flip speed set to " + args[0]));
            sender.addChatMessage(runtext);
            return true;
        } else {
            return false;
        }
    }
}
