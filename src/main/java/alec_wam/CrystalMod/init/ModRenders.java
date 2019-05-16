package alec_wam.CrystalMod.init;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.tiles.chests.metal.TileEntityMetalCrystalChest;
import alec_wam.CrystalMod.tiles.chests.metal.TileEntityMetalCrystalChestRender;
import alec_wam.CrystalMod.tiles.chests.wireless.TileEntityWirelessChest;
import alec_wam.CrystalMod.tiles.chests.wireless.TileEntityWirelessChestRender;
import alec_wam.CrystalMod.tiles.chests.wooden.TileEntityWoodenCrystalChest;
import alec_wam.CrystalMod.tiles.chests.wooden.TileEntityWoodenCrystalChestRender;
import alec_wam.CrystalMod.tiles.crate.TileEntityCrate;
import alec_wam.CrystalMod.tiles.crate.TileEntityCrateRender;
import alec_wam.CrystalMod.tiles.fusion.TileEntityFusionPedestal;
import alec_wam.CrystalMod.tiles.fusion.TileEntityFusionPedestalRender;
import alec_wam.CrystalMod.tiles.fusion.TileEntityPedestal;
import alec_wam.CrystalMod.tiles.fusion.TileEntityPedestalRender;
import alec_wam.CrystalMod.tiles.jar.TileEntityJar;
import alec_wam.CrystalMod.tiles.jar.TileEntityJarRender;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CrystalMod.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class ModRenders {
	@SubscribeEvent
	public static void register(final FMLClientSetupEvent event) {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrate.class, new TileEntityCrateRender());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWoodenCrystalChest.class, new TileEntityWoodenCrystalChestRender());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMetalCrystalChest.class, new TileEntityMetalCrystalChestRender());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWirelessChest.class, new TileEntityWirelessChestRender());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPedestal.class, new TileEntityPedestalRender());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFusionPedestal.class, new TileEntityFusionPedestalRender());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityJar.class, new TileEntityJarRender());
	}
}
