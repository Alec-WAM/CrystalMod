package alec_wam.CrystalMod.entities.boatflume;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockFlumeRailBaseLand extends BlockFlumeRailBase {

	public BlockFlumeRailBaseLand(boolean isPowered) {
		super(isPowered);
		setSoundType(SoundType.STONE);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
	{
		IBlockState below = worldIn.getBlockState(pos.down());
		return below.isSideSolid(worldIn, pos.down(), EnumFacing.UP);
	}
	
	@Override
	public boolean canMakeSlopes(IBlockAccess world, BlockPos pos)
    {
        return true;
    }
	
}
