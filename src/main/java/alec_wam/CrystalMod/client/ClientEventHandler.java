package alec_wam.CrystalMod.client;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = CrystalMod.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class ClientEventHandler {
	public static volatile int elapsedTicks;
    
    @SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
    	if (event.phase == TickEvent.Phase.END && event.type == TickEvent.Type.CLIENT && event.side == LogicalSide.CLIENT) {
    		elapsedTicks++;
    	}
    }
}
