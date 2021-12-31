package me.mindlessly.notenoughcoins.events;

import me.mindlessly.notenoughcoins.Config;
import me.mindlessly.notenoughcoins.commands.subcommands.Toggle;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.Timer;
import java.util.TimerTask;

public class OnWorldJoin {
    boolean hasRan = false;

    @SubscribeEvent
    public void onEntityJoinWorld(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        Timer timer = new Timer();
        if (Config.enabled && !hasRan) {
            hasRan = true;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Toggle.updateConfig();
                }
            }, 2000);
        }
    }
}
