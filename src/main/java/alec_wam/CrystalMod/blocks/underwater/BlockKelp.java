package alec_wam.CrystalMod.blocks.underwater;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.items.ItemMiscFood.FoodType;
import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockKelp extends BlockBush implements ICustomModel, IShearable {
	private static final AxisAlignedBB BOUNDING_BOX;
	public static PropertyBool ISYELLOW = PropertyBool.create("yellow");
	static {
		final float size = 0.4F;
		BOUNDING_BOX = new AxisAlignedBB(0.5F - size, 0.0F, 0.5F - size, 0.5F + size, 0.8F, 0.5F + size);
	}

	public BlockKelp() {
		super(Material.WATER);
		setSoundType(SoundType.PLANT);
		setCreativeTab(CreativeTabs.DECORATIONS);
		setDefaultState(blockState.getBaseState().withProperty(BlockLiquid.LEVEL, 0).withProperty(ISYELLOW, false));
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel(){
		ModelLoader.setCustomStateMapper(this, new CustomBlockStateMapper());
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName()+"_green", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 1, new ModelResourceLocation(getRegistryName()+"_yellow", "inventory"));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, NonNullList<ItemStack> list){
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
		return BOUNDING_BOX;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{BlockLiquid.LEVEL, ISYELLOW});
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(ISYELLOW, meta == 1);
	}
	
	@Override
	public int getMetaFromState(final IBlockState state) {
		return state.getValue(ISYELLOW) ? 1 : 0;
	}

	@Override
    public net.minecraftforge.common.EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos)
    {
		return ModBlocks.waterPlantType;
    }
	
	@Override
	protected boolean canSustainBush(IBlockState state)
    {
        return state.getMaterial() == Material.SAND || (state.getBlock() == this);
    }
	
	@Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
    	java.util.List<ItemStack> dropped = Lists.newArrayList();
        int count = 2 + RANDOM.nextInt(3) + (fortune > 0 ? RANDOM.nextInt(fortune + 1) : 0);

        for (int k = 0; k < count; ++k)
        {
        	dropped.add(new ItemStack(ModItems.miscFood, 1, state.getValue(ISYELLOW) ? FoodType.YELLOW_KELP.getMeta() : FoodType.KELP.getMeta()));
        }
    	return dropped;
    }
	
	@Override
	public boolean canBlockStay(final World worldIn, final BlockPos pos, final IBlockState state) {
		if(worldIn.getBlockState(pos.up()).getBlock() == Blocks.WATER || worldIn.getBlockState(pos.up()).getBlock() == this || worldIn.getBlockState(pos.up()).getBlock() == Blocks.FLOWING_WATER){
			IBlockState below = worldIn.getBlockState(pos.down());
			return below.getBlock().canSustainPlant(below, worldIn, pos.down(), EnumFacing.UP, this);
		}
		return false;
	}

	@Override
	public boolean canPlaceBlockOnSide(final World worldIn, final BlockPos pos, final EnumFacing side) {
		return canBlockStay(worldIn, pos, getDefaultState());
	}

	@Override
	public void onBlockDestroyedByPlayer(final World worldIn, final BlockPos pos, final IBlockState state) {
		worldIn.setBlockState(pos, Blocks.WATER.getDefaultState());
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
		this.onBlockHarvested(world, pos, state, player);
        return world.setBlockState(pos, Blocks.WATER.getDefaultState(), world.isRemote ? 11 : 3);
    }
	
	@Override
	protected void checkAndDropBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.WATER.getDefaultState());
		}
	}
	
	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
		return super.canPlaceBlockAt(worldIn, pos) && worldIn.getBlockState(pos).getBlock() !=this;
    }

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		IBlockState state = world.getBlockState(pos);
		return Lists.newArrayList(new ItemStack(this, 1, getMetaFromState(state)));
	}
	
	public static class CustomBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			boolean yellow = state.getValue(ISYELLOW);
			return new ModelResourceLocation(new ResourceLocation(state.getBlock().getRegistryName()+"_"+(yellow ? "yellow" : "green")), "normal");
		}
	}
}
