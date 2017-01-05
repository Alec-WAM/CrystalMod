package alec_wam.CrystalMod.integration;

import alec_wam.CrystalMod.api.tools.IBatType;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.integration.ModIntegration.IModIntegration;
import alec_wam.CrystalMod.integration.enderio.DarkSteelBatType;
import alec_wam.CrystalMod.integration.enderio.MaterialCropFarmer;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.bat.BatHelper;
import crazypants.enderio.machine.farm.farmers.FarmersCommune;
import net.minecraft.item.ItemStack;

public class EnderIOIntegration implements IModIntegration {

	public static IBatType DARKSTEEL;
	
	@Override
	public String getModID() {
		return "EnderIO";
	}

	public void preInit(){
		DARKSTEEL = BatHelper.registerBatType(new DarkSteelBatType());
	}
	
	public void postInit(){
		FarmersCommune.joinCommune(new MaterialCropFarmer(ModBlocks.materialCrop, 1, new ItemStack(ModItems.materialSeed)));
	}
	
}
