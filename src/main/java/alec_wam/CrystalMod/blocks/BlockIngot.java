package alec_wam.CrystalMod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class BlockIngot extends Block {
	
	public BlockIngot(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isBeaconBase(IBlockState state, IWorldReader world, BlockPos pos, BlockPos beacon)
    {
		return true;
    }
	
}
