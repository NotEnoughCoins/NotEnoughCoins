package me.mindlessly.notenoughcoins.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.mindlessly.notenoughcoins.Reference;
import me.mindlessly.notenoughcoins.commands.subcommands.Toggle;
import me.mindlessly.notenoughcoins.config.Config;
import net.minecraft.client.Minecraft;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;

import static me.mindlessly.notenoughcoins.utils.Utils.getJson;

public class ApiHandler {
	public static String UUID;
	// Will make configurable
	private static final ArrayList<String> filter = new ArrayList<>(
			Arrays.asList("TRAVEL_SCROLL", "COSMETIC", "DUNGEON_PASS", "ARROW_POISON", "PET_ITEM"));
	private static final ArrayList<String> nameFilter = new ArrayList<>(Arrays.asList("STARRED", "SALMON", "PERFECT",
			"BEASTMASTER", "MASTER_SKULL", "BLAZE", "TITANIUM", "SUPER_HEAVY", "WAND_OF", "FARM_ARMOR", "PURE_MITHRIL",
			"STEEL_CHESTPLATE", "MIDAS", "TRIBAL_SPEAR"));

	public static void getBins(HashMap<String, Double> dataset) {
		boolean skip;
		Toggle.initialDataset.clear();
		try {
			JsonObject binJson = Objects.requireNonNull(getJson("https://moulberry.codes/lowestbin.json"))
					.getAsJsonObject();
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

	public static void getAuctionAverages(LinkedHashMap<String, Double> dataset,
			LinkedHashMap<String, Integer> demand) {
		Toggle.avgDataset.clear();
		Toggle.demandDataset.clear();

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

				if (jsonElement.getValue().getAsJsonObject().has("sales")) {
					int sales = jsonElement.getValue().getAsJsonObject().get("sales").getAsInt();
					if(sales >= Config.demand) {
						demand.put(jsonElement.getKey(), sales);
					}
				}

			}
		} catch (Exception e) {
			Reference.logger.error(e.getMessage(), e);
		}

		Toggle.avgDataset.putAll(dataset);
		Toggle.demandDataset.putAll(demand);

		for (Map.Entry<String, Double> entry : Toggle.avgDataset.entrySet()) {
			if (Toggle.initialDataset.containsKey(entry.getKey())) {
				if (Toggle.initialDataset.get(entry.getKey()) * 0.6 > entry.getValue()) {
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
								if (Toggle.demandDataset.containsKey(key)) {
									int demand = Toggle.demandDataset.get(key);
									Toggle.demandDataset.put(name, demand);
									Toggle.demandDataset.remove(key);
								}
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
					.forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

			Toggle.secondDataset = sortedMap;
		} catch (Exception e) {
			Reference.logger.error(e.getMessage(), e);
		}
	}

	public static String getUuid(String name) {
		try {
			return Objects.requireNonNull(getJson("https://api.mojang.com/users/profiles/minecraft/" + name))
					.getAsJsonObject().get("id").getAsString();
		} catch (Exception e) {
			Reference.logger.error(e.getMessage(), e);
			return "";
		}
	}

	public static void updatePurseCoins() {
		try {
			JsonArray profilesArray = Objects
					.requireNonNull(
							getJson("https://api.hypixel.net/skyblock/profiles?key=" + Config.apiKey + "&uuid=" + UUID))
					.getAsJsonObject().get("profiles").getAsJsonArray();

			// Get last played profile
			int profileIndex = 0;
			Instant lastProfileSave = Instant.EPOCH;
			for (int i = 0; i < profilesArray.size(); i++) {
				Instant lastSaveLoop;
				try {
					lastSaveLoop = Instant.ofEpochMilli(profilesArray.get(i).getAsJsonObject().get("members")
							.getAsJsonObject().get(UUID).getAsJsonObject().get("last_save").getAsLong());
				} catch (Exception e) {
					continue;
				}

				if (lastSaveLoop.isAfter(lastProfileSave)) {
					profileIndex = i;
					lastProfileSave = lastSaveLoop;
				}
			}

			Toggle.purse = profilesArray.get(profileIndex).getAsJsonObject().get("members").getAsJsonObject().get(UUID)
					.getAsJsonObject().get("coin_purse").getAsDouble();
		} catch (Exception e) {
			Reference.logger.error(e.getMessage(), e);
		}
	}

	public static boolean getFlips(LinkedHashMap<String, Double> dataset, int i, ArrayList<String> ignored) {
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
					String uuid = item.getAsJsonObject().get("uuid").getAsString();
					String auctioneer = item.getAsJsonObject().get("auctioneer").getAsString();
					String rawName = item.getAsJsonObject().get("item_name").getAsString();

					if (!ApiHandler.UUID.equals(auctioneer)) {
						if (!ignored.contains(uuid)) {
							if (rawName.contains(entry.getKey())) {
								if (item.getAsJsonObject().has("bin")
										&& item.getAsJsonObject().get("bin").getAsBoolean()) {
									if (item.getAsJsonObject().has("claimed")
											&& (!item.getAsJsonObject().get("claimed").getAsBoolean())) {
										double startingBid = item.getAsJsonObject().get("starting_bid").getAsDouble();
										if (startingBid < entry.getValue()) {
											if (startingBid <= Toggle.purse) {
												String name = new String(rawName.getBytes(), StandardCharsets.UTF_8);
												double profit;
												double percentProfit;
												if (entry.getValue() - startingBid > Config.minProfit) {
													if (startingBid >= 1000000) {
														profit = (entry.getValue() - startingBid)
																- (entry.getValue() * 0.02);
														percentProfit = (((entry.getValue() - startingBid)
																- (entry.getValue() * 0.02)) / startingBid) * 100;
													} else {
														profit = (entry.getValue() - startingBid)
																- (entry.getValue() * 0.01);
														percentProfit = (((entry.getValue() - startingBid)
																- (entry.getValue() * 0.01)) / startingBid) * 100;
													}
													if (profit > Config.minProfit
															&& percentProfit > Config.minProfitPercentage) {
														Toggle.namedDataset.put(name, profit);
														Toggle.commands.add("/viewauction " + uuid);
														Toggle.rawNames.add(entry.getKey());
														Toggle.percentageProfit.add(percentProfit);
														ignored.add(uuid);
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
