package me.mindlessly.notenoughcoins.events;

import me.mindlessly.notenoughcoins.commands.Flip;
import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class OnWorldJoin {
	
	
	@SubscribeEvent
    public void onEntityJoinWorld(PlayerLoggedInEvent event) {
			
		EntityPlayer player = (EntityPlayer) event.player;
		if(ConfigHandler.hasKey(Configuration.CATEGORY_GENERAL, "Flip")){
    		    Flip.flip(player);
		}
	}
}