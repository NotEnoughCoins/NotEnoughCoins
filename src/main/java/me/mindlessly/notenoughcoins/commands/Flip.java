package me.mindlessly.notenoughcoins.commands;

import java.io.IOException;
import java.util.Comparator;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.mindlessly.notenoughcoins.utils.APIPull;
import me.mindlessly.notenoughcoins.utils.BINScraper;
import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;
import me.mindlessly.notenoughcoins.commands.Data;

public class Flip extends CommandBase {
	
	//Take initial set of lbins, take second set, and use compared set to identify the biggest gainers/losers
	public HashMap<String,Double> initialDataset  = new HashMap<String,Double>();
	public HashMap<String,Double> secondDataset  = new HashMap<String,Double>();
	public HashMap<String,Double> comparedDataset  = new HashMap<String,Double>();
	public String[] data;
	public static double purse;
	
	private boolean enable = false;
	public String signage = null;
	
	
	

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) 
	{
		return true;
	}
	
	@Override
	public String getCommandName() 
	{
		return "flip";
	}
	
	
	@Override
	public String getCommandUsage(ICommandSender sender) 
	{
		return "/flip";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		enable = !enable;
		
		if(enable) 
		{
			ChatComponentText enabletext = new ChatComponentText(EnumChatFormatting.GOLD+("NEC ") + EnumChatFormatting.GREEN+("Flipper alerts enabled."));
			sender.addChatMessage(enabletext);
			BINScraper.getBins(initialDataset , data, sender);
			 
			 Timer timer = new Timer();
		        timer.schedule(new TimerTask() 
		        {
		            @Override
		            public void run() 
		            {
		            	
            	    	String name = sender.getName();
            	    	String id = ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "APIKey");
            	    	try 
            	    	{
            	    		BINScraper.getBins(secondDataset, data, sender);
            				}
            			 catch (Exception e) 
            	    	{
            				sender.addChatMessage(new ChatComponentText("Could not load BINs."));
            			}
            	    	try 
            	    	{
            	    		APIPull.webrequest(id, name, purse);
            	    	}
            	    	 catch (Exception e) 
            	    	{
            	    		 sender.addChatMessage(new ChatComponentText("Could not load purse."));
            	    	 }
            	    	
		            	
		            	purse = Math.round(purse);
		    			ChatComponentText runtext = new ChatComponentText(EnumChatFormatting.GOLD+("NEC ") + EnumChatFormatting.AQUA+("Suggested Flips:"));
		    			sender.addChatMessage(runtext);
		    			Minecraft mc = Minecraft.getMinecraft();
		            	if(!enable) {
		            		return;
		            	}
		            	int length = secondDataset.size();
		            	for (HashMap.Entry<String, Double> entry : initialDataset.entrySet()) {
		            		String key = entry.getKey();
	            	    	Double difference = 0.0;
	            	    	Double price1 = initialDataset.get(key);
	            	    	Double price2 = 0.0;
							//precaution for if entry magically dissapears on website
	            	    	
	            	    	if(secondDataset.keySet().contains(key)) {
	            	    		price2  = secondDataset.get(key);
	            	    	}
	            	    	else {
	            	    		continue;
	            	    	}
	            	    	
	            	    	if(price1 >= price2) {
	            	    		difference = price1-price2;
	            	    		signage = "-";
	            	    	}
	            	    	else {
	            	    		difference = price2-price1;
	            	    		signage = "+";
	            	    	}
	            	    	
	            	    	//temporary measure to test if my code is fucked
	            	    	
	            	    	if(price2 <= purse && price2 < price1) {
	            	    		comparedDataset.put(key, difference);
	            	    	}

	            	    	
		            	    	
		            	    
		            	    
		            	    
		            	}
		            	//Sorted hashmap by descending order of value (largest % change)
            	    	HashMap<String, Double> unSortedMap = comparedDataset;
            	         
            	    	 
            	    	//LinkedHashMap preserve the ordering of elements in which they are inserted
            	    	LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
            	    	 
            	    	//Use Comparator.reverseOrder() for reverse ordering
            	    	unSortedMap.entrySet()
            	    	    .stream()
            	    	    .sorted(HashMap.Entry.comparingByValue(Comparator.reverseOrder())) 
            	    	    .forEachOrdered(x -> sortedMap.put(x.getKey(), (double) Math.round(x.getValue())));
            	    	
            	    	sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD+"Your Budget: "+
            	    			EnumChatFormatting.WHITE+String.valueOf((long)purse+"\n")));
            	    	int count = 0;
            	    	Data.auctiondata.clear();
            	    	for (Map.Entry<String, Double> entry : sortedMap.entrySet()) {
            	    		Data.auctiondata.put(entry.getKey(), entry.getValue());
            	    		}
            	    	for (Map.Entry<String, Double> entry : sortedMap.entrySet()) {
            	    		if (count == 3)
            	    			{
            	    			break;
            	    		}
            	    		
            	    		sender.addChatMessage(new ChatComponentText(entry.getKey()+signage+String.valueOf(entry.getValue().longValue())));
            	    		
            	    		count++;
            	    	}
            	    	initialDataset.clear();
            	    	for (Map.Entry<String, Double> entry : secondDataset.entrySet()) {
            	    		initialDataset.put(entry.getKey(), entry.getValue());
            	    	}
            	    	secondDataset.clear();
		            			
		            }
		        }, 60000,60000);
		        
		    }else {
				ChatComponentText enabletext = new ChatComponentText(EnumChatFormatting.GOLD+("NEC ") + EnumChatFormatting.RED+("Flipper alerts disabled."));
				sender.addChatMessage(enabletext);
				
		    }
			
		}		
}

 
