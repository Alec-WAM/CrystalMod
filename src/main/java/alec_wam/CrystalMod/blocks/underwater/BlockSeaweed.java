package alec_wam.CrystalMod.blocks.underwater;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.blocks.FakeBlockStateWithData;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.NormalBlockStateMapper;
import alec_wam.CrystalMod.items.ItemMiscFood.FoodType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.util.FluidUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSeaweed extends BlockBush implements ICustomModel {
	private static final AxisAlignedBB BOUNDING_BOX;

	static {
		final float size = 0.4F;
		BOUNDING_BOX = new AxisAlignedBB(0.5F - size, 0.0F, 0.5F - size, 0.5F + size, 0.8F, 0.5F + size);
	}

	public BlockSeaweed() {
		super(Material.WATER);
		setSoundType(SoundType.PLANT);
		setCreativeTab(CreativeTabs.DECORATIONS);
		setDefaultState(blockState.getBaseState().withProperty(BlockLiquid.LEVEL, 0));
        this.setTickRandomly(true);
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel(){
		ModelLoader.setCustomStateMapper(this, new NormalBlockStateMapper());
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
		ClientProxy.registerCustomModel(new ModelResourceLocation(getRegistryName(), "normal"), new ModelCrossedWater("plant/seaweed"));
	}
	
	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
    {
        return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
    }
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}	
	
	@Override
    public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
		return new FakeBlockStateWithData(state, world, pos);
    }
	
	@Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
    	java.util.List<ItemStack> dropped = Lists.newArrayList();
        int count = 2 + RANDOM.nextInt(3) + (fortune > 0 ? RANDOM.nextInt(fortune + 1) : 0);

        for (int k = 0; k < count; ++k)
        {
        	dropped.add(new ItemStack(ModItems.miscFood, 1, FoodType.SEAWEED.getMeta()));
        }
    	return dropped;
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
    public net.minecraftforge.common.EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos)
    {
		return ModBlocks.waterPlantType;
    }
	
	@Override
	protected boolean canSustainBush(IBlockState state)
    {
        return state.getMaterial() == Material.SAND;
    }
	
	@Override
	public boolean canBlockStay(final World worldIn, final BlockPos pos, final IBlockState state) {
		/*for(EnumFacing facing : EnumFacing.HORIZONTALS){
			BlockPos offset = pos.offset(facing);
			IBlockState otherState = worldIn.getBlockState(offset);
			if(FluidUtil.canWaterFlowInto(worldIn, offset, otherState)){
				return false;
			}
		}		*/
		IBlockState downState = worldIn.getBlockState(pos.down());
		return downState.getBlock().canSustainPlant(downState, worldIn, pos.down(), EnumFacing.UP, this);
	}

	@Override
	public boolean canPlaceBlockOnSide(final World worldIn, final BlockPos pos, final EnumFacing side) {
		return FluidUtil.isFluidSource(worldIn, pos, worldIn.getBlockState(pos)) && canBlockStay(worldIn, pos, this.getDefaultState());
	}

	@Override
	public void onBlockDestroyedByPlayer(final World worldIn, final BlockPos pos, final IBlockState state) {
		worldIn.setBlockState(pos, Blocks.WATER.getDefaultState(), worldIn.isRemote ? 11 : 3);
		worldIn.scheduleUpdate(pos, Blocks.WATER, Blocks.WATER.tickRate(worldIn));
	}

	@Override
	protected void checkAndDropBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.WATER.getDefaultState(), worldIn.isRemote ? 11 : 3);
			worldIn.scheduleUpdate(pos, Blocks.WATER, Blocks.WATER.tickRate(worldIn));
		}
	}
	
	//WaterStuff
	int adjacentSourceBlocks;
	
	@Override
	public int tickRate(World worldIn)
    {
		return 5;
    }
	
	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
       worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
	}    
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        int i = 0;
        int j = 1;

        if (this.blockMaterial == Material.LAVA && !worldIn.provider.doesWaterVaporize())
        {
            j = 2;
        }

        int k = this.tickRate(worldIn);

        if (i > 0)
        {
            int l = -100;
            this.adjacentSourceBlocks = 0;

            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
            {
                l = this.checkAdjacentBlock(worldIn, pos.offset(enumfacing), l);
            }

            int i1 = l + j;

            if (i1 >= 8 || l < 0)
            {
                i1 = -1;
            }

            int j1 = this.getDepth(worldIn.getBlockState(pos.up()));

            if (j1 >= 0)
            {
                if (j1 >= 8)
                {
                    i1 = j1;
                }
                else
                {
                    i1 = j1 + 8;
                }
            }

            if (this.adjacentSourceBlocks >= 2 && net.minecraftforge.event.ForgeEventFactory.canCreateFluidSource(worldIn, pos, state, this.blockMaterial == Material.WATER))
            {
                IBlockState iblockstate = worldIn.getBlockState(pos.down());

                if (iblockstate.getMaterial().isSolid())
                {
                    i1 = 0;
                }
                else if (iblockstate.getMaterial() == this.blockMaterial && ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() == 0)
                {
                    i1 = 0;
                }
            }

            if (this.blockMaterial == Material.LAVA && i < 8 && i1 < 8 && i1 > i && rand.nextInt(4) != 0)
            {
                k *= 4;
            }

            if (i1 == i)
            {
                //this.placeStaticBlock(worldIn, pos, state);
            }
            else
            {
                i = i1;

                if (i1 < 0)
                {
                    //worldIn.setBlockToAir(pos);
                }
                else
                {

                    worldIn.scheduleUpdate(pos, this, k);
                    worldIn.notifyNeighborsOfStateChange(pos, this, false);
                    /*state = state.withProperty(LEVEL, Integer.valueOf(i1));
                    worldIn.setBlockState(pos, state, 2);
                    worldIn.scheduleUpdate(pos, this, k);
                    worldIn.notifyNeighborsOfStateChange(pos, this, false);*/
                }
            }
        }
        else
        {
            //this.placeStaticBlock(worldIn, pos, state);
        }

        IBlockState iblockstate1 = worldIn.getBlockState(pos.down());

        if (FluidUtil.canWaterFlowInto(worldIn, pos.down(), iblockstate1))
        {
            if (this.blockMaterial == Material.LAVA && worldIn.getBlockState(pos.down()).getMaterial() == Material.WATER)
            {
                worldIn.setBlockState(pos.down(), Blocks.STONE.getDefaultState());
                //this.triggerMixEffects(worldIn, pos.down());
                return;
            }

            if (i >= 8)
            {
                this.tryFlowInto(worldIn, pos.down(), iblockstate1, i);
            }
            else
            {
                this.tryFlowInto(worldIn, pos.down(), iblockstate1, i + 8);
            }
        }
        else if (i >= 0 && (i == 0 || FluidUtil.isFluidBlocker(worldIn, pos.down(), iblockstate1)))
        {
            Set<EnumFacing> set = this.getPossibleFlowDirections(worldIn, pos);
            int k1 = i + j;

            if (i >= 8)
            {
                k1 = 1;
            }

            if (k1 >= 8)
            {
                return;
            }

            for (EnumFacing enumfacing1 : set)
            {
                this.tryFlowInto(worldIn, pos.offset(enumfacing1), worldIn.getBlockState(pos.offset(enumfacing1)), k1);
            }
        }
    }

    private void tryFlowInto(World worldIn, BlockPos pos, IBlockState state, int level)
    {
        if (FluidUtil.canWaterFlowInto(worldIn, pos, state))
        {
            if (state.getMaterial() != Material.AIR)
            {
                if (this.blockMaterial == Material.LAVA)
                {
                    //this.triggerMixEffects(worldIn, pos);
                }
                else
                {
                    if (state.getBlock() != Blocks.SNOW_LAYER) //Forge: Vanilla has a 'bug' where snowballs don't drop like every other block. So special case because ewww...
                    state.getBlock().dropBlockAsItem(worldIn, pos, state, 0);
                }
            }

            worldIn.setBlockState(pos, Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL, Integer.valueOf(level)), 3);
        }
    }

    private int getSlopeDistance(World worldIn, BlockPos pos, int distance, EnumFacing calculateFlowCost)
    {
        int i = 1000;

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
        {
            if (enumfacing != calculateFlowCost)
            {
                BlockPos blockpos = pos.offset(enumfacing);
                IBlockState iblockstate = worldIn.getBlockState(blockpos);

                if (!FluidUtil.isFluidBlocker(worldIn, blockpos, iblockstate) && (iblockstate.getMaterial() != this.blockMaterial || ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() > 0))
                {
                    if (!FluidUtil.isFluidBlocker(worldIn, blockpos.down(), iblockstate))
                    {
                        return distance;
                    }

                    if (distance < this.getSlopeFindDistance(worldIn))
                    {
                        int j = this.getSlopeDistance(worldIn, blockpos, distance + 1, enumfacing.getOpposite());

                        if (j < i)
                        {
                            i = j;
                        }
                    }
                }
            }
        }

        return i;
    }

    private int getSlopeFindDistance(World worldIn)
    {
        return this.blockMaterial == Material.LAVA && !worldIn.provider.doesWaterVaporize() ? 2 : 4;
    }

    private Set<EnumFacing> getPossibleFlowDirections(World worldIn, BlockPos pos)
    {
        int i = 1000;
        Set<EnumFacing> set = EnumSet.<EnumFacing>noneOf(EnumFacing.class);

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
        {
            BlockPos blockpos = pos.offset(enumfacing);
            IBlockState iblockstate = worldIn.getBlockState(blockpos);

            if (!FluidUtil.isFluidBlocker(worldIn, blockpos, iblockstate) && (iblockstate.getMaterial() != Material.WATER || ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() > 0))
            {
                int j;

                if (FluidUtil.isFluidBlocker(worldIn, blockpos.down(), worldIn.getBlockState(blockpos.down())))
                {
                    j = this.getSlopeDistance(worldIn, blockpos, 1, enumfacing.getOpposite());
                }
                else
                {
                    j = 0;
                }

                if (j < i)
                {
                    set.clear();
                }

                if (j <= i)
                {
                    set.add(enumfacing);
                    i = j;
                }
            }
        }

        return set;
    }
    

    protected int getDepth(IBlockState p_189542_1_)
    {
        return p_189542_1_.getMaterial() == this.blockMaterial ? ((Integer)p_189542_1_.getValue(BlockLiquid.LEVEL)).intValue() : -1;
    }
    protected int checkAdjacentBlock(World worldIn, BlockPos pos, int currentMinLevel)
    {
        int i = this.getDepth(worldIn.getBlockState(pos));

        if (i < 0)
        {
            return currentMinLevel;
        }
        else
        {
            if (i == 0)
            {
                ++this.adjacentSourceBlocks;
            }

            if (i >= 8)
            {
                i = 0;
            }

            return currentMinLevel >= 0 && i >= currentMinLevel ? currentMinLevel : i;
        }
    }
}
