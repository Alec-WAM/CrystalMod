package alec_wam.CrystalMod.blocks;

import java.util.Random;

import alec_wam.CrystalMod.ModConfig;
import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.init.ModItems;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockCrystalShard extends Block {
	public static final IntegerProperty SHARDS_1_3 = IntegerProperty.create("shards", 1, 3);
	private static final VoxelShape SHAPE_SINGLE = Block.makeCuboidShape(6.25D, 0.0D, 6.25D, 9.75D, 11.5D, 9.75D);
	private static final VoxelShape SHAPE_MULTIPLE = Block.makeCuboidShape(4.25D, 0.0D, 4.25D, 11.75D, 11.5D, 11.75D);
	protected final BlockVariantGroup<EnumCrystalColor, BlockCrystalShard> variantGroup;
	protected final EnumCrystalColor type;
	
	public BlockCrystalShard(EnumCrystalColor type, BlockVariantGroup<EnumCrystalColor, BlockCrystalShard> variantGroup, Properties properties) {
		super(properties);
		this.type = type;
		this.variantGroup = variantGroup;
		this.setDefaultState(this.stateContainer.getBaseState().with(SHARDS_1_3, Integer.valueOf(1)));
	}
	//TODO Make Drop when not on valid block
	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
		int shards = state.get(SHARDS_1_3);
		if(shards > 1){
			return SHAPE_MULTIPLE;
		}
		return SHAPE_SINGLE;
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		builder.add(SHARDS_1_3);
	}	
	
	@Override
	public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
		int shards = state.get(SHARDS_1_3);
		int growthChance = ModConfig.BLOCKS.Shard_Block_Growth.get();
		if (shards < 3 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, worldIn.rand.nextInt(growthChance) == 0)) {
			worldIn.setBlockState(pos, state.with(SHARDS_1_3, Integer.valueOf(shards + 1)), 2);
			net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
		}
	}
	
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.SOLID;
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
		super.dropBlockAsItemWithChance(state, worldIn, pos, chancePerItem, fortune);
	}

	@Override
	public void getDrops(IBlockState state, net.minecraft.util.NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune) {
		int shards = state.get(SHARDS_1_3);
		int dropCount = 1;
		if (shards == 2) {
			dropCount = 2;
		}
		if (shards == 3) {
			dropCount = 3;
			if(world.rand.nextInt(5  - fortune) == 0){
				dropCount++;
			}
		}

		for(int k = 0; k < dropCount; ++k) {
			drops.add(new ItemStack(ModItems.crystalShardGroup.getItem(EnumCrystalColorSpecial.convert(type))));
		}
	}

	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(ModItems.crystalShardGroup.getItem(EnumCrystalColorSpecial.convert(type)));
	}
	
	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		int shards = state.get(SHARDS_1_3).intValue();
		if(shards < 3 && player.abilities.isCreativeMode){
			if(ItemStackTools.isValid(stack)){
				if(stack.getItem() == ModItems.crystalShardGroup.getItem(EnumCrystalColorSpecial.convert(type))){
					IBlockState newState = state.with(SHARDS_1_3, shards + 1);
					world.setBlockState(pos, newState);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state)
    {
        return false;
    }
	
	@SuppressWarnings("deprecation")
	@Override
	public int getOpacity(IBlockState state, IBlockReader worldIn, BlockPos pos) {
		return super.getOpacity(state, worldIn, pos);
	}
	
	@Override
	public boolean isSolid(IBlockState state) {
		return false;
	}

}
