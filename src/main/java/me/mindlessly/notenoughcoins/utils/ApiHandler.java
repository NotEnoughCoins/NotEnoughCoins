package me.mindlessly.notenoughcoins.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.mindlessly.notenoughcoins.commands.subcommands.Toggle;
import net.minecraftforge.common.config.Configuration;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import static me.mindlessly.notenoughcoins.utils.Utils.getJson;

public class ApiHandler {
    // Will make configurable
    private static final ArrayList<String> filter = new ArrayList<>(
            Arrays.asList("TRAVEL_SCROLL", "COSMETIC", "DUNGEON_PASS", "ARROW_POISON", "PET_ITEM"));

    private static final ArrayList<String> nameFilter = new ArrayList<>(
            Arrays.asList("STARRED", "SALMON", "PERFECT", "BEASTMASTER", "MASTER_SKULL", "BLAZE", "TITANIUM"));

    public static void getBins(HashMap<String, Double> dataset) {
        boolean skip;
        Toggle.initialDataset.clear();
        try {
            JsonObject binJson = getJson("https://moulberry.codes/lowestbin.json").getAsJsonObject();
            for (Map.Entry<String, JsonElement> auction : binJson.entrySet()) {
                skip = false;
                for (String name : nameFilter) {
                    if (auction.getKey().contains(name)) {
                        skip = true;
                        break;
                    }
                }
                if (!skip) {
                    dataset.put(auction.getKey(), auction.getValue().getAsDouble());
                }
            }
        } catch (Exception e) {
            Reference.logger.error(e.getMessage(), e);
        }
        Toggle.initialDataset.putAll(dataset);

    }

    public static void getAuctionAverages(LinkedHashMap<String, Double> dataset) {
        Toggle.secondDataset.clear();
        try {
            JsonObject items = Objects.requireNonNull(getJson("https://moulberry.codes/auction_averages/3day.json"))
                    .getAsJsonObject();

            for (Entry<String, JsonElement> jsonElement : items.entrySet()) {
                if (jsonElement.getValue().getAsJsonObject().has("clean_price")) {
                    dataset.put(jsonElement.getKey(),
                            (jsonElement.getValue().getAsJsonObject().get("clean_price").getAsDouble()));
                }

                if (jsonElement.getValue().getAsJsonObject().has("price")
                        && !jsonElement.getValue().getAsJsonObject().has("clean_price")) {
                    dataset.put(jsonElement.getKey(),
                            (jsonElement.getValue().getAsJsonObject().get("price").getAsDouble()));
                }

            }
        } catch (Exception e) {
            Reference.logger.error(e.getMessage(), e);
        }

        Toggle.secondDataset.putAll(dataset);

        for (Map.Entry<String, Double> entry : Toggle.secondDataset.entrySet()) {
            if (Toggle.initialDataset.containsKey(entry.getKey())) {
                if (Toggle.initialDataset.get(entry.getKey()) > entry.getValue()) {
                    Toggle.initialDataset.remove(entry.getKey());
                }
            }
        }
    }

