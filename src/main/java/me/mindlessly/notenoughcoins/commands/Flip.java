package me.mindlessly.notenoughcoins.commands;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.mindlessly.notenoughcoins.utils.ApiHandler;
import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;

public class Flip extends CommandBase {

	//Take initial set of lbins, take second set, and use compared set to identify the biggest gainers/losers
	//namedSet is used to replace internal ids with actual item names
	public HashMap<String, Double> initialDataset = new HashMap<>();
	public HashMap<String, Double> secondDataset = new HashMap<>();
	public HashMap<String, Double> comparedDataset = new HashMap<>();
	public static LinkedHashMap<String, Double> namedDataset = new LinkedHashMap<>();
	public static double purse;

	private boolean enable = false;
	public String signage = null;
	public Timer timer = new Timer();

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

			
			timer.schedule(
				new TimerTask() {
					@Override
					public void run() {
						comparedDataset.clear();
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
							

							if (secondDataset.containsKey(key)) {
								price2 = secondDataset.get(key);
								
								//Anything below this margin is quite useless
								if (price1 > price2) {
									difference = price1 - price2;
									signage = "-";
									comparedDataset.put(key, difference);
									
								} else {
									continue;
								}
								
							} else {
								continue;
							}
									
						}
						
						//Sorted hashmap by descending order of value (largest change)
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
						namedDataset.clear();
						namedDataset.putAll(sortedMap);
						ApiHandler.itemIdsToNames(namedDataset);
						Data.auctionData.putAll(secondDataset);
						for (Map.Entry<String, Double> entry : namedDataset.entrySet()) {
							if (count == 3) {
								break;
							}

							DecimalFormat formatter = new DecimalFormat("#,###.00");
							sender.addChatMessage(new ChatComponentText(entry.getKey() +" "+ signage + formatter.format(entry.getValue().longValue())));

							count++;
						}
						initialDataset.clear();
						try {
							ApiHandler.getBins(initialDataset);
						}catch(Exception e) {
							initialDataset.putAll(secondDataset);
						}
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
			timer.cancel();
			timer.purge();
			timer = new Timer();
		}
	}
}
