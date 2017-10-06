package alec_wam.CrystalMod.blocks.underwater;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.NormalBlockStateMapper;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockKelp extends BlockBush implements ICustomModel {
	private static final AxisAlignedBB BOUNDING_BOX;

	static {
		final float size = 0.4F;
		BOUNDING_BOX = new AxisAlignedBB(0.5F - size, 0.0F, 0.5F - size, 0.5F + size, 0.8F, 0.5F + size);
	}

	public BlockKelp() {
		super(Material.WATER);
		setCreativeTab(CrystalMod.tabCrops);
		setDefaultState(blockState.getBaseState().withProperty(BlockLiquid.LEVEL, 0));
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel(){
		ModelLoader.setCustomStateMapper(this, new NormalBlockStateMapper());
		ModBlocks.initBasicModel(this);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
		return BOUNDING_BOX;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BlockLiquid.LEVEL);
	}

	@Override
	public int getMetaFromState(final IBlockState state) {
		return 0;
	}

	@Override
	protected boolean canSustainBush(IBlockState state)
    {
        return state.getMaterial() == Material.SAND;
    }
	
	@Override
	public boolean canBlockStay(final World worldIn, final BlockPos pos, final IBlockState state) {
		return worldIn.getBlockState(pos.up()).getBlock() == Blocks.WATER && worldIn.getBlockState(pos.down()).getMaterial() == Material.SAND;
	}

	@Override
	public boolean canPlaceBlockOnSide(final World worldIn, final BlockPos pos, final EnumFacing side) {
		return canBlockStay(worldIn, pos, this.getDefaultState());
	}

	@Override
	public void onBlockDestroyedByPlayer(final World worldIn, final BlockPos pos, final IBlockState state) {
		worldIn.setBlockState(pos, Blocks.WATER.getDefaultState());
	}

	@Override
	protected void checkAndDropBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.WATER.getDefaultState());
		}
	}
}
