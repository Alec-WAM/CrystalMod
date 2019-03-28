package alec_wam.CrystalMod;

import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import alec_wam.CrystalMod.world.CrystalModWorldGenerator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CrystalMod.MODID)
public class CrystalMod {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "crystalmod";
	public static final String NAME = "Crystal Mod";	
	
	public CrystalMod() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
	}
	
	public void preInit(final FMLCommonSetupEvent event) {
		LOGGER.info("CrystalMod init");
		CrystalModWorldGenerator.instance.setupFeatures();
	}

	public static String resource(String res) {
		return String.format("%s:%s", CrystalMod.MODID.toLowerCase(Locale.US), res);
	}
	
	public static String resourceDot(String res) {
		return String.format("%s.%s", CrystalMod.MODID.toLowerCase(Locale.US), res);
	}
}
