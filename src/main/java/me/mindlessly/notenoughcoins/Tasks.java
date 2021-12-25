package me.mindlessly.notenoughcoins;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gg.essential.universal.USound;
import me.mindlessly.notenoughcoins.utils.ApiHandler;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.event.ClickEvent;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static me.mindlessly.notenoughcoins.utils.Utils.blacklistMessage;

public class Tasks {
    public static Thread updateBalance = new Thread(() -> {
        while (true) {
            if (Config.enabled) {
                try {
                    ApiHandler.updatePurse();
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    try {
                        Utils.sendMessageWithPrefix("&cFailed to update balance, please check if you set your API key correctly.");
                        Thread.sleep(60000); // Wait until the user sets the API key
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }, "Not Enough Coins Balance Updating Task");
    public static Thread updateAverageItem = new Thread(() -> {
        while (true) {
            if (Config.enabled || Config.bestSellingMethod) {
                try {
                    ApiHandler.updateAverage();
                    Thread.sleep(10000);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Utils.blacklistMessage();
                    try {
                        Thread.sleep(60000); // sleep 60s if the API is down or got blacklisted
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } // This shouldn't happen though

            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }, "Not Enough Coins Average Item Updating Task");
    public static Thread updateLBINItem = new Thread(() -> {
        while (true) {
            if (Config.enabled || Config.bestSellingMethod) {
                try {
                    ApiHandler.updateLBIN();
                    Thread.sleep(2500);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Utils.blacklistMessage();
                    try {
                        Thread.sleep(60000); // sleep 60s if the API is down or got blacklisted
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } // This shouldn't happen though

            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }, "Not Enough Coins Lowest Buy-It-Now Updating Task");
    public static Thread updateBazaarItem = new Thread(() -> {
        while (true) {
            if (Config.enabled || Config.bestSellingMethod) {
                try {
                    ApiHandler.updateBazaar();
                    Thread.sleep(2500);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Utils.blacklistMessage();
                    try {
                        Thread.sleep(60000); // sleep 60s if the API is down or got blacklisted
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } // This shouldn't happen though

            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }, "Not Enough Coins Bazaar Updating Task");
    public static Thread flipping = new Thread(() -> {
        while (true) {
            if (Config.enabled) {
                if (Main.balance == 0D) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                Date start = new Date();
                JsonElement json;
                try {
                    json = Objects.requireNonNull(Authenticator.getAuthenticatedJson("https://nec.robothanzo.dev/profit"));
                } catch (Exception e) {
                    e.printStackTrace();
                    blacklistMessage();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    continue;
                }
                long responseLatency = new Date().getTime() - start.getTime();
                for (JsonElement element : json.getAsJsonObject().getAsJsonArray("result")) {
                    JsonObject item = element.getAsJsonObject();
                    String itemID = item.get("auction_id").getAsString();
                    /* example:
                     * {"amount":1,"auction_id":"770d177104dd4c62b3f5610bcb0269e0","auctioneer":"5c003dfe48e741e497dcedbb2fe13475",
                     * "bin":true,"category":"accessories","dungeon_level":null,"enchantments":{},"end":1638823899955,"hpb_count":0,
                     * "id":"SHARP_SHARK_TOOTH_NECKLACE","item_name":"Sharp Shark Tooth Necklace",
                     * "pet_info":null,"price":135000, "profile_id":"d2bcb7b76cd14837b19c03ea258e51fd","profit":5000,"rarity":"EPIC",
                     * "recombobulated":false,"reforge":null,"starred":false,"start":1638802299955,"generated_at":1638802300983}
                     */
                    if (!Main.processedItem.containsKey(itemID)) { // havent been processed
                        Main.processedItem.put(itemID, new Date(item.get("end").getAsLong()));
                        if (!Config.categoryFilter.contains(item.get("category").getAsString().toUpperCase(Locale.ROOT)) && !Arrays.asList(Config.blacklistedIDs.split("\n")).contains(item.get("id").getAsString())) { // blacklist checks
                            int price = item.get("price").getAsInt();
                            int profit = Utils.getTaxedProfit(price, item.get("profit").getAsInt());
                            int demand;
                            try {
                                demand = Main.averageItemMap.get(item.get("id").getAsString()).demand;
                            } catch (NullPointerException e) {
                                Main.processedItem.remove(itemID);
                                continue;
                            }
                            double profitPercentage = ((double) profit / (double) price);
                            if (price <= Main.balance && profit >= Config.minProfit && profitPercentage >= Config.minProfitPercentage && demand >= Config.minDemand) { // min profit etc checks
                                if ((!Config.manipulationCheck)||(!((price + item.get("profit").getAsInt()) * 0.6 > Main.averageItemMap.get(item.get("id").getAsString()).ahAvgPrice))) { // Manipulation checks
                                    if (!Authenticator.myUUID.toLowerCase(Locale.ROOT).replaceAll("-", "").equals(item.get("auctioneer").getAsString())) { //not self
                                        Utils.sendMessageWithPrefix("&e" + item.get("item_name").getAsString() + " " + // item name
                                                Utils.getProfitText(profit) + " " + // profit
                                                "&eP: &a" + Utils.formatValue(price) + " " + // price
                                                "&ePP: &a" + (int) Math.floor(profitPercentage * 100) + "% " + // profit %
                                                "&eSPD: &a" + demand + " " + // demand
                                                (Config.debug ? "&eRL: &a" + responseLatency + "ms" : "") + " " + // debug: response latency
                                                (Config.debug ? "&ePL: &a" + (new Date().getTime() - start.getTime() - responseLatency) + "ms" : "") + " " + // debug: processing latency
                                                ((profit >= 100000) ? "\n" : ""), // emphasize flips with large profit by sending a new line
                                            new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewauction " + itemID));
                                        if (Config.alertSounds && !Main.justPlayedASound) {
                                            Main.justPlayedASound = true;
                                            USound.INSTANCE.playPlingSound();
                                        }
                                    }
                                } else {
                                    Reference.logger.info("Failed manipulation check for " + item.get("item_name").getAsString() + " price " + price + " profit " + profit + " avg " + Main.averageItemMap.get(item.get("id").getAsString()).ahAvgPrice);
                                }
                            }
                        }
                    }
                }
                Main.justPlayedASound = false;
                Main.processedItem.entrySet().removeIf(item -> (Main.processedItem.get(item.getKey()).getTime() - new Date().getTime()) <= 0);
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }, "Not Enough Coins Flipping Task");

    public static Thread updateFilters = new Thread(() -> {
        while (true) {
            if (Config.hideSpam) {
                List<String> filters = new LinkedList<>();
                try {
                    for (Map.Entry<String, JsonElement> f : Utils.getJson("https://nec.robothanzo.dev/filter").getAsJsonObject().getAsJsonObject("result").entrySet()) {
                        for (JsonElement filter : f.getValue().getAsJsonArray()) {
                            filters.add(filter.getAsString().toLowerCase(Locale.ROOT));
                        }
                    }
                    Main.chatFilters = filters;
                    Reference.logger.info("Filter: " + Main.chatFilters);
                    Thread.sleep(60000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }, "Not Enough Coins Filters Updating Task");
}
