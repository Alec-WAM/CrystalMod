package alec_wam.CrystalMod.blocks.crystexium;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.blocks.BlockCustomStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCrystexStairs extends BlockCustomStairs {

	public BlockCrystexStairs(IBlockState modelState) {
		super(modelState);
	}

	@Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        if (net.minecraftforge.common.ForgeModContainer.disableStairSlabCulling)
            return super.doesSideBlockRendering(state, world, pos, face);

        if ( state.isOpaqueCube() )
            return true;

        return false;
    }
	
	@SuppressWarnings("deprecation")
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side)
	{
		IBlockState other = worldIn.getBlockState(pos.offset(side));
		if(other.getBlock() == this && getMetaFromState(state) == getMetaFromState(other)){
			return false;
		}
		return super.shouldSideBeRendered(state, worldIn, pos, side);
	}
	
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state){
    	return EnumBlockRenderType.MODEL;
    }
    
    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
      return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
      return false;
    }

}
