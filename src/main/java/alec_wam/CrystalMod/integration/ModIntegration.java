package alec_wam.CrystalMod.integration;

import net.minecraftforge.fml.common.Loader;

public class ModIntegration {

	public static void preInit(){
		if(Loader.isModLoaded("tconstruct")){
        	TConstructIntegration.preInit();
        }
	}
	
	public static void init(){
		if(Loader.isModLoaded("tconstruct")){
        	TConstructIntegration.init();
        }
	}
	
	public static void postInit(){
		
	}
	
}
