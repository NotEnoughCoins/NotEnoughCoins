package me.mindlessly.notenoughcoins.commands.subcommands;

import me.mindlessly.notenoughcoins.Main;
import net.minecraft.command.ICommandSender;

public class Help implements Subcommand {
    @Override
    public String getCommandName() {
        return "help";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "";
    }

    @Override
    public boolean processCommand(ICommandSender sender, String[] args) {
        Main.commandManager.sendHelp(sender);
        return true;
    }
}
