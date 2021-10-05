package me.mindlessly.notenoughcoins.commands;

import java.util.*;
import me.mindlessly.notenoughcoins.utils.ApiHandler;
import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;

public class Flip extends CommandBase {

	//Take initial set of lbins, take second set, and use compared set to identify the biggest gainers/losers
	public HashMap<String, Double> initialDataset = new HashMap<>();
	public HashMap<String, Double> secondDataset = new HashMap<>();
	public HashMap<String, Double> comparedDataset = new HashMap<>();
	public static double purse;

	private boolean enable = false;
	public String signage = null;

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public String getCommandName() {
		return "flip";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/flip";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		enable = !enable;

		if (enable) {
			ChatComponentText enableText = new ChatComponentText(
				EnumChatFormatting.GOLD + ("NEC ") + EnumChatFormatting.GREEN + ("Flipper alerts enabled.")
			);
			sender.addChatMessage(enableText);
			ApiHandler.getBins(initialDataset);

			Timer timer = new Timer();
			timer.schedule(
				new TimerTask() {
					@Override
					public void run() {
						String name = sender.getName();
						String id = ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "APIKey");
						try {
							ApiHandler.getBins(secondDataset);
						} catch (Exception e) {
							sender.addChatMessage(new ChatComponentText("Could not load BINs."));
						}
						try {
							ApiHandler.updatePurseCoins(id, name);
						} catch (Exception e) {
							sender.addChatMessage(new ChatComponentText("Could not load purse."));
						}

						purse = Math.round(purse);
						ChatComponentText runtext = new ChatComponentText(
							EnumChatFormatting.GOLD + ("NEC ") + EnumChatFormatting.AQUA + ("Suggested Flips:")
						);
						sender.addChatMessage(runtext);
						if (!enable) {
							return;
						}
						for (HashMap.Entry<String, Double> entry : initialDataset.entrySet()) {
							String key = entry.getKey();
							double difference;
							double price1 = initialDataset.get(key);
							double price2;
							//precaution for if entry magically dissapears on website

							if (secondDataset.containsKey(key)) {
								price2 = secondDataset.get(key);
							} else {
								continue;
							}

							if (price1 >= price2) {
								difference = price1 - price2;
								signage = "-";
							} else {
								difference = price2 - price1;
								signage = "+";
							}

							//temporary measure to test if my code is fucked

							if (price2 <= purse && price2 < price1) {
								comparedDataset.put(key, difference);
							}
						}
						//Sorted hashmap by descending order of value (largest % change)
						HashMap<String, Double> unsortedMap = comparedDataset;

						//LinkedHashMap preserve the ordering of elements in which they are inserted
						LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();

						//Use Comparator.reverseOrder() for reverse ordering
						unsortedMap
							.entrySet()
							.stream()
							.sorted(HashMap.Entry.comparingByValue(Comparator.reverseOrder()))
							.forEachOrdered(x -> sortedMap.put(x.getKey(), (double) Math.round(x.getValue())));

						sender.addChatMessage(
							new ChatComponentText(
								EnumChatFormatting.GOLD + "Your Budget: " + EnumChatFormatting.WHITE + (long) purse + "\n"
							)
						);
						int count = 0;
						Data.auctionData.clear();
						Data.auctionData.putAll(sortedMap);
						for (Map.Entry<String, Double> entry : sortedMap.entrySet()) {
							if (count == 3) {
								break;
							}

							sender.addChatMessage(new ChatComponentText(entry.getKey() + signage + entry.getValue().longValue()));

							count++;
						}
						initialDataset.clear();
						initialDataset.putAll(secondDataset);
						secondDataset.clear();
					}
				},
				60000,
				60000
			);
		} else {
			ChatComponentText enableText = new ChatComponentText(
				EnumChatFormatting.GOLD + ("NEC ") + EnumChatFormatting.RED + ("Flipper alerts disabled.")
			);
			sender.addChatMessage(enableText);
		}
	}
}
