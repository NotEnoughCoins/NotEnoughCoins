package me.mindlessly.notenoughcoins.websocket;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.essential.universal.USound;
import me.mindlessly.notenoughcoins.Authenticator;
import me.mindlessly.notenoughcoins.Config;
import me.mindlessly.notenoughcoins.Main;
import me.mindlessly.notenoughcoins.Reference;
import me.mindlessly.notenoughcoins.objects.AverageItem;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ResourceLocation;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Client extends WebSocketClient {
    private Date lastPing;
    public long latency = -1; // in ms

    public Client(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                lastPing = new Date();
                send("{\"type\":\"ping\"}");
            }
        }, 1000, 30000);
    }

    public static void connectWithToken() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", Main.authenticator.getToken());
        try {
            new Client(new URI("wss://nec.robothanzo.dev/ws"), headers).connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Reference.logger.info("Websocket connection established");
    }

    @Override
    public void onMessage(String message) {
        Date start = new Date();
        JsonObject json = new JsonParser().parse(message).getAsJsonObject();
        if (json.has("type")) {
            switch (json.get("type").getAsString()) {
                case "profit":
                    if (Config.enabled && ((!Config.onlySkyblock) || Utils.isOnSkyblock())) {
                        for (JsonElement element : json.getAsJsonArray("result")) {
                            JsonObject item = element.getAsJsonObject();
                            String auctionID = item.get("auction_id").getAsString();
                            String itemID = item.get("id").getAsString();
                            if (!Main.processedItem.containsKey(auctionID)) { // havent been processed
                                Main.processedItem.put(auctionID, new Date(item.get("end").getAsLong()));
                                if (!Config.categoryFilter.contains(item.get("category").getAsString().toUpperCase(Locale.ROOT)) && !Arrays.asList(Config.blacklistedIDs.split("\n")).contains(item.get("id").getAsString())) { // blacklist checks
                                    int price = item.get("price").getAsInt();
                                    int profit = Utils.getTaxedProfit(price, item.get("profit").getAsInt());
                                    int demand;
                                    try {
                                        demand = Main.averageItemMap.get(itemID).demand;
                                    } catch (NullPointerException e) {
                                        Main.processedItem.remove(auctionID);
                                        continue;
                                    }
                                    double profitPercentage = ((double) profit / (double) price);
                                    if (price <= Main.balance && profit >= Config.minProfit && profitPercentage >= Config.minProfitPercentage && demand >= Config.minDemand) { // min profit etc checks
                                        if ((!Config.manipulationCheck) || (!((price + item.get("profit").getAsInt()) * 0.6 > Main.averageItemMap.get(itemID).ahAvgPrice))) { // Manipulation checks
                                            if (!Authenticator.myUUID.toLowerCase(Locale.ROOT).replaceAll("-", "").equals(item.get("auctioneer").getAsString())) { //not self
                                                Utils.sendMessageWithPrefix(Utils.getColorCodeFromRarity(item.get("rarity").getAsString()) + item.get("item_name").getAsString() + "&e " + // item name
                                                        Utils.getProfitText(profit) + " " + // profit
                                                        "&eP: &a" + Utils.formatValue(price) + " " + // price
                                                        "&ePP: &a" + (int) Math.floor(profitPercentage * 100) + "% " + // profit %
                                                        "&eSPD: &a" + demand + " " + // demand
                                                        (Config.debug ? "\n&eCL: &a" + item.get("cache_latency").getAsInt() + "ms" : "") + " " + // debug: cache latency
                                                        (Config.debug ? "&eAL: &a" + item.get("api_latency").getAsInt() + "ms" : "") + " " + // debug: api latency
                                                        (Config.debug ? "&eWL: &a" + latency + "ms" : "") + " " + // debug: websocket latency
                                                        (Config.debug ? "&ePL: &a" + (new Date().getTime() - start.getTime()) + "ms" : "") + " " + // debug: processing latency
                                                        (Config.debug ? "&eAA: &a" + Utils.formatValue(Main.averageItemMap.get(itemID).ahAvgPrice) : "") + " " + // debug: auction average
                                                        (Config.debug ? "&eLBIN: &a" + Utils.formatValue(Main.lbinItem.get(itemID)) : "") + " " + // debug: lowest buy-it-now
                                                        ((profit >= 100000) ? "\n" : ""), // emphasize flips with large profit by sending a new line
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewauction " + auctionID));
                                                if (Config.alertSounds && !Main.justPlayedASound) {
                                                    Main.justPlayedASound = true;
                                                    USound.INSTANCE.playSoundStatic(new ResourceLocation("note.pling"), 2F, 1.0F);
                                                }
                                            }
                                        } else {
                                            Reference.logger.info("Failed manipulation check for " + item.get("item_name").getAsString() + " price " + price + " profit " + profit + " avg " + Main.averageItemMap.get(item.get("id").getAsString()).ahAvgPrice);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Main.justPlayedASound = false;
                    return;
                case "lowest_bin":
                    for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("result").entrySet()) {
                        Main.lbinItem.put(entry.getKey(), entry.getValue().getAsInt());
                    }
                    return;
                case "average":
                    for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("result").entrySet()) {
                        String item = entry.getKey();
                        JsonObject itemDetails = entry.getValue().getAsJsonObject();
                        int sampledDays = itemDetails.getAsJsonPrimitive("sampled_days").getAsInt();
                        int ahSales = Math.floorDiv(itemDetails.getAsJsonObject("auction").getAsJsonPrimitive("sales").getAsInt(), sampledDays);
                        int ahAvgPrice = (int) Math.floor(itemDetails.getAsJsonObject("auction").getAsJsonPrimitive("average_price").getAsDouble());
                        int binSales = Math.floorDiv(itemDetails.getAsJsonObject("bin").getAsJsonPrimitive("sales").getAsInt(), sampledDays);
                        // int binAvgPrice = (int)Math.floor(itemDetails.getAsJsonObject("bin").getAsJsonPrimitive("average_price").getAsDouble());
                        Main.averageItemMap.put(item, new AverageItem(item, ahSales + binSales, ahAvgPrice));
                    }
                    return;
                case "pong":
                    if (lastPing!=null) {
                        latency = (new Date().getTime() - lastPing.getTime()) / 2;
                    }
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Reference.logger.warn("Websocket connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: " + reason);
        if (reason.contains("418")) {
            Utils.sendMessageWithPrefix("&cFailed to fetch from NEC backend, this might be caused by:\n" +
                "&cYou have been blacklisted from the mod for using macro scripts\n" +
                "&cPlease join our discord server for more information (in /nec > links)");
        }
        if (reason.contains("401")) {
            Reference.logger.warn("Token expired, fetching new token");
            try {
                Main.authenticator.authenticate(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (code != 1001) { // abnormal close
            connectWithToken();
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Reference.logger.warn("Restarting connection due to abnormal close");
        }
    }

    @Override
    public void onError(Exception ex) {
        Reference.logger.error("Websocket error", ex);
    }
}
