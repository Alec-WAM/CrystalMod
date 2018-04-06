package alec_wam.CrystalMod.entities.boatflume.rails;

import alec_wam.CrystalMod.blocks.FakeBlockStateWithData;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.entities.boatflume.BlockFlumeRailBase;
import alec_wam.CrystalMod.entities.boatflume.BlockFlumeRailBaseLand;
import alec_wam.CrystalMod.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFlumeRailAscending extends BlockFlumeRailBaseLand implements ICustomModel
{
	public static final BlockFlumeRailBase.EnumRailDirection[] ASCENDING_DIRS =  new EnumRailDirection[]{BlockFlumeRailBase.EnumRailDirection.ASCENDING_NORTH, BlockFlumeRailBase.EnumRailDirection.ASCENDING_SOUTH, BlockFlumeRailBase.EnumRailDirection.ASCENDING_WEST, BlockFlumeRailBase.EnumRailDirection.ASCENDING_EAST};
    public static final PropertyEnum<BlockFlumeRailBase.EnumRailDirection> CUSTOM_SHAPE = PropertyEnum.<BlockFlumeRailBase.EnumRailDirection>create("shape", BlockFlumeRailBase.EnumRailDirection.class, BlockFlumeRailBase.EnumRailDirection.ASCENDING_EAST, BlockFlumeRailBase.EnumRailDirection.ASCENDING_WEST, BlockFlumeRailBase.EnumRailDirection.ASCENDING_NORTH, BlockFlumeRailBase.EnumRailDirection.ASCENDING_SOUTH);


    public BlockFlumeRailAscending()
    {
        super(false);
        this.setDefaultState(this.blockState.getBaseState().withProperty(CUSTOM_SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_NORTH));
    }

    @SideOnly(Side.CLIENT)
    public final ModelFlumeRailRaisedGround RAISED_MODEL_INSTANCE = new ModelFlumeRailRaisedGround("rail_ramp");
    
    @Override
	public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
		return new FakeBlockStateWithData(state, world, pos);
    }
    
    @Override
	@SideOnly(Side.CLIENT)
	public void initModel(){
    	ModBlocks.initBasicModel(this);
    	for(EnumRailDirection dir : ASCENDING_DIRS){
			ResourceLocation baseLocation = getRegistryName();
			ModelResourceLocation inv = new ModelResourceLocation(baseLocation, "shape="+dir.getName());
			ClientProxy.registerCustomModel(inv, RAISED_MODEL_INSTANCE);
		}
	}

    public IProperty<BlockFlumeRailBase.EnumRailDirection> getShapeProperty()
    {
        return CUSTOM_SHAPE;
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(CUSTOM_SHAPE, ASCENDING_DIRS[meta % 4]);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state)
    {
        EnumRailDirection dir = ((BlockFlumeRailBase.EnumRailDirection)state.getValue(CUSTOM_SHAPE));
        return dir == EnumRailDirection.ASCENDING_SOUTH ? 1 : dir == EnumRailDirection.ASCENDING_WEST ? 2 : dir == EnumRailDirection.ASCENDING_EAST ? 3 : 0;
    }
    

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @SuppressWarnings("incomplete-switch")
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        switch (rot)
        {
            case CLOCKWISE_180:

                switch ((BlockFlumeRailBase.EnumRailDirection)state.getValue(CUSTOM_SHAPE))
                {
                    case ASCENDING_EAST:
                        return state.withProperty(CUSTOM_SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return state.withProperty(CUSTOM_SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                        return state.withProperty(CUSTOM_SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return state.withProperty(CUSTOM_SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_NORTH);
                }

            case COUNTERCLOCKWISE_90:

                switch ((BlockFlumeRailBase.EnumRailDirection)state.getValue(CUSTOM_SHAPE))
                {
                    case ASCENDING_EAST:
                        return state.withProperty(CUSTOM_SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_NORTH);
                    case ASCENDING_WEST:
                        return state.withProperty(CUSTOM_SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_SOUTH);
                    case ASCENDING_NORTH:
                        return state.withProperty(CUSTOM_SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_WEST);
                    case ASCENDING_SOUTH:
                        return state.withProperty(CUSTOM_SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_EAST);
                }

            case CLOCKWISE_90:

                switch ((BlockFlumeRailBase.EnumRailDirection)state.getValue(CUSTOM_SHAPE))
                {
                    case ASCENDING_EAST:
                        return state.withProperty(CUSTOM_SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_SOUTH);
                    case ASCENDING_WEST:
                        return state.withProperty(CUSTOM_SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_NORTH);
                    case ASCENDING_NORTH:
                        return state.withProperty(CUSTOM_SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_EAST);
                    case ASCENDING_SOUTH:
                        return state.withProperty(CUSTOM_SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_WEST);
                }

            default:
                return state;
        }
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @SuppressWarnings("incomplete-switch")
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        BlockFlumeRailBase.EnumRailDirection blockrailbase$enumraildirection = (BlockFlumeRailBase.EnumRailDirection)state.getValue(CUSTOM_SHAPE);

        switch (mirrorIn)
        {
            case LEFT_RIGHT:

                switch (blockrailbase$enumraildirection)
                {
                    case ASCENDING_NORTH:
                        return state.withProperty(CUSTOM_SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return state.withProperty(CUSTOM_SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_NORTH);
                    default:
                        return super.withMirror(state, mirrorIn);
                }

            case FRONT_BACK:

                switch (blockrailbase$enumraildirection)
                {
                    case ASCENDING_EAST:
                        return state.withProperty(CUSTOM_SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return state.withProperty(CUSTOM_SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                    case ASCENDING_SOUTH:
                    default:
                        break;
                }
        }

        return super.withMirror(state, mirrorIn);
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {CUSTOM_SHAPE});
    }
    
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote)
        {
            BlockFlumeRailBase.EnumRailDirection blockrailbase$enumraildirection = getRailDirection(worldIn, pos, worldIn.getBlockState(pos), null);
            boolean flag = false;

            if (!worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP))
            {
                flag = true;
            }

            if (flag && !worldIn.isAirBlock(pos))
            {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
            else
            {
                this.updateState(state, worldIn, pos, blockIn);
            }
        }
    }
    
    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        
    }
    
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
    	EnumRailDirection dir = EnumRailDirection.ASCENDING_NORTH;
    	EnumFacing facing = placer.getAdjustedHorizontalFacing();
    	if(facing == EnumFacing.SOUTH){
    		dir = EnumRailDirection.ASCENDING_SOUTH;
    	}
    	if(facing == EnumFacing.EAST){
    		dir = EnumRailDirection.ASCENDING_EAST;
    	}
    	if(facing == EnumFacing.WEST){
    		dir = EnumRailDirection.ASCENDING_WEST;
    	}
    	world.setBlockState(pos, state.withProperty(CUSTOM_SHAPE, dir), 3);
    }
}