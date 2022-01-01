package me.mindlessly.notenoughcoins.events;

import gg.essential.universal.UKeyboard;
import me.mindlessly.notenoughcoins.Config;
import me.mindlessly.notenoughcoins.Main;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class OnTooltip {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onTooltip(ItemTooltipEvent event) {
        if (!Utils.isOnSkyblock()) return;
        String id = Utils.getIDFromItemStack(event.itemStack);
        if (Config.debug&&id!=null) {
            event.toolTip.add(EnumChatFormatting.YELLOW + EnumChatFormatting.BOLD.toString() + "Item ID: " +
                EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + id);
        }
        if (Config.bestSellingMethod) {
            if (id == null) return;
            if (id.equals("POTION")) return; // Potions are not supported
            String bestMethod = null;
            int bestPrice = 0;
            boolean shifted = UKeyboard.isShiftKeyDown();
            if (Main.bazaarItem.containsKey(id) && Main.bazaarItem.get(id) > bestPrice) {
                bestPrice = Main.bazaarItem.get(id);
                bestMethod = "Bazaar ($" + Utils.formatValue(shifted ? (long) bestPrice * event.itemStack.stackSize : bestPrice) + ")";
            }
            if (Main.lbinItem.containsKey(id) && Main.lbinItem.get(id) > bestPrice) {
                bestPrice = Main.lbinItem.get(id);
                bestMethod = "Buy-It-Now ($" + Utils.formatValue(shifted ? (long) bestPrice * event.itemStack.stackSize : bestPrice) + ")";
            }
            if (Main.npcItem.containsKey(id) && Main.npcItem.get(id) > bestPrice) {
                bestPrice = Main.npcItem.get(id);
                bestMethod = "NPC ($" + Utils.formatValue(shifted ? (long) bestPrice * event.itemStack.stackSize : bestPrice) + ")";
            }
            if (bestMethod == null) return;
            if (event.itemStack.stackSize > 1 && !shifted &&
                !event.toolTip.contains(EnumChatFormatting.DARK_GRAY + "[SHIFT show x" + event.itemStack.stackSize + "]")) { // compatible with NEC
                event.toolTip.add(EnumChatFormatting.DARK_GRAY + "[SHIFT show x" + event.itemStack.stackSize + "]");
            }

            event.toolTip.add(EnumChatFormatting.YELLOW + EnumChatFormatting.BOLD.toString() + "Best Selling Method: " +
                EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + bestMethod);
        }
    }
}
