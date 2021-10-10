package me.mindlessly.notenoughcoins.commands;

import me.mindlessly.notenoughcoins.utils.ApiHandler;
import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.config.Configuration;

import java.util.*;

public class Flip extends CommandBase {

  // Take initial set of lbins, take second set, and use compared set to identify the biggest
  // gainers/losers
  // namedSet is used to replace internal ids with actual item names
  public static LinkedHashMap<String, Double> initialDataset = new LinkedHashMap<>();
  public static LinkedHashMap<String, Double> secondDataset = new LinkedHashMap<>();
  public static LinkedHashMap<String, Double> namedDataset = new LinkedHashMap<>();
  public static double purse;
  public static ArrayList<String> commands = new ArrayList<String>();

  private static int auctionPages = 0;
  private static int cycle = 0;
  private static Timer timer = new Timer();

  @Override
  public boolean canCommandSenderUseCommand(ICommandSender sender) {
    return true;
  }

  @Override
  public String getCommandName() {
    return "flip";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "/flip";
  }
  

  @Override
  public void processCommand(ICommandSender sender, String[] args) {
	if(ConfigHandler.hasKey(Configuration.CATEGORY_GENERAL, "Flip")) {
		if(ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "Flip").equals("true")) {
	    	ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL, 
	    			"Flip", 
	    			"false");
	    	
	    }else if(ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "Flip").equals("false")) {
	    	ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL,
	    			"Flip", 
	    			"true");
	    }
	    
	}else{
    	ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL,
    			"Flip", 
    			"true");
    	}

    flip((EntityPlayer) sender.getCommandSenderEntity());
  }
  
  public static void flip(EntityPlayer sender) {
	    if (ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "Flip").equals("true")) {
	        ChatComponentText enableText =
	            new ChatComponentText(
	                EnumChatFormatting.GOLD
	                    + ("NEC ")
	                    + EnumChatFormatting.GREEN
	                    + ("Flipper alerts enabled."));
	        sender.addChatMessage(enableText);
	        ApiHandler.getAuctionAverages(initialDataset);
	        auctionPages = ApiHandler.getNumberOfPages();

	        timer.schedule(
	            new TimerTask() {
	              @Override
	              public void run() {
	                if (cycle == auctionPages) {
	                  cycle = 0;
	                }

	                String name = sender.getName();
	                String id = ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "APIKey");
	                try {
	                  ApiHandler.updatePurseCoins(id, name);
	                } catch (Exception e) {
	                  sender.addChatMessage(new ChatComponentText("Could not load purse."));
	                }
	                ApiHandler.itemIdsToNames(secondDataset);
	                ApiHandler.getFlips(namedDataset, cycle, commands);
	                if (namedDataset.size() > 0) {
	                  purse = Math.round(purse);
	                  /*ChatComponentText runtext = new ChatComponentText(
	                  	EnumChatFormatting.GOLD + ("NEC ") + EnumChatFormatting.AQUA + ("Suggested Flips:")
	                  );
	                  sender.addChatMessage(runtext);
	                  if (!enable) {
	                  	return;
	                  }
	                  sender.addChatMessage(
	                  		new ChatComponentText(
	                  			EnumChatFormatting.GOLD + "Your Budget: " + EnumChatFormatting.WHITE + (long) purse + "\n"
	                  		)
	                  	);*/
	                  int count = 0;

	                  for (Map.Entry<String, Double> entry : namedDataset.entrySet()) {
	                    if (count == 3) {
	                      break;
	                    }
	                    long profit = Math.abs(entry.getValue().longValue());
	                    IChatComponent result =
	                        new ChatComponentText(
	                            EnumChatFormatting.AQUA
	                                + "[NEC] "
	                                + EnumChatFormatting.YELLOW
	                                + entry.getKey()
	                                + " "
	                                + (profit > 200_000 || purse / 5 < 100_000
	                                    ? EnumChatFormatting.GREEN
	                                    : profit > 100_000 || purse / 5 < 200_000
	                                        ? EnumChatFormatting.GOLD
	                                        : EnumChatFormatting.YELLOW)
	                                + "+$"
	                                + Utils.formatValue(profit));

	                    ChatStyle style =
	                        new ChatStyle()
	                            .setChatClickEvent(
	                                new ClickEvent(Action.RUN_COMMAND, commands.get(count)) {
	                                  @Override
	                                  public Action getAction() {
	                                    // custom behavior
	                                    return Action.RUN_COMMAND;
	                                  }
	                                });
	                    result.setChatStyle(style);

	                    sender.addChatMessage(result);

	                    count++;
	                  }
	                }
	                namedDataset.clear();
	                cycle++;
	              }
	            },
	            1000,
	            1000);

	        timer.schedule(
	            new TimerTask() {
	              @Override
	              public void run() {
	                auctionPages = ApiHandler.getNumberOfPages();
	                
	                try {
	                    ApiHandler.getAuctionAverages(initialDataset);
	                  } catch (Exception e) {
	                    sender.addChatMessage(new ChatComponentText("Could not load BINs."));
	                  }
	              }
	            },
	            60000,
	            60000);

	      } else {
	        ChatComponentText enableText =
	            new ChatComponentText(
	                EnumChatFormatting.GOLD
	                    + ("NEC ")
	                    + EnumChatFormatting.RED
	                    + ("Flipper alerts disabled."));
	        sender.addChatMessage(enableText);
	        timer.cancel();
	        timer.purge();
	        timer = new Timer();
	      }
  }
}
