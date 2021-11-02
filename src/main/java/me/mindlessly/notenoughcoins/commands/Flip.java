package me.mindlessly.notenoughcoins.commands;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.mindlessly.notenoughcoins.utils.ApiHandler;
import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import me.mindlessly.notenoughcoins.utils.Reference;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.config.Configuration;

public class Flip extends CommandBase {

	// Take initial set of lbins, take second set, and use compared set to identify
	// the biggest
	// gainers/losers
	// namedSet is used to replace internal ids with actual item names
	public static LinkedHashMap<String, Double> initialDataset = new LinkedHashMap<>();
	public static LinkedHashMap<String, Double> secondDataset = new LinkedHashMap<>();
	public static LinkedHashMap<String, Double> namedDataset = new LinkedHashMap<>();
	public static LinkedHashMap<Integer, Long> updatedDataset = new LinkedHashMap<>();

	public static double purse;
	public static ArrayList<String> commands = new ArrayList<String>();

	private static int auctionPages = 0;

	private static int flipSpeed = 1;
	public static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(flipSpeed);

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
		if (ConfigHandler.hasKey(Configuration.CATEGORY_GENERAL, "Flip")) {
			if (ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "Flip").equals("true")) {
				ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL, "Flip", "false");

			} else if (ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "Flip").equals("false")) {
				ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL, "Flip", "true");
			}

		} else {
			ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL, "Flip", "true");
		}
		
		if (ConfigHandler.hasKey(Configuration.CATEGORY_GENERAL, "FlipSpeed")) {
			scheduledExecutorService.shutdownNow();
			flipSpeed = Integer.valueOf(ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "FlipSpeed"));
			scheduledExecutorService = Executors.newScheduledThreadPool(flipSpeed);
		}

		flip((EntityPlayer) sender.getCommandSenderEntity());
	}

	public static void flip(EntityPlayer sender) {
		if (ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "Flip").equals("true")) {
			ChatComponentText enableText = new ChatComponentText(
					EnumChatFormatting.GOLD + ("NEC ") + EnumChatFormatting.GREEN + ("Flipper alerts enabled."));
			sender.addChatMessage(enableText);
			try {
				ApiHandler.getBins(initialDataset);
				ApiHandler.itemIdsToNames(initialDataset);
			}
			catch(Exception e) {
				Reference.logger.error(e.getMessage(), e);
			}
			auctionPages = ApiHandler.getNumberOfPages() - 1;
			String name = sender.getName();
			String id = ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "APIKey");
			try {
				ApiHandler.updatePurseCoins(id, name);
			} catch (Exception e) {
				sender.addChatMessage(new ChatComponentText("Could not load purse."));
			}
			for (int i = 0; i < flipSpeed; i++) {
				final int start = i;
				Thread thread = new Thread() {
					public void run() {
						flipper(sender, start, flipSpeed);
				
					}
				};
				thread.start();
			}
				
			scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					auctionPages = ApiHandler.getNumberOfPages() - 1;
					try {
						ApiHandler.getBins(initialDataset);
						ApiHandler.itemIdsToNames(initialDataset);
					} catch (Exception e) {
						sender.addChatMessage(new ChatComponentText("Could not load BINs."));
					}
					String name = sender.getName();
					String id = ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "APIKey");
					try {
						ApiHandler.updatePurseCoins(id, name);
					} catch (Exception e) {
						sender.addChatMessage(new ChatComponentText("Could not load purse."));
					}
				}
			}, 60000, 60000, TimeUnit.MILLISECONDS);

		} else {
			ChatComponentText enableText = new ChatComponentText(
					EnumChatFormatting.GOLD + ("NEC ") + EnumChatFormatting.RED + ("Flipper alerts disabled."));
			sender.addChatMessage(enableText);
			scheduledExecutorService.shutdownNow();
			scheduledExecutorService = Executors.newScheduledThreadPool(flipSpeed);
		}
	}

	public static void flipper(EntityPlayer sender, int start, int increment) {
		for (int iterate = start; iterate < auctionPages; iterate += increment) {
			final int page = iterate;
			scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
				public void run() {
					boolean print = ApiHandler.getFlips(secondDataset, page, commands);
					if (print) {
						if (namedDataset.size() > 0) {
							purse = Math.round(purse);
							int count = 0;

							for (Map.Entry<String, Double> entry : namedDataset.entrySet()) {
								long profit = Math.abs(entry.getValue().longValue());
								IChatComponent result = new ChatComponentText(EnumChatFormatting.AQUA + "[NEC] "
										+ EnumChatFormatting.YELLOW + entry.getKey() + " "
										+ (profit > 200_000 || purse / 5 < 100_000 ? EnumChatFormatting.GREEN
												: profit > 100_000 || purse / 5 < 200_000 ? EnumChatFormatting.GOLD
														: EnumChatFormatting.YELLOW)
										+ "+$" + Utils.formatValue(profit));

								ChatStyle style = new ChatStyle()
										.setChatClickEvent(new ClickEvent(Action.RUN_COMMAND, commands.get(count)) {
											@Override
											public Action getAction() {
												// custom behavior
												return Action.RUN_COMMAND;
											}
										});
								result.setChatStyle(style);
								sender.addChatMessage(result);
								count++;
							}
						}
					}
					namedDataset.clear();
				}

			}, 100, 100, TimeUnit.MILLISECONDS);
		}
	}
}
