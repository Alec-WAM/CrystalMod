package alec_wam.CrystalMod.events;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.tiles.machine.crafting.grinder.InterModGrinderRecipes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

@Mod.EventBusSubscriber(modid = CrystalMod.MODID, value = Dist.DEDICATED_SERVER, bus = Bus.MOD)
public class ServerEventHandler {

	public static final ServerEventHandler INSTANCE = new ServerEventHandler();
	
	@SubscribeEvent
	public void addResourcesToServer(final FMLServerAboutToStartEvent event){
		event.getServer().getResourceManager().func_219534_a(InterModGrinderRecipes.INSTANCE);
	}
	
}
