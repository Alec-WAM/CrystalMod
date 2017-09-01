package alec_wam.CrystalMod.blocks.crops;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.crops.ItemCorn.CornItemType;
import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCorn extends Block implements net.minecraftforge.common.IPlantable, ICustomModel, IGrowable
{
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 4);
    public static final PropertyBool TOP = PropertyBool.create("top");
    protected static final AxisAlignedBB REED_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);
    public BlockCorn()
    {
        super(Material.PLANTS);
        this.setSoundType(SoundType.PLANT);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
        this.setTickRandomly(true);
        disableStats();
    }
    
    @Override
	@SideOnly(Side.CLIENT)
	public void initModel(){
		ModelLoader.setCustomStateMapper(this, new CustomBlockStateMapper());
		ModBlocks.initBasicModel(this);
	}
    
    public static void placeGrownCorn(World world, BlockPos pos, int age){
    	if(age >= 2){
    		world.setBlockState(pos.up(), ModBlocks.corn.getDefaultState().withProperty(AGE, age).withProperty(TOP, true));
    	}
    	world.setBlockState(pos, ModBlocks.corn.getDefaultState().withProperty(AGE, age));
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
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if(worldIn.isAirBlock(pos.up())){
        	boolean bottom = state.getValue(TOP) == false;
        	if(bottom){
        		int age = state.getValue(AGE).intValue();
        		if(age < 4){
        			int newAge = age+1;
        			if(newAge >= 2){
        				worldIn.setBlockState(pos.up(), state.withProperty(AGE, Integer.valueOf(newAge)).withProperty(TOP, true), 4);
        			}
                    worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(newAge)), 4);
        		}
        	}
        }
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

    /**
     * Called when a neighboring block changes.
     */
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

    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return state.getValue(TOP) ? Items.AIR : ModItems.corn;
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
    
    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
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
    	return new ItemStack(ModItems.corn, 1, CornItemType.CORN.getMetadata());
    }
    
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
    	int age = state.getValue(AGE);
    	boolean top = state.getValue(TOP);
    	java.util.List<ItemStack> dropped = Lists.newArrayList();
        if(top) return Lists.newArrayList();
        if (age == 4)
        {
            int count = 2 + RANDOM.nextInt(3) + (fortune > 0 ? RANDOM.nextInt(fortune + 1) : 0);

            for (int k = 0; k < count; ++k)
            {
                dropped.add(new ItemStack(ModItems.corn, 1, CornItemType.CORN.getMetadata()));
            }
        } else {
        	dropped.add(new ItemStack(ModItems.corn, 1, CornItemType.KERNELS.getMetadata()));
        }
    	return dropped;
    }
    
    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
	public IBlockState getStateFromMeta(int meta)
    {
    	int top = meta & 0x2F;
    	int age = (meta >> 2) & 1;
    	return this.getDefaultState().withProperty(TOP, top == 1).withProperty(AGE, age);
    }

    @Override
	@SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
	public int getMetaFromState(IBlockState state)
    {
    	int top = (state.getValue(TOP).booleanValue() ? 1 : 0);
    	int age = ((Integer)state.getValue(AGE)).intValue();
    	return (top << 2) | age;
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
        return new BlockStateContainer(this, new IProperty[] {AGE, TOP});
    }

    @Override
    public net.minecraftforge.common.EnumPlantType getPlantType(IBlockAccess world, BlockPos pos)
    {
        return net.minecraftforge.common.EnumPlantType.Crop;
    }
    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos)
    {
        return getDefaultState();
    }
    
    public static class CustomBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			int age = state.getValue(AGE);
			boolean top = state.getValue(TOP);
			ResourceLocation baseLocation = new ResourceLocation(state.getBlock().getRegistryName()+"_"+(top ? "top" : "bottom"));			
			return new ModelResourceLocation(baseLocation, "age="+age);
		}
	}

	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		return state.getValue(AGE) < 4;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		boolean top = state.getValue(TOP).booleanValue();
		if(!top){
			int age = state.getValue(AGE).intValue();
			if(age < 4){
				int newAge = age+1;
				if(newAge >= 2){
					worldIn.setBlockState(pos.up(), this.getDefaultState().withProperty(AGE, Integer.valueOf(newAge)).withProperty(TOP, true));
				}
				worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(newAge)), 4);
			}
		} else {
			IBlockState belowState = worldIn.getBlockState(pos.down());
			int age = belowState.getValue(AGE).intValue();
			if(age < 4){
				int newAge = age+1;
				worldIn.setBlockState(pos.down(), belowState.withProperty(AGE, Integer.valueOf(newAge)));
				worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(newAge)), 4);
			}
		}
	}
}