    public static void itemIdsToNames(LinkedHashMap<String, Double> initialDataset) {
        Toggle.namedDataset.clear();
        LinkedHashMap<String, Double> datasettemp = new LinkedHashMap<>(initialDataset);
        initialDataset.clear();

        try {
            JsonArray itemArray = Objects.requireNonNull(getJson("https://api.hypixel.net/resources/skyblock/items"))
                    .getAsJsonObject().get("items").getAsJsonArray();

            for (Map.Entry<String, Double> auction : datasettemp.entrySet()) {
                String key = auction.getKey();
                Double value = auction.getValue();

                for (JsonElement item : itemArray) {
                    if (item.getAsJsonObject().get("id").getAsString().equals(key)) {
                        if (item.getAsJsonObject().has("category")) {
                            if (!(filter.contains(item.getAsJsonObject().get("category").getAsString()))) {
                                String name = item.getAsJsonObject().get("name").getAsString();
                                initialDataset.put(name, value);
                            }
                        }
                    }
                }
            }
            Toggle.secondDataset.putAll(initialDataset);
            LinkedHashMap<String, Double> unsortedMap = Toggle.secondDataset;
            // LinkedHashMap preserve the ordering of elements in which they are inserted
            LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();

            // Use Comparator.reverseOrder() for reverse ordering
            unsortedMap.entrySet().stream().sorted(HashMap.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(x -> sortedMap.put(x.getKey(), (double) Math.round(x.getValue())));

            Toggle.secondDataset = sortedMap;
        } catch (Exception e) {
            Reference.logger.error(e.getMessage(), e);
        }
    }

    private static String getUuid(String name) {
        try {
            return Objects.requireNonNull(getJson("https://api.mojang.com/users/profiles/minecraft/" + name))
                    .getAsJsonObject().get("id").getAsString();
        } catch (Exception e) {
            Reference.logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static void updatePurseCoins(String key, String name) {
        String uuid = getUuid(name);

        try {
            JsonArray profilesArray = Objects
                    .requireNonNull(getJson("https://api.hypixel.net/skyblock/profiles?key=" + key + "&uuid=" + uuid))
                    .getAsJsonObject().get("profiles").getAsJsonArray();

            // Get last played profile
            int profileIndex = 0;
            Instant lastProfileSave = Instant.EPOCH;
            for (int i = 0; i < profilesArray.size(); i++) {
                Instant lastSaveLoop;
                try {
                    lastSaveLoop = Instant.ofEpochMilli(profilesArray.get(i).getAsJsonObject().get("members")
                            .getAsJsonObject().get(uuid).getAsJsonObject().get("last_save").getAsLong());
                } catch (Exception e) {
                    continue;
                }

                if (lastSaveLoop.isAfter(lastProfileSave)) {
                    profileIndex = i;
                    lastProfileSave = lastSaveLoop;
                }
            }

            Toggle.purse = profilesArray.get(profileIndex).getAsJsonObject().get("members").getAsJsonObject().get(uuid)
                    .getAsJsonObject().get("coin_purse").getAsDouble();
        } catch (Exception e) {
            Reference.logger.error(e.getMessage(), e);
        }
    }

    public static boolean getFlips(LinkedHashMap<String, Double> dataset, int i, ArrayList<String> commands) {
        Toggle.commands.clear();

        try {
            JsonObject auctionPage = Objects
                    .requireNonNull(getJson("https://api.hypixel.net/skyblock/auctions?page=" + i)).getAsJsonObject();

            Long lastUpdated = auctionPage.get("lastUpdated").getAsLong();

            if (Toggle.updatedDataset.containsKey(i)) {
                if (lastUpdated.equals(Toggle.updatedDataset.get(i))) {
                    return false;
                } else {
                    Toggle.updatedDataset.remove(i);
                    Toggle.updatedDataset.put(i, lastUpdated);
                }
            }

            JsonArray auctionsArray = auctionPage.get("auctions").getAsJsonArray();

            for (JsonElement item : auctionsArray) {
                for (HashMap.Entry<String, Double> entry : dataset.entrySet()) {
                    if (item.getAsJsonObject().get("item_name").getAsString().contains(entry.getKey())) {
                        if (item.getAsJsonObject().has("bin")) {
                            if (item.getAsJsonObject().get("bin").getAsString().contains("true")) {
                                if (item.getAsJsonObject().has("claimed")) {
                                    if (item.getAsJsonObject().get("claimed").getAsString().contains("false")) {
                                        if (item.getAsJsonObject().has("starting_bid")) {
                                            if (item.getAsJsonObject().get("starting_bid").getAsDouble() < entry
                                                    .getValue()) {
                                                if (item.getAsJsonObject().get("starting_bid")
                                                        .getAsDouble() <= Toggle.purse) {
                                                    String rawName = item.getAsJsonObject().get("item_name")
                                                            .getAsString();
                                                    String name = new String(rawName.getBytes(),
                                                            StandardCharsets.UTF_8);

                                                    long minflip = 50000;
                                                    if (ConfigHandler.hasKey(Configuration.CATEGORY_GENERAL,
                                                            "MinProfit")) {
                                                        minflip = Long.parseLong(ConfigHandler.getString(
                                                                Configuration.CATEGORY_GENERAL, "MinProfit"));
                                                    }
                                                    if (entry.getValue() - item.getAsJsonObject().get("starting_bid")
                                                            .getAsLong() > minflip) {
                                                        Toggle.namedDataset.put(name, entry.getValue() - item
                                                                .getAsJsonObject().get("starting_bid").getAsLong());

                                                        if (item.getAsJsonObject().has("uuid")) {
                                                            commands.add("/viewauction "
                                                                    + item.getAsJsonObject().get("uuid").getAsString());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Reference.logger.error(e.getMessage(), e);
        }

        Toggle.commands.addAll(commands);
        return true;
    }

    public static int getNumberOfPages() {
        int pages = 0;
        try {
            pages = Objects.requireNonNull(getJson("https://api.hypixel.net/skyblock/auctions?page=0"))
                    .getAsJsonObject().get("totalPages").getAsInt();
        } catch (Exception e) {
            Reference.logger.error(e.getMessage(), e);
        }
        return pages;
    }
}
