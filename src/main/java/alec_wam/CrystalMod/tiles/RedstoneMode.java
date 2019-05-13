package alec_wam.CrystalMod.tiles;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public enum RedstoneMode {
	NONE, IGNORE, OFF, ON;
	
	public boolean passes(World world, BlockPos pos){
		if(this == IGNORE){
			return true;
		}
		if(this == OFF){
			return !world.isBlockPowered(pos);
		}
		if(this == ON){
			return world.isBlockPowered(pos);
		}
		return false;
	}
}
