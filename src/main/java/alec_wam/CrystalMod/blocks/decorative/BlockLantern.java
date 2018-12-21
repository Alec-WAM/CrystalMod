package alec_wam.CrystalMod.blocks.decorative;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockWall;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLantern extends Block {

    protected static final AxisAlignedBB AABB = new AxisAlignedBB(3.0D * (1.0D / 16.0D), 0.0D, 3.0D * (1.0D / 16.0D), 13.0D * (1.0D / 16.0D), 13.0D * (1.0D / 16.0D), 13.0D * (1.0D / 16.0D));
	public BlockLantern() {
		super(Material.GLASS);
		setCreativeTab(CrystalMod.tabItems);
		setLightLevel(1.0F);
		setHardness(0.1F);
		setSoundType(SoundType.GLASS);
	}
	
	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
		IBlockState state = worldIn.getBlockState(pos.down());
		return state.isSideSolid(worldIn, pos, EnumFacing.UP) || state.getBlock() instanceof BlockWall || state.getBlock() instanceof BlockFence;
    }
	
	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
		if (!canPlaceBlockAt(worldIn, pos))
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
		if (!canPlaceBlockAt(worldIn, pos))
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return AABB;
    }
	
	@SideOnly(Side.CLIENT)
    @Override
	public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }	
	
	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state)
    {
        return false;
    }

	@Override
	public boolean isFullBlock(IBlockState state)
    {
        return false;
    }

}
