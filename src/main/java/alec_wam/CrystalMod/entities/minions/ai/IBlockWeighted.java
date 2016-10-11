package alec_wam.CrystalMod.entities.minions.ai;

import net.minecraft.util.math.BlockPos;

public interface IBlockWeighted {

	public float getBlockPathWeight(BlockPos pos);
}
