package alec_wam.CrystalMod.world;

import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class ModDimensions {

	public static int CUBE_ID = "PlayerCube".hashCode();

	public static void register()
	{
		DimensionManager.registerDimension(CUBE_ID, DimensionType.register("PlayerCube", "PlayerCube_", CUBE_ID, PlayerCubeWorldProvider.class, true));
	}
	
}
