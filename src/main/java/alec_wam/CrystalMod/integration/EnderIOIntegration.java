package alec_wam.CrystalMod.integration;

import alec_wam.CrystalMod.api.tools.IBatType;
import alec_wam.CrystalMod.integration.ModIntegration.IModIntegration;
import alec_wam.CrystalMod.integration.enderio.DarkSteelBatType;
import alec_wam.CrystalMod.items.tools.bat.BatHelper;
import alec_wam.CrystalMod.items.tools.bat.types.WoodBatType;

public class EnderIOIntegration implements IModIntegration {

	public static IBatType DARKSTEEL;
	
	@Override
	public String getModID() {
		return "EnderIO";
	}

	public void preInit(){
		DARKSTEEL = BatHelper.registerBatType(new DarkSteelBatType());
	}
	
}
