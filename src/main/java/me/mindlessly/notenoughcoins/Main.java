package me.mindlessly.notenoughcoins;

import me.mindlessly.notenoughcoins.commands.NECCommand;
import me.mindlessly.notenoughcoins.commands.subcommands.Help;
import me.mindlessly.notenoughcoins.commands.subcommands.Subcommand;
import me.mindlessly.notenoughcoins.commands.subcommands.Toggle;
import me.mindlessly.notenoughcoins.config.Config;
import me.mindlessly.notenoughcoins.events.ChatReceivedEvent;
import me.mindlessly.notenoughcoins.events.OnWorldJoin;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class Main {
    public static boolean checkedForUpdate = false;
    public static Config config = new Config();
    public static NECCommand commandManager = new NECCommand(new Subcommand[]{
            new Toggle(),
            new Help()
    });

    @EventHandler
    public void init(FMLInitializationEvent event) {
        config.preload();
        ClientCommandHandler.instance.registerCommand(commandManager);
        MinecraftForge.EVENT_BUS.register(new OnWorldJoin());
        MinecraftForge.EVENT_BUS.register(new ChatReceivedEvent());
    }
}
