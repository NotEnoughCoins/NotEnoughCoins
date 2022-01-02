package me.mindlessly.notenoughcoins.utils;

import com.google.common.collect.Sets;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.essential.universal.UChat;
import gg.essential.universal.wrappers.message.UTextComponent;
import me.mindlessly.notenoughcoins.Main;
import me.mindlessly.notenoughcoins.Reference;
import me.mindlessly.notenoughcoins.objects.BestSellingMethod;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
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
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public class Utils {
    //Stolen from Biscut & Moulberry and used for detecting whether in skyblock
    private static final Set<String> SKYBLOCK_IN_ALL_LANGUAGES = Sets.newHashSet("SKYBLOCK", "\u7A7A\u5C9B\u751F\u5B58", "\u7A7A\u5CF6\u751F\u5B58");
    private static boolean hasSkyblockScoreboard;

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
                    id = petInfoObject.get("type").getAsString() + petInfoObject.get("tier").getAsString();
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

    public static Map.Entry<BestSellingMethod, Long> getBestSellingMethod(String id) {
        if (id == null) return new AbstractMap.SimpleEntry<>(BestSellingMethod.NONE, 0L);
        if (id.equals("POTION")) return new AbstractMap.SimpleEntry<>(BestSellingMethod.NONE, 0L);
        BestSellingMethod method = BestSellingMethod.NONE;
        long bestPrice = 0;
        if (Main.lbinItem.containsKey(id) && Main.lbinItem.get(id) - getTax(Main.lbinItem.get(id)) > bestPrice) {
            bestPrice = Main.lbinItem.get(id) - getTax(Main.lbinItem.get(id));
            method = BestSellingMethod.LBIN;
        }
        if (Main.bazaarItem.containsKey(id) && Main.bazaarItem.get(id) > bestPrice) {
            bestPrice = Main.bazaarItem.get(id);
            method = BestSellingMethod.BAZAAR;
        }
        if (Main.npcItem.containsKey(id) && Main.npcItem.get(id) > bestPrice) {
            bestPrice = Main.npcItem.get(id);
            method = BestSellingMethod.NPC;
        }
        return new AbstractMap.SimpleEntry<>(method, bestPrice);
    }

    public static EnumChatFormatting getColorCodeFromRarity(String rarity) {
        switch (rarity) {
            case "COMMON":
                return EnumChatFormatting.WHITE;
            case "UNCOMMON":
                return EnumChatFormatting.GREEN;
            case "RARE":
                return EnumChatFormatting.BLUE;
            case "EPIC":
                return EnumChatFormatting.DARK_PURPLE;
            case "LEGENDARY":
                return EnumChatFormatting.GOLD;
            case "MYTHIC":
                return EnumChatFormatting.LIGHT_PURPLE;
            case "DIVINE":
                return EnumChatFormatting.AQUA;
            case "SPECIAL":
            case "VERY_SPECIAL":
                return EnumChatFormatting.RED;
            default:
                return EnumChatFormatting.WHITE;
        }
    }
}
