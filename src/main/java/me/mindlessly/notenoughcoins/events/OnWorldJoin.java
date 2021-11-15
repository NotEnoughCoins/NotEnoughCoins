package me.mindlessly.notenoughcoins.events;

import me.mindlessly.notenoughcoins.Main;
import me.mindlessly.notenoughcoins.commands.subcommands.Toggle;
import me.mindlessly.notenoughcoins.utils.Config;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.Timer;
import java.util.TimerTask;

public class OnWorldJoin {

    @SubscribeEvent
    public void onEntityJoinWorld(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        Timer timer = new Timer();
        if (Config.enabled) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Toggle.flip();
                }
            }, 2000);
        } else {
            Toggle.updateConfig();
        }
        if (!Main.checkedForUpdate) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Utils.checkForUpdate();
                    Main.checkedForUpdate = true;
                }
            }, 2000);
        }
    }
}