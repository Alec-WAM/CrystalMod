package alec_wam.CrystalMod.integration;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.common.Loader;

public class ModIntegration {
	
	private static List<IModIntegration> integrations = new ArrayList<IModIntegration>();
	
	
	public static void register(){
		if(Loader.isModLoaded("tconstruct"))integrations.add(new TConstructIntegration());
		if(Loader.isModLoaded("EnderIO"))integrations.add(new EnderIOIntegration());
	}
	
	public static void preInit(){
		for(IModIntegration inte : integrations){
			if(Loader.isModLoaded(inte.getModID())){
				inte.preInit();
	        }
		}
	}
	
	public static void init(){
		for(IModIntegration inte : integrations){
			if(Loader.isModLoaded(inte.getModID())){
				inte.init();
	        }
		}
	}
	
	public static void postInit(){
		for(IModIntegration inte : integrations){
			if(Loader.isModLoaded(inte.getModID())){
				inte.postInit();
	        }
		}
	}

	public static interface IModIntegration{
		public String getModID();
		
		public default void preInit(){}
		public default void init(){}
		public default void postInit(){}
	}
	
}
