package me.mindlessly.notenoughcoins.utils;

import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ApiHandler {
	
	public static HashMap<String, String> items = new HashMap<>();
	public static JsonObject pets;
	

	public static void getItems() {
		JsonArray jsonArray = Utils.getJson("https://api.hypixel.net/resources/skyblock/items").getAsJsonObject()
				.get("items").getAsJsonArray();
		for (JsonElement jsonElement : jsonArray) {
			JsonObject item = jsonElement.getAsJsonObject();
			String name = item.get("name").getAsString();
			String id = item.get("id").getAsString();
			items.put(name, id);
		}
		pets = Utils.getJson("https://notenoughcoins.net/static/pets.json").getAsJsonObject();
	}

}
