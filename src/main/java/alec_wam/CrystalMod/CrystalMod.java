package alec_wam.CrystalMod;

import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import alec_wam.CrystalMod.api.energy.CapabilityCrystalEnergy;
import alec_wam.CrystalMod.client.BakedModelEventHandler;
import alec_wam.CrystalMod.client.ClientEventHandler;
import alec_wam.CrystalMod.client.GuiHandler;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.init.ModRecipes;
import alec_wam.CrystalMod.tiles.pipes.PipeNetworkTickHandler;
import alec_wam.CrystalMod.world.CrystalModWorldGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CrystalMod.MODID)
public class CrystalMod {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "crystalmod";
	public static final String NAME = "Crystal Mod";	
	
	static {
		FluidRegistry.enableUniversalBucket();
	}
	
	public CrystalMod() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
	}
	
	public void preInit(final FMLCommonSetupEvent event) {
		LOGGER.info("CrystalMod init");
		DistExecutor.runWhenOn(Dist.CLIENT, () -> BakedModelEventHandler::registerOBJ);
		
		CrystalModWorldGenerator.instance.setupFeatures();
		ModBlocks.addBlocksToTags();
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.GUIFACTORY, () -> GuiHandler::openGui);
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(PipeNetworkTickHandler.INSTANCE);
        ModRecipes.registerModRecipes();
        CapabilityCrystalEnergy.register();
	}

	public static String resource(String res) {
		return String.format("%s:%s", CrystalMod.MODID.toLowerCase(Locale.US), res);
	}
	
	public static ResourceLocation resourceL(String res){
		return new ResourceLocation(resource(res));
	}
	
	public static String resourceDot(String res) {
		return String.format("%s.%s", CrystalMod.MODID.toLowerCase(Locale.US), res);
	}
}
