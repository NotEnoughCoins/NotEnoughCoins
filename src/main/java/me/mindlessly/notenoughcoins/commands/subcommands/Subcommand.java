package me.mindlessly.notenoughcoins.commands.subcommands;

import net.minecraft.command.ICommandSender;

public interface Subcommand {
	String getCommandName();

	/**
	 * Returns a String that will be shown after the subcommand name in the help
	 * menu and error message. It will look like: /nec subcommand (usage)
	 */
	String getCommandUsage(ICommandSender sender);

	/**
	 * This function will be called upon execution of the subcommand, and should
	 * return a boolean deciding whether the command succeeded or not, which will
	 * send an error message if not. Note: The arguments will not include the
	 * subcommand itself, example: /nec subcommand arg1 arg2 will have {"arg1",
	 * "arg2"} as its args
	 */
	boolean processCommand(ICommandSender sender, String[] args);
}
