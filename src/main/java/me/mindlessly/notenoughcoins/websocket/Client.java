package me.mindlessly.notenoughcoins.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import me.mindlessly.notenoughcoins.configuration.ConfigHandler;
import me.mindlessly.notenoughcoins.utils.Blacklist;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class Client {

	public static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
	private static final String SERVER_HOST = "vps-9587f748.vps.ovh.ca";
	private static final int SERVER_PORT = 8087;
	private static Gson gson = new Gson();
	private static Socket socket;

	private static Minecraft mc;

	public static void start() {
		try {
			socket = new Socket(SERVER_HOST, SERVER_PORT);

			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// Listen for server messages in a separate thread
			Thread serverListenerThread = new Thread(() -> {
				try {

					byte[] buffer = new byte[1024];
					int bytesRead;
					while ((bytesRead = socket.getInputStream().read(buffer)) != -1) {
						// Sometimes multiple JSON objects will be received from the websocket at the
						// same time, we need to seperate these
						String receivedData = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);

						// Define the regular expression pattern to match JSON objects
						String regexPattern = "\\{.*?\\}";

						// Create a regular expression matcher
						Pattern pattern = Pattern.compile(regexPattern);
						Matcher matcher = pattern.matcher(receivedData);

						// Process each matched JSON object
						while (matcher.find()) {
							String jsonObject = matcher.group();

							JsonObject config = ConfigHandler.getConfig();
							try {
								// Parse the JSON object and send the flip
								JsonArray blacklist = Blacklist.json.get("items").getAsJsonArray();
								JsonObject flip = new Gson().fromJson(jsonObject, JsonObject.class);
								boolean skip = false;
								for (JsonElement jsonElement : blacklist) {
									if (jsonElement.getAsString().equals(flip.get("id").getAsString())) {
										skip = true;
										break;
									}
								}
								if (skip) {
									continue;
								}
								mc = Minecraft.getMinecraft();
								if (mc.theWorld != null) {

									String name = flip.get("name").getAsString();
									String stars = "";
									int index = name.indexOf("✪");
									if (index > -1) {
										stars = name.substring(index);
										name = name.substring(0, index);
									}

									double price = flip.get("price").getAsDouble();
									double listFor = flip.get("listFor").getAsDouble();
									double profit = flip.get("profit").getAsDouble();

									if (price > Utils.getPurse()) {
										continue;
									}

									if (config.has("minProfit") && profit < config.get("minProfit").getAsInt()) {
										continue;
									}

									if (config.has("minPercent")
											&& (profit / listFor) * 100 < config.get("minPercent").getAsInt()) {
										continue;
									}

									ChatComponentText msg = new ChatComponentText(EnumChatFormatting.GOLD + "[NEC] "
											+ Utils.getColorCodeFromRarity(flip.get("rarity").getAsString()) + name
											+ EnumChatFormatting.GOLD + stars + EnumChatFormatting.GREEN + " "
											+ Utils.formatPrice(flip.get("price").getAsDouble())
											+ EnumChatFormatting.WHITE + "->" + EnumChatFormatting.GREEN
											+ Utils.formatPrice(flip.get("listFor").getAsDouble()) + " " + "+"
											+ Utils.formatPrice(flip.get("profit").getAsDouble()));

									msg.setChatStyle(new ChatStyle()
											.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
													"/viewauction " + flip.get("uuid").getAsString())));

									if (config.get("toggle").getAsBoolean()) {
										mc.thePlayer.addChatMessage(new ChatComponentText(""));
										mc.thePlayer.addChatMessage(msg);
									}
								}

							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			serverListenerThread.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void autoReconnect() {
		scheduledExecutorService.scheduleAtFixedRate(() -> {
			if (!isSocketConnected(socket)) {
				start();
			}
		}, 10, 1, TimeUnit.SECONDS);

	}

	private static boolean isSocketConnected(Socket socket) {
		try {
			// Check if the socket's input stream is closed
			if (socket.getInputStream().read() == -1) {
				return false;
			}

			// Check if the socket's output stream is closed
			socket.getOutputStream().write(0);
		} catch (IOException e) {
			return false;
		}

		return true;
	}
}
