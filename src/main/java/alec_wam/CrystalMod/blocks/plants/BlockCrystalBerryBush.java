package alec_wam.CrystalMod.blocks.plants;

import java.util.Random;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import alec_wam.CrystalMod.init.ModItems;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockCrystalBerryBush extends BushBlock implements IGrowable {

	public static final IntegerProperty AGE = BlockStateProperties.AGE_0_3;
	protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
	protected final BlockVariantGroup<EnumCrystalColor, BlockCrystalBerryBush> variantGroup;
	protected final EnumCrystalColor type;
	public BlockCrystalBerryBush(EnumCrystalColor type, BlockVariantGroup<EnumCrystalColor, BlockCrystalBerryBush> variantGroup, Properties builder) {
		super(builder);
		this.type = type;
		this.variantGroup = variantGroup;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}
	
    @Override
    public void tick(BlockState state, World worldIn, BlockPos pos, Random random) 
    {
        int i = state.get(AGE).intValue();

        if (i < 3 && random.nextInt(4) == 0)
        {
            state = state.with(AGE, Integer.valueOf(i + 1));
            worldIn.setBlockState(pos, state, 2);
        }
    }
    
	@Override
	protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
		Block block = state.getBlock();
		return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) 
	{
		if((state.get(AGE)) < 3)return false;
    	
    	if(worldIn.isRemote){
    		@SuppressWarnings("deprecation")
			SoundType type = getSoundType(state);
        	worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), type.getPlaceSound(), SoundCategory.BLOCKS, (type.getVolume()) / 4.0F, type.getPitch() * 0.9F, true);
        	return true;
    	}
    	
    	Random rand = worldIn.rand;
    	int count = 1 + rand.nextInt(2);
    	ItemStack crop = new ItemStack(ModItems.crystalBerryGroup.getItem(type), 1);
    	worldIn.setBlockState(pos, state.with(AGE, 0));
    	
    	double x = pos.getX() + 0.5 + (ray.getFace().getXOffset() * 0.6);
    	double y = pos.getY() + 0.25 + (ray.getFace().getYOffset() * 0.6);
    	double z = pos.getZ() + 0.5 + (ray.getFace().getZOffset() * 0.6);
    	if(ItemStackTools.isValid(crop)){
    		for (int i = 0; i < count; i++)
    		{
    			ItemUtil.spawnItemInWorldWithoutMotion(new ItemEntity(worldIn, x, y, z, crop));
    		}
    	}
    	return true;
    }

	@Override
	public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
		return state.get(AGE) < 3;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return worldIn.rand.nextFloat() < 0.45D;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, BlockState state) {
		if(state.get(AGE) < 3){
			worldIn.setBlockState(pos, state.cycle(AGE), 4);
			BlockUtil.markBlockForUpdate(worldIn, pos);
		}
	}

	
}
