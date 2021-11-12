package me.mindlessly.notenoughcoins;

import me.mindlessly.notenoughcoins.commands.NECCommand;
import me.mindlessly.notenoughcoins.commands.subcommands.*;
import me.mindlessly.notenoughcoins.events.OnWorldJoin;
import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import me.mindlessly.notenoughcoins.utils.Reference;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME)
public class Main {
    public static boolean checkedForUpdate = false;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new NECCommand(new Subcommand[]{
                new MinPercent(),
                new MinProfit(),
                new SetKey(),
                new Speed(),
                new Toggle(),
                new AlertSound(),
        }));
        MinecraftForge.EVENT_BUS.register(new OnWorldJoin());
    }
}
