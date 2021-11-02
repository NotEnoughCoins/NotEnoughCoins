package me.mindlessly.notenoughcoins.events;

import me.mindlessly.notenoughcoins.commands.subcommands.Toggle;
import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.Timer;
import java.util.TimerTask;

public class OnWorldJoin {

    @SubscribeEvent
    public void onEntityJoinWorld(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (ConfigHandler.hasKey(Configuration.CATEGORY_GENERAL, "Flip")) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Toggle.flip(Minecraft.getMinecraft().thePlayer);
                }
            }, 2000);
        }
    }
}