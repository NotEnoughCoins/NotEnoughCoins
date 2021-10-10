package me.mindlessly.notenoughcoins.events;

import me.mindlessly.notenoughcoins.commands.Flip;
import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class OnWorldJoin {
	
	
	@SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		
		if(event.world.isRemote) {
			if(!(event.entity instanceof EntityPlayer)) {
				return;
			}
			
			EntityPlayer player = (EntityPlayer) event.entity;
			if(ConfigHandler.hasKey(Configuration.CATEGORY_GENERAL, "Flip")){
	    		    Flip.flip(player);
			}
		}
	}
}