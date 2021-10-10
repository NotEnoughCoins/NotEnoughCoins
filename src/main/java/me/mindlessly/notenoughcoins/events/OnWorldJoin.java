package me.mindlessly.notenoughcoins.events;

import me.mindlessly.notenoughcoins.commands.Flip;
import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class OnWorldJoin {
	
	
	@SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (event.world.isRemote && event.entity == Minecraft.getMinecraft().thePlayer) {
			if(ConfigHandler.hasKey(Configuration.CATEGORY_GENERAL, "Flip")){
    		    Flip.flip(Minecraft.getMinecraft().thePlayer);
			}
		}
	}
}