package alec_wam.CrystalMod.blocks.crystexium;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.crystex.ItemCrystex.CrystexItemType;
import net.minecraft.block.Block;
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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCrystheriumPlant extends Block implements net.minecraftforge.common.IPlantable, ICustomModel
{
    public static final PropertyBool TOP = PropertyBool.create("top");
    public static final PropertyEnum<CrystheriumType> TYPE = PropertyEnum.create("type", CrystheriumType.class);
	protected static final AxisAlignedBB REED_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);
    public BlockCrystheriumPlant()
    {
        super(Material.PLANTS);
        this.setCreativeTab(CrystalMod.tabCrops);
        this.setSoundType(SoundType.PLANT);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, CrystheriumType.NORMAL).withProperty(TOP, false));
        disableStats();
    }
    
    @Override
	@SideOnly(Side.CLIENT)
	public void initModel(){
		ModelLoader.setCustomStateMapper(this, new CustomBlockStateMapper());
		for(CrystheriumType type : CrystheriumType.values()){
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(new ResourceLocation(getRegistryName()+"_"+type.getName()), "inventory"));
		}
	}
    
    @Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, NonNullList<ItemStack> list){
    	for(CrystheriumType type : CrystheriumType.values()){
			list.add(new ItemStack(this, 1, type.getMeta()));
		}
    }
    
    public static void placeFullPlant(World world, BlockPos pos, CrystheriumType type){
    	world.setBlockState(pos.up(), ModBlocks.crystheriumPlant.getDefaultState().withProperty(TYPE, type).withProperty(TOP, true));
    	world.setBlockState(pos, ModBlocks.crystheriumPlant.getDefaultState().withProperty(TYPE, type));
    }
    
    @Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return REED_AABB;
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
        //Allow only dirt for plains generation
        if(block == Blocks.DIRT){
        	return true;
        }
        if(currentState.getBlock() == this && currentState.getValue(TOP) == true){
        	return block == this;
        }        
        return false;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        this.checkForDrop(worldIn, pos, state);
    }

    protected final boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state)
    {
        if (this.canBlockStay(worldIn, pos))
        {
            return true;
        }
        else
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
            return false;
        }
    }

    public boolean canBlockStay(World worldIn, BlockPos pos)
    {
    	return this.canPlaceBlockAt(worldIn, pos);
    }

    @Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return state.getValue(TOP) ? Items.AIR : ModItems.crystexItems;
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
    	CrystheriumType type = state.getValue(TYPE);
    	return new ItemStack(this, 1, type.getMeta());
    }
    
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
    	boolean top = state.getValue(TOP);
    	CrystheriumType type = state.getValue(TYPE);
    	java.util.List<ItemStack> dropped = Lists.newArrayList();
        if(top) return Lists.newArrayList();
        int count = 2 + RANDOM.nextInt(3) + (fortune > 0 ? RANDOM.nextInt(fortune + 1) : 0);

        for (int k = 0; k < count; ++k)
        {
        	dropped.add(new ItemStack(ModItems.crystexItems, 1, CrystexItemType.CRYSTHERIUM_UTILIA_NORMAL.getMeta() + type.getMeta()));
        }
    	return dropped;
    }
    
    @Override
	public IBlockState getStateFromMeta(int meta)
    {
    	int top = meta & 1;
    	int type = meta >> 1;
    	return this.getDefaultState().withProperty(TOP, top == 1).withProperty(TYPE, CrystheriumType.values()[type]);
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
    	int type = ((Integer)state.getValue(TYPE).getMeta()).intValue();
    	return (type << 1) | top;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state;
    }
    
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }
    
    @Override
	protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {TYPE, TOP});
    }

    @Override
    public net.minecraftforge.common.EnumPlantType getPlantType(IBlockAccess world, BlockPos pos)
    {
        return net.minecraftforge.common.EnumPlantType.Plains;
    }
    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos)
    {
        return getDefaultState().withProperty(TOP, false);
    }
    
    public static class CustomBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			boolean top = state.getValue(TOP);
			CrystheriumType type = state.getValue(TYPE);
			return new ModelResourceLocation(new ResourceLocation(state.getBlock().getRegistryName()+"_"+type.getName()), "top="+top);
		}
	}
}