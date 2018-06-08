package alec_wam.CrystalMod.world;

import alec_wam.CrystalMod.world.crystex.CrystexWorldProvider;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class ModDimensions {

	public static int CUBE_ID = "PlayerCube".hashCode();
	public static int CRYSTEX_ID = "Crystex".hashCode();

	public static void register()
	{
		DimensionManager.registerDimension(CUBE_ID, DimensionType.register("PlayerCube", "PlayerCube_", CUBE_ID, PlayerCubeWorldProvider.class, true));
		DimensionManager.registerDimension(CRYSTEX_ID, DimensionType.register("Crystex", "Crystex_", CRYSTEX_ID, CrystexWorldProvider.class, true));
	}
	
}
