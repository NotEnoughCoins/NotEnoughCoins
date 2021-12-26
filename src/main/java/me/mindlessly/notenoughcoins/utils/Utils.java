package me.mindlessly.notenoughcoins.utils;

import com.google.common.collect.Sets;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.essential.universal.UChat;
import gg.essential.universal.wrappers.message.UTextComponent;
import me.mindlessly.notenoughcoins.Config;
import me.mindlessly.notenoughcoins.Reference;
import me.mindlessly.notenoughcoins.commands.subcommands.Toggle;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;

public class Utils {
    //Stolen from Biscut & Moulberry and used for detecting whether in skyblock
    private static final Set<String> SKYBLOCK_IN_ALL_LANGUAGES = Sets.newHashSet("SKYBLOCK", "\u7A7A\u5C9B\u751F\u5B58", "\u7A7A\u5CF6\u751F\u5B58");
    private static boolean hasSkyblockScoreboard;
    private static short failedTimes;

    private static String formatValue(final long amount, final long div, final char suffix) {
        return new DecimalFormat(".##").format(amount / (double) div) + suffix;
    }

    public static JsonElement getJson(String jsonUrl) throws IOException {
        URL url = new URL(jsonUrl);
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("Connection", "close");
        conn.setRequestProperty("User-Agent", "NotEnoughCoins/1.0");
        return new JsonParser().parse(new InputStreamReader(conn.getInputStream()));
    }

    public static String formatValue(final long amount) {
        if (amount >= 1_000_000_000_000_000L) {
            return formatValue(amount, 1_000_000_000_000_000L, 'q');
        } else if (amount >= 1_000_000_000_000L) {
            return formatValue(amount, 1_000_000_000_000L, 't');
        } else if (amount >= 1_000_000_000L) {
            return formatValue(amount, 1_000_000_000L, 'b');
        } else if (amount >= 1_000_000L) {
            return formatValue(amount, 1_000_000L, 'm');
        } else if (amount >= 100_000L) {
            return formatValue(amount, 1000L, 'k');
        }

        return NumberFormat.getInstance().format(amount);
    }

    public static void sendMessageWithPrefix(String message) {
        UChat.chat(EnumChatFormatting.GOLD + ("[NEC] ") + message.replaceAll("&", "§"));
    }

    public static void sendMessageWithPrefix(String message, ClickEvent clickEvent) {
        UTextComponent result = new UTextComponent(EnumChatFormatting.GOLD + ("[NEC] ") + message.replaceAll("&", "§"));
        result.setChatStyle(new ChatStyle().setChatClickEvent(clickEvent));
        UChat.chat(result);
    }

    public static void checkForUpdate() {
        String latestVersion;
        try {
            latestVersion = Utils.getJson("https://api.github.com/repos/mindlesslydev/NotEnoughCoins/releases").getAsJsonArray().get(0).getAsJsonObject().get("tag_name").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (!Objects.equals(latestVersion, Reference.VERSION)) {
            Utils.sendMessageWithPrefix("&aAn update (" + latestVersion + ") is available at https://github.com/mindlesslydev/NotEnoughCoins/releases", new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/mindlesslydev/NotEnoughCoins/releases"));
        }
    }

    public static int getTax(int price) {
        float taxRate = 0.01f;
        if (price >= 1000000) {
            taxRate = 0.02f; // 2% = starting tax + over 1m tax
        }
        return (int) Math.floor(price * taxRate);
    }

    public static int getTaxedProfit(int price, int profit) {
        return profit - getTax(price);
    }

    public static String getProfitText(int profit) {
        EnumChatFormatting color = EnumChatFormatting.GRAY;
        if (profit >= 100_000) {
            color = EnumChatFormatting.GOLD;
        }
        if (profit >= 500_000) {
            color = EnumChatFormatting.GREEN;
        }
        if (profit >= 1_000_000) {
            color = EnumChatFormatting.DARK_GREEN;
        }
        if (profit >= 10_000_000) {
            color = EnumChatFormatting.AQUA;
        }
        return color + "+$" + formatValue(profit);
    }

    public static boolean isOnSkyblock() {
        return hasSkyblockScoreboard();
    }

    public static boolean hasSkyblockScoreboard() {
        return hasSkyblockScoreboard;
    }

    public static void updateSkyblockScoreboard() { // Thanks to NEU
        Minecraft mc = Minecraft.getMinecraft();

        if (mc != null && mc.theWorld != null && mc.thePlayer != null) {
            if (mc.isSingleplayer() || mc.thePlayer.getClientBrand() == null ||
                !mc.thePlayer.getClientBrand().toLowerCase().contains("hypixel")) {
                hasSkyblockScoreboard = false;
                return;
            }

            Scoreboard scoreboard = mc.theWorld.getScoreboard();
            ScoreObjective sidebarObjective = scoreboard.getObjectiveInDisplaySlot(1);
            if (sidebarObjective != null) {
                String objectiveName = sidebarObjective.getDisplayName().replaceAll("(?i)\\u00A7.", "");
                for (String skyblock : SKYBLOCK_IN_ALL_LANGUAGES) {
                    if (objectiveName.contains(skyblock)) {
                        hasSkyblockScoreboard = true;
                        return;
                    }
                }
            }
            hasSkyblockScoreboard = false;
        }
    }

    public static String getIDFromItemStack(ItemStack stack) { // Thanks to NEU
        if (stack == null) return null;
        NBTTagCompound tag = stack.getTagCompound();
        String id = null;
        if (tag != null && tag.hasKey("ExtraAttributes", 10)) {
            NBTTagCompound ea = tag.getCompoundTag("ExtraAttributes");

            if (ea.hasKey("id", 8)) {
                id = ea.getString("id");
            } else {
                return null;
            }

            if ("PET".equals(id)) {
                String petInfo = ea.getString("petInfo");
                if (petInfo.length() > 0) {
                    JsonObject petInfoObject = new GsonBuilder().setPrettyPrinting().create().fromJson(petInfo, JsonObject.class);
                    id = petInfoObject.get("type").getAsString();
                    String tier = petInfoObject.get("tier").getAsString();
                    id += ";" + tier;
                }
            }
            if ("ENCHANTED_BOOK".equals(id)) {
                NBTTagCompound enchants = ea.getCompoundTag("enchantments");

                for (String enchname : enchants.getKeySet()) {
                    id = enchname.toUpperCase() + ";" + enchants.getInteger(enchname);
                    break;
                }
            }
        }

        return id;
    }

    public static void blacklistMessage() {
        failedTimes++;
        if (failedTimes > 20) {
            Utils.sendMessageWithPrefix("&cFailed to fetch from NEC backend, this might be caused by one of the following reasons:\n" +
                "&c1. The backend server is down, and should recover within in next few minutes or hours\n" +
                "&c2. You have been blacklisted from the mod for using macro scripts\n" +
                "&c3. You are using a version of the mod that is not compatible with the backend server\n" +
                "&cTherefore the mod has disabled flipping, if you wish to re-enable it when this is sorted out, please use the command /nec or /nec toggle");
            Config.enabled = false;
            Toggle.updateConfig();
            failedTimes = 0;
        }
    }

    public static void runInAThread(Callable<Void> callable) {
        new Thread(() -> {
            try {
                callable.call();
            } catch (Exception e) {
                Reference.logger.error(e.getMessage(), e);
            }
        }).start();
    }

    public static String removeColorCodes(String in) {
        return in.replaceAll("(?i)\\u00A7.", "");
    }
}
