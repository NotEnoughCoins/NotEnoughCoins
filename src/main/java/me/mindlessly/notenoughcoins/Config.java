package me.mindlessly.notenoughcoins;

import gg.essential.universal.UDesktop;
import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Category;
import gg.essential.vigilance.data.JVMAnnotationPropertyCollector;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyData;
import gg.essential.vigilance.data.PropertyType;
import gg.essential.vigilance.data.SortingBehavior;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Config extends Vigilant {
    public static final File CONFIG_FILE = new File("config/nec.toml");
    public static ArrayList<String> categoryFilter = new ArrayList<>(
        Arrays.asList("TRAVEL_SCROLL", "COSMETIC", "DUNGEON_PASS", "ARROW_POISON", "PET_ITEM"));
    @Property(
        type = PropertyType.SWITCH,
        category = "Flipping",
        subcategory = "Basic",
        name = "Enabled",
        description = "Whether the mod should check for and send flips"
    )
    public static boolean enabled = false;
    @Property(
        type = PropertyType.SWITCH,
        category = "Flipping",
        subcategory = "Basic",
        name = "Enabled only in skyblock",
        description = "Whether the mod should disable sending flips when not in SkyBlock"
    )
    public static boolean onlySkyblock = true;
    @Property(
        type = PropertyType.NUMBER,
        category = "Flipping",
        subcategory = "Basic",
        name = "Minimum Profit",
        description = "The minimum amount of profit that is required for the mod to send you the flip",
        max = Integer.MAX_VALUE,
        increment = 10000
    )
    public static int minProfit = 50000;
    @Property(
        type = PropertyType.NUMBER,
        category = "Flipping",
        subcategory = "Basic",
        name = "Minimum Demand",
        description = "The minimum sales per day that is required for the mod to send you the flip",
        max = Integer.MAX_VALUE,
        increment = 5
    )
    public static int minDemand = 10;
    @Property(
        type = PropertyType.PERCENT_SLIDER,
        category = "Flipping",
        subcategory = "Basic",
        name = "Minimum Profit Percentage",
        description = "The minimum percentage of profit that is required for the mod to send you the flip"
    )
    public static float minProfitPercentage = 0F;
    @Property(
        type = PropertyType.SWITCH,
        category = "Flipping",
        subcategory = "Basic",
        name = "Alert Sounds",
        description = "Whether a sound should be played upon flip sent"
    )
    public static boolean alertSounds = true;
    @Property(
        type = PropertyType.PARAGRAPH,
        category = "Flipping",
        subcategory = "Advanced",
        name = "Item Blacklist",
        description = "Exclude items from being sent as a flip (please configure this via the website provided below)"
    )
    public static String blacklistedIDs = "";
    @Property(
        type = PropertyType.SWITCH,
        category = "Flipping",
        subcategory = "Advanced",
        name = "Manipulation Check",
        description = "Whether the mod should check if the item was manipulated before sending the flip, DISABLE THIS AT YOUR OWN RISK AS YOU CAN LOSE YOUR MONEY TO MARKET MANIPULATORS"
    )
    public static boolean manipulationCheck = true;
    @Property(
        type = PropertyType.TEXT,
        category = "Confidential",
        name = "API Key", protectedText = true,
        description = "Run /api new to set it automatically, or paste one if you do not want to renew it"
    )
    public static String apiKey = "";
    @Property(
        type = PropertyType.SWITCH,
        category = "Confidential",
        name = "Debug",
        description = "Whether to show debug information, such as latency"
    )
    public static boolean debug = false;
    @Property(
        type = PropertyType.SWITCH,
        category = "Money Saving",
        name = "Best Selling Method",
        description = "Shows the best way to sell an item on its lore, the one that sells for the most will be shown"
    )
    public static boolean bestSellingMethod = true;
    @Property(
        type = PropertyType.SWITCH,
        category = "Money Saving",
        name = "Best Selling Method Item Overlay",
        description = "Shows a green overlay on the item if the current open menu is the best selling method for the item"
    )
    public static boolean bestSellingOverlay = true;
    @Property(
        type = PropertyType.SWITCH,
        category = "QOL",
        name = "Hide spam messages",
        description = "Hide messages that contain predefined keywords of scam advertisements and etc"
    )
    public static boolean hideSpam = true;

    public Config() {
        super(CONFIG_FILE, "NEC Configuration", new JVMAnnotationPropertyCollector(), new CustomSorting());
        initialize();
    }

    @Property(
        type = PropertyType.BUTTON,
        category = "Flipping",
        subcategory = "Advanced",
        name = "Blacklist Configuration Website",
        description = "Configure the blacklist via this website"
    )
    public static void blacklistConfigure() {
        UDesktop.browse(URI.create("https://nec.robothanzo.dev/panel/#/itemBlacklist"));
    }

    @Property(
        type = PropertyType.BUTTON,
        category = "Links",
        name = "Patreon",
        description = "Donate to cover the server's hosting costs!"
    )
    public static void patreon() {
        UDesktop.browse(URI.create("https://www.patreon.com/robothanzo"));
    }

    @Property(
        type = PropertyType.BUTTON,
        category = "Links",
        name = "GitHub",
        description = "Help with the development!"
    )
    public static void github() {
        UDesktop.browse(URI.create("https://github.com/mindlesslydev/NotEnoughCoins"));
    }

    @Property(
        type = PropertyType.BUTTON,
        category = "Links",
        name = "Discord",
        description = "Join our Discord server!"
    )
    public static void discord() {
        UDesktop.browse(URI.create("https://discord.gg/b3JBsh8fEd"));
    }
}

class CustomSorting extends SortingBehavior { // We compare by the order of the properties in the code
    @NotNull
    @Override
    public Comparator<? super Category> getCategoryComparator() {
        return (Comparator<Category>) (o1, o2) -> 0;
    }

    @NotNull
    @Override
    public Comparator<? super Map.Entry<String, ? extends List<PropertyData>>> getSubcategoryComparator() {
        return (Comparator<Map.Entry<String, ? extends List<PropertyData>>>) (o1, o2) -> 0;
    }

    @NotNull
    @Override
    public Comparator<? super PropertyData> getPropertyComparator() {
        return (Comparator<PropertyData>) (o1, o2) -> 0;
    }
}
