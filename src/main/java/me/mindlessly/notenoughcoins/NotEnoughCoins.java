package me.mindlessly.notenoughcoins;


import net.minecraftforge.common.MinecraftForge;


public class NotEnoughCoins 
{
	public static NotEnoughCoins instance;

	
	
	public void init() 
	{
		
		MinecraftForge.EVENT_BUS.register(this);
		
		
		
	}
	
}
