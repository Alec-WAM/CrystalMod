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
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFlumeRailBasicGround extends BlockFlumeRailBaseLand implements ICustomModel 
{
    public static final PropertyEnum<BlockFlumeRailBase.EnumRailDirection> SHAPE = PropertyEnum.<BlockFlumeRailBase.EnumRailDirection>create("shape", BlockFlumeRailBase.EnumRailDirection.class);

    public BlockFlumeRailBasicGround()
    {
        super(false);
        this.setDefaultState(this.blockState.getBaseState().withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.NORTH_SOUTH));
    }

    protected void updateState(IBlockState p_189541_1_, World p_189541_2_, BlockPos p_189541_3_, Block p_189541_4_)
    {
        if (p_189541_4_.getDefaultState().canProvidePower() && (new BlockFlumeRailBase.Rail(p_189541_2_, p_189541_3_, p_189541_1_)).countAdjacentRails() == 3)
        {
            this.updateDir(p_189541_2_, p_189541_3_, p_189541_1_, false);
        }
    }

    public IProperty<BlockFlumeRailBase.EnumRailDirection> getShapeProperty()
    {
        return SHAPE;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.byMetadata(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((BlockFlumeRailBase.EnumRailDirection)state.getValue(SHAPE)).getMetadata();
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

                switch ((BlockFlumeRailBase.EnumRailDirection)state.getValue(SHAPE))
                {
                    case ASCENDING_EAST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.NORTH_WEST);
                    case SOUTH_WEST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.NORTH_EAST);
                    case NORTH_WEST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.SOUTH_EAST);
                    case NORTH_EAST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.SOUTH_WEST);
                }

            case COUNTERCLOCKWISE_90:

                switch ((BlockFlumeRailBase.EnumRailDirection)state.getValue(SHAPE))
                {
                    case ASCENDING_EAST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_NORTH);
                    case ASCENDING_WEST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_SOUTH);
                    case ASCENDING_NORTH:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_WEST);
                    case ASCENDING_SOUTH:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_EAST);
                    case SOUTH_EAST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.NORTH_EAST);
                    case SOUTH_WEST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.SOUTH_EAST);
                    case NORTH_WEST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.SOUTH_WEST);
                    case NORTH_EAST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.NORTH_WEST);
                    case NORTH_SOUTH:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.EAST_WEST);
                    case EAST_WEST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.NORTH_SOUTH);
                }

            case CLOCKWISE_90:

                switch ((BlockFlumeRailBase.EnumRailDirection)state.getValue(SHAPE))
                {
                    case ASCENDING_EAST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_SOUTH);
                    case ASCENDING_WEST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_NORTH);
                    case ASCENDING_NORTH:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_EAST);
                    case ASCENDING_SOUTH:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_WEST);
                    case SOUTH_EAST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.SOUTH_WEST);
                    case SOUTH_WEST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.NORTH_WEST);
                    case NORTH_WEST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.NORTH_EAST);
                    case NORTH_EAST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.SOUTH_EAST);
                    case NORTH_SOUTH:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.EAST_WEST);
                    case EAST_WEST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.NORTH_SOUTH);
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
        BlockFlumeRailBase.EnumRailDirection BlockFlumeRailBase$enumraildirection = (BlockFlumeRailBase.EnumRailDirection)state.getValue(SHAPE);

        switch (mirrorIn)
        {
            case LEFT_RIGHT:

                switch (BlockFlumeRailBase$enumraildirection)
                {
                    case ASCENDING_NORTH:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.NORTH_EAST);
                    case SOUTH_WEST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.NORTH_WEST);
                    case NORTH_WEST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.SOUTH_WEST);
                    case NORTH_EAST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.SOUTH_EAST);
                    default:
                        return super.withMirror(state, mirrorIn);
                }

            case FRONT_BACK:

                switch (BlockFlumeRailBase$enumraildirection)
                {
                    case ASCENDING_EAST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                    case ASCENDING_SOUTH:
                    default:
                        break;
                    case SOUTH_EAST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.SOUTH_WEST);
                    case SOUTH_WEST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.SOUTH_EAST);
                    case NORTH_WEST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.NORTH_EAST);
                    case NORTH_EAST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.NORTH_WEST);
                }
        }

        return super.withMirror(state, mirrorIn);
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {SHAPE});
    }

    @SideOnly(Side.CLIENT)
    public final ModelFlumeRailRaisedGround RAISED_MODEL_INSTANCE = new ModelFlumeRailRaisedGround("rail_basic_ground");
    
    @Override
	public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
		if(state.getValue(SHAPE).isAscending()){
			return new FakeBlockStateWithData(state, world, pos);
		}
    	return super.getExtendedState(state, world, pos);
    }
    
    @Override
	@SideOnly(Side.CLIENT)
	public void initModel(){
    	ModBlocks.initBasicModel(this);
    	for(EnumRailDirection dir : new EnumRailDirection[]{BlockFlumeRailBase.EnumRailDirection.ASCENDING_NORTH, BlockFlumeRailBase.EnumRailDirection.ASCENDING_SOUTH, BlockFlumeRailBase.EnumRailDirection.ASCENDING_WEST, BlockFlumeRailBase.EnumRailDirection.ASCENDING_EAST}){
			ResourceLocation baseLocation = getRegistryName();
			ModelResourceLocation inv = new ModelResourceLocation(baseLocation, "shape="+dir.getName());
			ClientProxy.registerCustomModel(inv, RAISED_MODEL_INSTANCE);
		}
	}
    
    public static class CustomBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			StringBuilder builder = new StringBuilder();
			return new ModelResourceLocation(state.getBlock().getRegistryName(), builder.toString());
		}
	}
}