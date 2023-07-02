package me.mindlessly.notenoughcoins.commands.subcommand;
import me.mindlessly.notenoughcoins.utils.ApiHandler;
import me.mindlessly.notenoughcoins.utils.Blacklist;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class BlacklistCommand implements Subcommand {
	public BlacklistCommand() {

	}

	public static void updateConfig() {

	}

	@Override
	public String getCommandName() {
		return "blacklist";
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
		return "Add or remove items from the blacklist";
	}

	@Override
	public boolean processCommand(ICommandSender sender, String[] args) {
		if (args.length < 2) {
			return false;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < args.length; i++) {
			sb.append(args[i]).append(" ");
		}
		String name = sb.toString().trim();
		
		boolean item = false;
		boolean pet = false;
		
		if(ApiHandler.items.containsKey(name)) {
			item = true;
		}
		if(ApiHandler.pets.has(name)) {
			pet = true;
		}

		if (!item && !pet) {
			sender.addChatMessage(new ChatComponentText("Item does not exist, Note: CaSe SeNsItIvE"));
			return false;
		}
		if (args[0].equals("add")) {
			if(item) {
				Blacklist.add(ApiHandler.items.get(name));
			}else {
				Blacklist.addPet(ApiHandler.pets.get(name).getAsJsonArray());
			}
			sender.addChatMessage(new ChatComponentText("Successfully added " + EnumChatFormatting.GREEN + name
					+ EnumChatFormatting.WHITE + " to the blacklist"));
		} else if (args[0].equals("remove")) {
			if(item) {
				Blacklist.remove(ApiHandler.items.get(name));
			}else {
				Blacklist.removePet(ApiHandler.pets.get(name).getAsJsonArray());
			}
			sender.addChatMessage(new ChatComponentText("Successfully removed " + EnumChatFormatting.GREEN + name
					+ EnumChatFormatting.WHITE + " from the blacklist"));

		} else {
			return false;
		}
		return true;
	}
}