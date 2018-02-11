package alec_wam.CrystalMod.blocks.decorative;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.items.IEnumMetaItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
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

public class BlockBetterRoses extends BlockBush implements IGrowable, IShearable, ICustomModel
{
    public static final PropertyBool TOP = PropertyBool.create("top");
    public static final PropertyEnum<RoseType> COLOR = PropertyEnum.create("color", RoseType.class);
    public BlockBetterRoses()
    {
        super(Material.PLANTS);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setSoundType(SoundType.PLANT);
        this.setDefaultState(this.blockState.getBaseState().withProperty(COLOR, RoseType.WHITE).withProperty(TOP, false));
        disableStats();
    }
    
    @Override
	@SideOnly(Side.CLIENT)
	public void initModel(){
		ModelLoader.setCustomStateMapper(this, new CustomBlockStateMapper());
		for(RoseType type : RoseType.values()){
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(new ResourceLocation(getRegistryName()+"_"+type.getName()), "inventory"));
		}
	}
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return FULL_BLOCK_AABB;
    }
    
    @Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, NonNullList<ItemStack> list){
    	for(RoseType type : RoseType.values()){
			list.add(new ItemStack(this, 1, type.getMeta()));
		}
    }
    
    public static void placeFullPlant(World world, BlockPos pos, RoseType type){
    	world.setBlockState(pos.up(), ModBlocks.roseBush.getDefaultState().withProperty(COLOR, type).withProperty(TOP, true));
    	world.setBlockState(pos, ModBlocks.roseBush.getDefaultState().withProperty(COLOR, type));
    }
    
    @Override
	@Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    @Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
    	IBlockState currentState = worldIn.getBlockState(pos);
        IBlockState state = worldIn.getBlockState(pos.down());
        Block block = state.getBlock();
        if (block.canSustainPlant(state, worldIn, pos.down(), EnumFacing.UP, this)) return true;
        if(currentState.getBlock() == this && currentState.getValue(TOP) == true){
        	return block == this;
        }        
        return false;
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
    	return this.canPlaceBlockAt(worldIn, pos);
    }

    @Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return state.getValue(TOP) ? Items.AIR : Item.getItemFromBlock(this);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
    	if(state.getValue(TOP)){
    		if (worldIn.getBlockState(pos.down()).getBlock() == this)
            {
                if (player.capabilities.isCreativeMode)
                {
                    worldIn.setBlockToAir(pos.down());
                }
                else
                {
                    if (worldIn.isRemote)
                    {
                        worldIn.setBlockToAir(pos.down());
                    }
                    else
                    {
                        worldIn.destroyBlock(pos.down(), true);
                    }
                }
            }
    	}
    	super.onBlockHarvested(worldIn, pos, state, player);
    }
    
    @Override
	public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
	public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
	@SideOnly(Side.CLIENT)
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
    	RoseType type = state.getValue(COLOR);
    	return new ItemStack(this, 1, type.getMeta());
    }
    
    @Override
	public IBlockState getStateFromMeta(int meta)
    {
    	int top = meta & 1;
    	int type = meta >> 1;
    	return this.getDefaultState().withProperty(TOP, top == 1).withProperty(COLOR, RoseType.values()[type]);
    }

    @Override
	@SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
	public int getMetaFromState(IBlockState state)
    {
    	int top = state.getValue(TOP).booleanValue() ? 1 : 0;
    	int type = ((Integer)state.getValue(COLOR).getMeta()).intValue();
    	return (type << 1) | top;
    }
    
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }
    
    @Override
	protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {COLOR, TOP});
    }

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return Lists.newArrayList(new ItemStack(this, 1, item.getMetadata()));
	}
	
	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		spawnAsEntity(worldIn, pos, new ItemStack(this, 1, state.getValue(COLOR).getMeta()));
	}
    
    public enum RoseType implements IStringSerializable, IEnumMeta, IEnumMetaItem {
    	WHITE, ORANGE, MAGENTA, YELLOW, PINK, CYAN, PURPLE;

    	final int meta;
    	
    	RoseType(){
    		meta = ordinal();
    	}
    	
    	@Override
    	public int getMeta() {
    		return meta;
    	}

    	@Override
    	public String getName() {
    		return this.toString().toLowerCase(Locale.US);
    	}

    	@Override
    	public int getMetadata() {
    		return meta;
    	}
    	
    }
    
    public static class CustomBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			boolean top = state.getValue(TOP);
			RoseType type = state.getValue(COLOR);
			return new ModelResourceLocation(new ResourceLocation(state.getBlock().getRegistryName()+"_"+type.getName()), "top="+top);
		}
	}
}