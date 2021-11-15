package me.mindlessly.notenoughcoins.commands.subcommands;

import gg.essential.universal.USound;
import me.mindlessly.notenoughcoins.utils.ApiHandler;
import me.mindlessly.notenoughcoins.utils.Config;
import me.mindlessly.notenoughcoins.utils.Reference;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Toggle implements Subcommand {
    // Take initial set of lbins, take second set, and use compared set to identify
    // the biggest
    // gainers/losers
    // namedSet is used to replace internal ids with actual item names
    public static LinkedHashMap<String, Double> initialDataset = new LinkedHashMap<>();
    public static LinkedHashMap<String, Double> secondDataset = new LinkedHashMap<>();
    public static LinkedHashMap<String, Double> namedDataset = new LinkedHashMap<>();
    public static LinkedHashMap<String, Double> avgDataset = new LinkedHashMap<>();
    public static LinkedHashMap<String, Integer> demandDataset = new LinkedHashMap<>();
    public static LinkedHashMap<Integer, Long> updatedDataset = new LinkedHashMap<>();
    public static ArrayList<String> ignoredUUIDs = new ArrayList<>();
    public static double purse;
    public static ArrayList<String> commands = new ArrayList<>();
    public static ArrayList<String> rawNames = new ArrayList<>();
    public static ArrayList<Double> percentageProfit = new ArrayList<>();
    public static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(Config.threads);
    private static int auctionPages = 0;

    public Toggle() {
    }

    public static void updateConfig(boolean showMessage) {
        scheduledExecutorService.shutdownNow();
        scheduledExecutorService = Executors.newScheduledThreadPool(Config.threads);
        scheduledExecutorService.schedule(() -> flip(showMessage), 0,
                TimeUnit.SECONDS);
    }

    public static void flip(boolean showMessage) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (Config.enabled) {
            if (showMessage)
                Utils.sendMessageWithPrefix("&aFlipper alerts enabled.", player);
            try {
                ApiHandler.getBins(initialDataset);
                ApiHandler.getAuctionAverages(avgDataset, demandDataset);
                ApiHandler.itemIdsToNames(initialDataset);
            } catch (Exception e) {
                Reference.logger.error(e.getMessage(), e);
            }
            auctionPages = ApiHandler.getNumberOfPages() - 1;
            try {
                ApiHandler.updatePurseCoins();
            } catch (Exception e) {
                player.addChatMessage(new ChatComponentText("Could not load purse."));
            }
            for (int i = 0; i < Config.threads; i++) {
                final int start = i;
                Thread thread = new Thread(() -> flipper(start, Config.threads));
                thread.start();
            }

            scheduledExecutorService.scheduleAtFixedRate(() -> {
                auctionPages = ApiHandler.getNumberOfPages() - 1;
                try {
                    ApiHandler.getBins(initialDataset);
                    ApiHandler.getAuctionAverages(avgDataset, demandDataset);
                    ApiHandler.itemIdsToNames(initialDataset);
                } catch (Exception e) {
                    player.addChatMessage(new ChatComponentText("Could not load BINs."));
                }
                try {
                    ApiHandler.updatePurseCoins();
                } catch (Exception e) {
                    player.addChatMessage(new ChatComponentText("Could not load purse."));
                }
            }, 60000, 60000, TimeUnit.MILLISECONDS);

        } else {
            if (showMessage)
                Utils.sendMessageWithPrefix("&cFlipper alerts disabled.", player);
            scheduledExecutorService.shutdownNow();
            scheduledExecutorService = Executors.newScheduledThreadPool(Config.threads);
        }
    }

    public static void flipper(int start, int increment) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        for (int iterate = start; iterate < auctionPages; iterate += increment) {
            final int page = iterate;
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                boolean print = ApiHandler.getFlips(secondDataset, page, ignoredUUIDs);
                if (print) {
                    if (namedDataset.size() > 0) {
                        purse = Math.round(purse);
                        int count = 0;
                        int demand = 0;
                        boolean noSales = false;
                        if (demandDataset.containsKey(rawNames.get(count))) {
                            demand = demandDataset.get(rawNames.get(count));
                        } else {
                            noSales = true;
                        }

                        for (Map.Entry<String, Double> entry : namedDataset.entrySet()) {
                            long profit = Math.abs(entry.getValue().longValue());
                            IChatComponent result = new ChatComponentText(EnumChatFormatting.AQUA + "[NEC] "
                                    + EnumChatFormatting.YELLOW + entry.getKey() + " "
                                    + (profit > 200_000 || purse / 5 < 100_000 ? EnumChatFormatting.GREEN
                                    : profit > 100_000 || purse / 5 < 200_000 ? EnumChatFormatting.GOLD
                                    : EnumChatFormatting.YELLOW)
                                    + "+$" + Utils.formatValue(profit) + " " + EnumChatFormatting.GOLD + "PP:" + " "
                                    + EnumChatFormatting.GREEN + percentageProfit.get(count).intValue() + "%" + " "
                                    + EnumChatFormatting.GOLD
                                    + (!noSales ? "Sales:" + " " + EnumChatFormatting.GREEN + demand + "/day"
                                    : ""));

                            ChatStyle style = new ChatStyle().setChatClickEvent(
                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, commands.get(count)));
                            result.setChatStyle(style);
                            player.addChatMessage(result);
                            if (Config.alertSounds) {
                                USound.INSTANCE.playPlingSound();
                            }
                            count++;
                            noSales = false;
                        }
                    }
                }
                namedDataset.clear();
                commands.clear();
                rawNames.clear();
                percentageProfit.clear();
            }, 100, 100, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public String getCommandName() {
        return "toggle";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "";
    }

    @Override
    public boolean processCommand(ICommandSender sender, String[] args) {
        Config.enabled = !Config.enabled;
        updateConfig(true);
        return true;
    }
}
