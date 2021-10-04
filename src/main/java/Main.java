import me.mindlessly.notenoughcoins.NotEnoughCoins;
import me.mindlessly.notenoughcoins.Reference;
import me.mindlessly.notenoughcoins.commands.Data;
import me.mindlessly.notenoughcoins.commands.Flip;
import me.mindlessly.notenoughcoins.commands.SetKey;
import me.mindlessly.notenoughcoins.utils.ConfigHandler;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME)
public class Main {
	
	
	@EventHandler
	public void	preInit(FMLPreInitializationEvent event) 
	{
		ConfigHandler.init();
		
	
	}
	@EventHandler
	public void init(FMLInitializationEvent event) 
	{
		
		NotEnoughCoins.instance = new NotEnoughCoins();
		ClientCommandHandler.instance.registerCommand(new Flip());
		ClientCommandHandler.instance.registerCommand(new Data());
		ClientCommandHandler.instance.registerCommand(new SetKey());
		NotEnoughCoins.instance.init();

		
	
	}
}

