package me.mindlessly.notenoughcoins.events;

import me.mindlessly.notenoughcoins.Main;
import me.mindlessly.notenoughcoins.commands.subcommands.Toggle;
import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.Timer;
import java.util.TimerTask;

public class OnWorldJoin {

    @SubscribeEvent
    public void onEntityJoinWorld(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        Timer timer = new Timer();
        if (ConfigHandler.hasKey(Configuration.CATEGORY_GENERAL, "Flip")) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Toggle.flip(Minecraft.getMinecraft().thePlayer);
                }
            }, 2000);
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