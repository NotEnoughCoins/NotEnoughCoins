package me.mindlessly.notenoughcoins.hooks;

import me.mindlessly.notenoughcoins.Config;
import me.mindlessly.notenoughcoins.objects.BestSellingMethod;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.List;

public class GuiContainerHook {
    public static final int GREEN_OVERLAY = new Color(0, 255, 0, 100).getRGB();

    public static boolean isSellMerchant(Container inventory) {
        if (inventory.inventorySlots.size() <= 49) return false;
        ItemStack itemStack = inventory.inventorySlots.get(49).getStack();
        if (itemStack != null) {
            if (itemStack.getItem() == Item.getItemFromBlock(Blocks.hopper) && itemStack.hasDisplayName() &&
                Utils.removeColorCodes(itemStack.getDisplayName()).equals("Sell Item")) {
                return true;
            }
            List<String> tooltip = itemStack.getTooltip(Minecraft.getMinecraft().thePlayer, false);
            for (String line : tooltip) {
                if (Utils.removeColorCodes(line).equals("Click to buyback!")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isBazaar(Container inventory) {
        if (inventory.getSlot(0) == null) return false;
        IInventory realInventory = inventory.getSlot(0).inventory;
        return realInventory.hasCustomName() && realInventory.getDisplayName().getUnformattedText().startsWith("Bazaar");
    }

    public static boolean isAuction(Container inventory) {
        if (inventory.getSlot(0) == null) return false;
        IInventory realInventory = inventory.getSlot(0).inventory;
        return realInventory.hasCustomName() && realInventory.getDisplayName().getUnformattedText().contains("Auction");
    }

    public static void drawSlot(Container inventorySlots, Slot slot) {
        if (Utils.isOnSkyblock()) {
            if (Config.bestSellingOverlay && slot.getHasStack()) {
                BestSellingMethod bestSellingMethod = Utils.getBestSellingMethod(Utils.getIDFromItemStack(slot.getStack())).getKey();
                boolean drawOverlay = bestSellingMethod == BestSellingMethod.NPC && isSellMerchant(inventorySlots);
                if (bestSellingMethod == BestSellingMethod.BAZAAR && isBazaar(inventorySlots)) {
                    drawOverlay = true;
                }
                if (bestSellingMethod == BestSellingMethod.LBIN && isAuction(inventorySlots)) {
                    drawOverlay = true;
                }
                if (drawOverlay) {
                    int slotLeft = slot.xDisplayPosition;
                    int slotTop = slot.yDisplayPosition;
                    int slotRight = slotLeft + 16;
                    int slotBottom = slotTop + 16;
                    Gui.drawRect(slotLeft, slotTop, slotRight, slotBottom, GREEN_OVERLAY);
                }
            }
        }
    }
}
