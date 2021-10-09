package me.mindlessly.notenoughcoins.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.mindlessly.notenoughcoins.commands.Flip;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;

import static me.mindlessly.notenoughcoins.utils.Utils.getJson;

public class ApiHandler {

  // Will make configurable
  private static final ArrayList<String> filter =
      new ArrayList<>(
          Arrays.asList("TRAVEL_SCROLL", "COSMETIC", "DUNGEON_PASS", "ARROW_POISON", "PET_ITEM"));

  public static void getBins(HashMap<String, Double> dataset) {
    try {
      JsonObject binJson =
          Objects.requireNonNull(getJson("https://moulberry.codes/lowestbin.json"))
              .getAsJsonObject();
      for (Map.Entry<String, JsonElement> auction : binJson.entrySet()) {
        dataset.put(auction.getKey(), auction.getValue().getAsDouble());
      }
    } catch (Exception e) {
      Reference.logger.error(e.getMessage(), e);
    }
  }

  private static void getAuctionAverages(LinkedHashMap<String, Double> initialDataset) {
    LinkedHashMap<String, Double> datasettemp = new LinkedHashMap<>(initialDataset);
    initialDataset.clear();

    try {
      JsonObject items =
          Objects.requireNonNull(getJson("https://moulberry.codes/auction_averages/1minute.json"))
              .getAsJsonObject();

      for (Entry<String, JsonElement> jsonElement : items.entrySet()) {
        for (Map.Entry<String, Double> auction : datasettemp.entrySet()) {
          String key = auction.getKey();
          Double value = auction.getValue();
          if (jsonElement.getKey().equals(key)) {
            if (jsonElement.getValue().getAsJsonObject().has("clean_price")) {
              Double price =
                  jsonElement.getValue().getAsJsonObject().get("clean_price").getAsDouble();
              if (value < price) {
                initialDataset.put(key, value);
              }

            } else if (jsonElement.getValue().getAsJsonObject().has("price")) {
              Double price = jsonElement.getValue().getAsJsonObject().get("price").getAsDouble();
              if (value < price) {
                initialDataset.put(key, value);
              }
            }
          }

          Flip.initialDataset.putAll(initialDataset);
        }
      }
    } catch (Exception e) {
      Reference.logger.error(e.getMessage(), e);
    }
  }

  public static void itemIdsToNames(LinkedHashMap<String, Double> initialDataset) {
    getAuctionAverages(initialDataset);
    LinkedHashMap<String, Double> datasettemp = new LinkedHashMap<>(initialDataset);
    initialDataset.clear();

    try {
      JsonArray itemArray =
          Objects.requireNonNull(getJson("https://api.hypixel.net/resources/skyblock/items"))
              .getAsJsonObject()
              .get("items")
              .getAsJsonArray();

      for (Map.Entry<String, Double> auction : datasettemp.entrySet()) {
        String key = auction.getKey();
        Double value = auction.getValue();

        for (JsonElement item : itemArray) {
          if (item.getAsJsonObject().get("id").getAsString().contains(key)) {
            if (item.getAsJsonObject().has("category")) {
              if (!(filter.contains(item.getAsJsonObject().get("category").getAsString()))) {
                String name = item.getAsJsonObject().get("name").getAsString();
                initialDataset.put(name, value);
              }
            }
          }
        }
        Flip.initialDataset.putAll(initialDataset);
      }
      LinkedHashMap<String, Double> unsortedMap = Flip.namedDataset;
      // LinkedHashMap preserve the ordering of elements in which they are inserted
      LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();

      // Use Comparator.reverseOrder() for reverse ordering
      unsortedMap.entrySet().stream()
          .sorted(HashMap.Entry.comparingByValue(Comparator.reverseOrder()))
          .forEachOrdered(x -> sortedMap.put(x.getKey(), (double) Math.round(x.getValue())));

      Flip.namedDataset = sortedMap;
    } catch (Exception e) {
      Reference.logger.error(e.getMessage(), e);
    }
  }

  private static String getUuid(String name) {
    try {
      return Objects.requireNonNull(
              getJson("https://api.mojang.com/users/profiles/minecraft/" + name))
          .getAsJsonObject()
          .get("id")
          .getAsString();
    } catch (Exception e) {
      Reference.logger.error(e.getMessage(), e);
      return null;
    }
  }

  public static void updatePurseCoins(String key, String name) {
    String uuid = getUuid(name);

    try {
      JsonArray profilesArray =
          Objects.requireNonNull(
                  getJson("https://api.hypixel.net/skyblock/profiles?key=" + key + "&uuid=" + uuid))
              .getAsJsonObject()
              .get("profiles")
              .getAsJsonArray();

      // Get last played profile
      int profileIndex = 0;
      Instant lastProfileSave = Instant.EPOCH;
      for (int i = 0; i < profilesArray.size(); i++) {
        Instant lastSaveLoop;
        try {
          lastSaveLoop =
              Instant.ofEpochMilli(
                  profilesArray
                      .get(i)
                      .getAsJsonObject()
                      .get("members")
                      .getAsJsonObject()
                      .get(uuid)
                      .getAsJsonObject()
                      .get("last_save")
                      .getAsLong());
        } catch (Exception e) {
          continue;
        }

        if (lastSaveLoop.isAfter(lastProfileSave)) {
          profileIndex = i;
          lastProfileSave = lastSaveLoop;
        }
      }

      Flip.purse =
          profilesArray
              .get(profileIndex)
              .getAsJsonObject()
              .get("members")
              .getAsJsonObject()
              .get(uuid)
              .getAsJsonObject()
              .get("coin_purse")
              .getAsDouble();
    } catch (Exception e) {
      Reference.logger.error(e.getMessage(), e);
    }
  }

  public static void getFlips(
      LinkedHashMap<String, Double> dataset, int i, ArrayList<String> commands) {
    Flip.commands.clear();

    try {
      JsonArray auctionsArray =
          Objects.requireNonNull(getJson("https://api.hypixel.net/skyblock/auctions?page=" + i))
              .getAsJsonObject()
              .get("auctions")
              .getAsJsonArray();

      for (JsonElement item : auctionsArray) {
        for (HashMap.Entry<String, Double> entry : Flip.initialDataset.entrySet()) {
          if (item.getAsJsonObject().get("item_name").getAsString().contains(entry.getKey())) {
            if (item.getAsJsonObject().has("bin")) {
              if (item.getAsJsonObject().get("bin").getAsString().contains("true")) {
                if (item.getAsJsonObject().has("starting_bid")) {
                  if (item.getAsJsonObject().get("starting_bid").getAsDouble() < entry.getValue()) {
                    if (item.getAsJsonObject().get("starting_bid").getAsDouble() > Flip.purse) {
                      String rawName = item.getAsJsonObject().get("item_name").getAsString();
                      String name = new String(rawName.getBytes(), StandardCharsets.UTF_8);
                      dataset.put(
                          name,
                          entry.getValue()
                              - item.getAsJsonObject().get("starting_bid").getAsLong());

                      if (item.getAsJsonObject().has("uuid")) {
                        commands.add(
                            "/viewauction " + item.getAsJsonObject().get("uuid").getAsString());
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

    Flip.namedDataset.putAll(dataset);
    Flip.commands.addAll(commands);
  }

  public static int getNumberOfPages() {
    int pages = 0;
    try {
      pages =
          Objects.requireNonNull(getJson("https://api.hypixel.net/skyblock/auctions?page=0"))
              .getAsJsonObject()
              .get("totalPages")
              .getAsInt();
    } catch (Exception e) {
      Reference.logger.error(e.getMessage(), e);
    }
    return pages;
  }
}
