package alec_wam.CrystalMod.tiles.pipes;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IPipeWrapper {

	public boolean isSender();

	public World getOtherWorld();
	
	public BlockPos getOtherPos();
	
}
