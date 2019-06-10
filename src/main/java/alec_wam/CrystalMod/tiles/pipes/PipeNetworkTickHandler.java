package alec_wam.CrystalMod.tiles.pipes;

import java.util.ArrayList;
import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@Mod.EventBusSubscriber(modid = CrystalMod.MODID, value = Dist.DEDICATED_SERVER, bus = Bus.MOD)
public class PipeNetworkTickHandler {
	public static final PipeNetworkTickHandler INSTANCE = new PipeNetworkTickHandler();
	
	public final List<PipeNetworkBase<?>> NETWORKS = new ArrayList<PipeNetworkBase<?>>();
	
	@SubscribeEvent
	public void serverTick(final TickEvent.ServerTickEvent event) {
		if(event.phase == Phase.END){
			for(PipeNetworkBase<?> network : NETWORKS){
				network.tick();
			}
		}
	}

}
