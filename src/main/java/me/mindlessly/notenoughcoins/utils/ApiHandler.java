package me.mindlessly.notenoughcoins.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.mindlessly.notenoughcoins.Authenticator;
import me.mindlessly.notenoughcoins.Config;
import me.mindlessly.notenoughcoins.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static me.mindlessly.notenoughcoins.utils.Utils.getJson;

public class ApiHandler {
    public static void updatePurse() throws IOException {
        if (Utils.isOnSkyblock()) {
            Scoreboard scoreboard = Minecraft.getMinecraft().theWorld.getScoreboard();
            List<Score> scores = new LinkedList<>(scoreboard.getSortedScores(scoreboard.getObjectiveInDisplaySlot(1)));
            for (Score score : scores) {
                ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(score.getPlayerName());
                String line = Utils.removeColorCodes(ScorePlayerTeam.formatPlayerName(scorePlayerTeam, score.getPlayerName()));
                if (line.contains("Purse: ") || line.contains("Piggy: ")) {
                    Main.balance = Double.parseDouble(line.replaceAll("\\(\\+[\\d]+\\)", "").replaceAll("[^\\d.]", ""));
                    return;
                }
            }
        }
        JsonArray profilesArray = Objects
            .requireNonNull(
                getJson("https://api.hypixel.net/skyblock/profiles?key=" + Config.apiKey + "&uuid=" + Authenticator.myUUID))
            .getAsJsonObject().getAsJsonArray("profiles");

        // Get last played profile
        int profileIndex = 0;
        Instant lastProfileSave = Instant.EPOCH;
        for (int i = 0; i < profilesArray.size(); i++) {
            Instant lastSaveLoop;
            try {
                lastSaveLoop = Instant.ofEpochMilli(profilesArray.get(i).getAsJsonObject().get("members")
                    .getAsJsonObject().get(Authenticator.myUUID).getAsJsonObject().get("last_save").getAsLong());
            } catch (Exception e) {
                continue;
            }

            if (lastSaveLoop.isAfter(lastProfileSave)) {
                profileIndex = i;
                lastProfileSave = lastSaveLoop;
            }
        }
        Main.balance = profilesArray.get(profileIndex).getAsJsonObject().get("members").getAsJsonObject().get(Authenticator.myUUID)
            .getAsJsonObject().get("coin_purse").getAsDouble();
    }

    public static void updateBazaar() throws IOException {
        JsonObject products = Objects.requireNonNull(getJson("https://api.hypixel.net/skyblock/bazaar"))
            .getAsJsonObject().getAsJsonObject("products");
        for (Map.Entry<String, JsonElement> itemEntry : products.entrySet()) {
            if (itemEntry.getValue().getAsJsonObject().getAsJsonArray("sell_summary").size() > 0) {
                Main.bazaarItem.put(itemEntry.getKey(), itemEntry.getValue().getAsJsonObject().getAsJsonArray("sell_summary").get(0).getAsJsonObject().get("pricePerUnit").getAsInt());
            }
        }
    }

    public static Void updateNPC() throws IOException {
        JsonArray items = Objects.requireNonNull(getJson("https://api.hypixel.net/resources/skyblock/items"))
            .getAsJsonObject().getAsJsonArray("items");
        for (JsonElement i : items) {
            JsonObject item = i.getAsJsonObject();
            if (item.has("npc_sell_price")) {
                Main.npcItem.put(item.get("id").getAsString(), item.get("npc_sell_price").getAsInt());
            }
        }
        return null;
    }
}
