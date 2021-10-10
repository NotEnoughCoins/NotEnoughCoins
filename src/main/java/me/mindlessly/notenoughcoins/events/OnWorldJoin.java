package me.mindlessly.notenoughcoins.events;

import java.util.Timer;
import java.util.TimerTask;

import me.mindlessly.notenoughcoins.commands.Flip;
import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class OnWorldJoin {
	
	
	@SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (!event.world.isRemote && event.entity == Minecraft.getMinecraft().thePlayer) {
			if(ConfigHandler.hasKey(Configuration.CATEGORY_GENERAL, "Flip")){
				Timer timer = new Timer();
				  timer.schedule(
		            new TimerTask() {
		              @Override
		              public void run() {
		      		    Flip.flip(Minecraft.getMinecraft().thePlayer);
		              }
		            },
		            2000);
			}
		}
	}
}