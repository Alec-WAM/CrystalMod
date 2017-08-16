package alec_wam.CrystalMod.integration;

import alec_wam.CrystalMod.api.tools.IBatType;
import alec_wam.CrystalMod.integration.ModIntegration.IModIntegration;
import alec_wam.CrystalMod.integration.enderio.DarkSteelBatType;
import alec_wam.CrystalMod.items.tools.bat.BatHelper;

public class EnderIOIntegration implements IModIntegration {

	public static IBatType DARKSTEEL;
	
	@Override
	public String getModID() {
		return "EnderIO";
	}

	@Override
	public void preInit(){
		DARKSTEEL = BatHelper.registerBatType(new DarkSteelBatType());
	}
	
	@Override
	public void postInit(){
		//TODO Add when enderio updates
		//FarmersCommune.joinCommune(new MaterialCropFarmer(ModBlocks.materialCrop, 1, new ItemStack(ModItems.materialSeed)));
	}
	
}
