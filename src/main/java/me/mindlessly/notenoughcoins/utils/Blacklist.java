package me.mindlessly.notenoughcoins.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;

public class Blacklist {

	private static File blacklistFile;
	public static JsonObject json;
	public final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static void init() throws IOException {
		blacklistFile = new File(
				Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + "//NotEnoughCoins//blacklist.json");
		if (blacklistFile.exists() && !blacklistFile.isDirectory()) {
			InputStream is = new FileInputStream(blacklistFile);
			String jsonTxt = IOUtils.toString(is, "UTF-8");
			json = new JsonParser().parse(jsonTxt).getAsJsonObject();
		} else {
			blacklistFile.getParentFile().mkdirs();
			blacklistFile.createNewFile();
			try (Writer writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(blacklistFile), "utf-8"))) {
				json = new JsonObject();
				JsonArray items = new JsonArray();
				json.add("items", items);
				writer.write(json.toString());
				writer.close();
			}
		}
	}

	public static void add(String item) {
		for (JsonElement jsonElement : json.get("items").getAsJsonArray()) {
			if (jsonElement.getAsString().equals(item)) {
				return;
			}
		}
		json.getAsJsonArray("items").add(gson.toJsonTree(item));
		save();
	}

	public static void remove(String item) {
		JsonArray items = json.getAsJsonArray("items");
		Iterator<JsonElement> iter = items.iterator();
		while (iter.hasNext()) {
			if (iter.next().getAsString().equals(item)) {
				iter.remove();
				break;
			}
		}
		save();
	}

	public static void addPet(JsonArray array) {
		for (JsonElement jsonElement : json.get("items").getAsJsonArray()) {
			for (JsonElement e : array) {
				if (jsonElement.getAsString().equals(e.getAsString())) {
					return;
				}
			}
		}
		for (JsonElement e : array) {
			json.getAsJsonArray("items").add(gson.toJsonTree(e));
		}
		save();

	}

	public static void removePet(JsonArray array) {
		JsonArray items = json.getAsJsonArray("items");
		for (JsonElement item : array) {
			Iterator<JsonElement> iter = items.iterator();
			while (iter.hasNext()) {
				if (iter.next().getAsString().equals(item.getAsString())) {
					iter.remove();
					break;
				}
			}
		}
		save();
	}

	public static void save() {
		try (FileWriter writer = new FileWriter(blacklistFile)) {
			gson.toJson(json, writer);
		} catch (IOException e) {
			System.err.println("Error saving to file: " + e.getMessage());
		}
	}
}