package alec_wam.CrystalMod.blocks.plants;

import java.util.Random;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import alec_wam.CrystalMod.init.ModItems;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockCrystalBerryBush extends BlockBush implements IGrowable {

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
	public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
		return SHAPE;
	}
	
    @Override
    public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) 
    {
        int i = state.get(AGE).intValue();

        if (i < 3 && random.nextInt(4) == 0)
        {
            state = state.with(AGE, Integer.valueOf(i + 1));
            worldIn.setBlockState(pos, state, 2);
        }
    }

    @SuppressWarnings("deprecation")
	@Override
    public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
    	super.dropBlockAsItemWithChance(state, worldIn, pos, chancePerItem, fortune);
    }
    
	@Override
	protected boolean isValidGround(IBlockState state, IBlockReader worldIn, BlockPos pos) {
		Block block = state.getBlock();
		return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL;
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		builder.add(AGE);
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) 
	{
		if((state.get(AGE)) < 3)return false;
    	
    	if(worldIn.isRemote){
    		@SuppressWarnings("deprecation")
			SoundType type = getSoundType();
        	worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), type.getPlaceSound(), SoundCategory.BLOCKS, (type.getVolume()) / 4.0F, type.getPitch() * 0.9F, true);
        	return true;
    	}
    	
    	Random rand = worldIn.rand;
    	int fortune = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FORTUNE, player);
    	int count = 1 + rand.nextInt(2) + (fortune > 0 ? rand.nextInt(fortune + 1) : 0);
    	ItemStack crop = new ItemStack(ModItems.crystalBerryGroup.getItem(type), 1);
    	worldIn.setBlockState(pos, state.with(AGE, 0));
    	
    	double x = pos.getX() + 0.5 + (side.getXOffset() * 0.6);
    	double y = pos.getY() + 0.25 + (side.getYOffset() * 0.6);
    	double z = pos.getZ() + 0.5 + (side.getZOffset() * 0.6);
    	if(ItemStackTools.isValid(crop)){
    		for (int i = 0; i < count; i++)
    		{
    			ItemUtil.spawnItemInWorldWithoutMotion(new EntityItem(worldIn, x, y, z, crop));
    		}
    	}
    	return true;
    }
    
    @Override
    public void getDrops(IBlockState state, NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune)
    {
    	super.getDrops(state, drops, world, pos, fortune);
        Random rand = world.rand;
        int count = 0;

        if ((state.get(AGE)) >= 3)
        {
            count = 1 + rand.nextInt(2) + (fortune > 0 ? rand.nextInt(fortune + 1) : 0);
        }

        ItemStack crop = new ItemStack(ModItems.crystalBerryGroup.getItem(type), 1);
        if(ItemStackTools.isValid(crop)){
	        for (int i = 0; i < count; i++)
	        {
	        	drops.add(crop);
	        }
        }
    }

	@Override
	public boolean canGrow(IBlockReader worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		return state.get(AGE) < 3;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return worldIn.rand.nextFloat() < 0.45D;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		if(state.get(AGE) < 3){
			worldIn.setBlockState(pos, state.cycle(AGE), 4);
			BlockUtil.markBlockForUpdate(worldIn, pos);
		}
	}

	
}
