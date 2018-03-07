package alec_wam.CrystalMod.entities.boatflume.rails;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.entities.boatflume.BlockFlumeRailBase;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFlumeRailPowered extends BlockFlumeRailBase implements ICustomModel
{
    public static final PropertyEnum<BlockFlumeRailBase.EnumRailDirection> SHAPE = PropertyEnum.<BlockFlumeRailBase.EnumRailDirection>create("shape", BlockFlumeRailBase.EnumRailDirection.class, new Predicate<BlockFlumeRailBase.EnumRailDirection>()
    {
        public boolean apply(@Nullable BlockFlumeRailBase.EnumRailDirection p_apply_1_)
        {
            return p_apply_1_ != BlockFlumeRailBase.EnumRailDirection.NORTH_EAST && p_apply_1_ != BlockFlumeRailBase.EnumRailDirection.NORTH_WEST && p_apply_1_ != BlockFlumeRailBase.EnumRailDirection.SOUTH_EAST && p_apply_1_ != BlockFlumeRailBase.EnumRailDirection.SOUTH_WEST;
        }
    });
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    protected BlockFlumeRailPowered()
    {
        super(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.NORTH_SOUTH).withProperty(POWERED, Boolean.valueOf(false)));
    }

    @SideOnly(Side.CLIENT)
    public void initModel(){
    	ModBlocks.initBasicModel(this);
    }
    
    @SuppressWarnings("incomplete-switch")
    protected boolean findPoweredRailSignal(World worldIn, BlockPos pos, IBlockState state, boolean p_176566_4_, int p_176566_5_)
    {
        if (p_176566_5_ >= 8)
        {
            return false;
        }
        else
        {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            boolean flag = true;
            BlockFlumeRailBase.EnumRailDirection blockrailbase$enumraildirection = (BlockFlumeRailBase.EnumRailDirection)state.getValue(SHAPE);

            switch (blockrailbase$enumraildirection)
            {
                case NORTH_SOUTH:

                    if (p_176566_4_)
                    {
                        ++k;
                    }
                    else
                    {
                        --k;
                    }

                    break;
                case EAST_WEST:

                    if (p_176566_4_)
                    {
                        --i;
                    }
                    else
                    {
                        ++i;
                    }

                    break;
                case ASCENDING_EAST:

                    if (p_176566_4_)
                    {
                        --i;
                    }
                    else
                    {
                        ++i;
                        ++j;
                        flag = false;
                    }

                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.EAST_WEST;
                    break;
                case ASCENDING_WEST:

                    if (p_176566_4_)
                    {
                        --i;
                        ++j;
                        flag = false;
                    }
                    else
                    {
                        ++i;
                    }

                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.EAST_WEST;
                    break;
                case ASCENDING_NORTH:

                    if (p_176566_4_)
                    {
                        ++k;
                    }
                    else
                    {
                        --k;
                        ++j;
                        flag = false;
                    }

                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.NORTH_SOUTH;
                    break;
                case ASCENDING_SOUTH:

                    if (p_176566_4_)
                    {
                        ++k;
                        ++j;
                        flag = false;
                    }
                    else
                    {
                        --k;
                    }

                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            return this.isSameRailWithPower(worldIn, new BlockPos(i, j, k), p_176566_4_, p_176566_5_, blockrailbase$enumraildirection) ? true : flag && this.isSameRailWithPower(worldIn, new BlockPos(i, j - 1, k), p_176566_4_, p_176566_5_, blockrailbase$enumraildirection);
        }
    }

    protected boolean isSameRailWithPower(World worldIn, BlockPos pos, boolean p_176567_3_, int distance, BlockFlumeRailBase.EnumRailDirection p_176567_5_)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() != this)
        {
            return false;
        }
        else
        {
            BlockFlumeRailBase.EnumRailDirection blockrailbase$enumraildirection = (BlockFlumeRailBase.EnumRailDirection)iblockstate.getValue(SHAPE);
            return p_176567_5_ != BlockFlumeRailBase.EnumRailDirection.EAST_WEST || blockrailbase$enumraildirection != BlockFlumeRailBase.EnumRailDirection.NORTH_SOUTH && blockrailbase$enumraildirection != BlockFlumeRailBase.EnumRailDirection.ASCENDING_NORTH && blockrailbase$enumraildirection != BlockFlumeRailBase.EnumRailDirection.ASCENDING_SOUTH ? (p_176567_5_ != BlockFlumeRailBase.EnumRailDirection.NORTH_SOUTH || blockrailbase$enumraildirection != BlockFlumeRailBase.EnumRailDirection.EAST_WEST && blockrailbase$enumraildirection != BlockFlumeRailBase.EnumRailDirection.ASCENDING_EAST && blockrailbase$enumraildirection != BlockFlumeRailBase.EnumRailDirection.ASCENDING_WEST ? (((Boolean)iblockstate.getValue(POWERED)).booleanValue() ? (worldIn.isBlockPowered(pos) ? true : this.findPoweredRailSignal(worldIn, pos, iblockstate, p_176567_3_, distance + 1)) : false) : false) : false;
        }
    }

    protected void updateState(IBlockState p_189541_1_, World p_189541_2_, BlockPos p_189541_3_, Block p_189541_4_)
    {
        boolean flag = ((Boolean)p_189541_1_.getValue(POWERED)).booleanValue();
        boolean flag1 = p_189541_2_.isBlockPowered(p_189541_3_) || this.findPoweredRailSignal(p_189541_2_, p_189541_3_, p_189541_1_, true, 0) || this.findPoweredRailSignal(p_189541_2_, p_189541_3_, p_189541_1_, false, 0);

        if (flag1 != flag)
        {
            p_189541_2_.setBlockState(p_189541_3_, p_189541_1_.withProperty(POWERED, Boolean.valueOf(flag1)), 3);
            p_189541_2_.notifyNeighborsOfStateChange(p_189541_3_.down(), this, false);

            if (((BlockFlumeRailBase.EnumRailDirection)p_189541_1_.getValue(SHAPE)).isAscending())
            {
                p_189541_2_.notifyNeighborsOfStateChange(p_189541_3_.up(), this, false);
            }
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
        return this.getDefaultState().withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.byMetadata(meta & 7)).withProperty(POWERED, Boolean.valueOf((meta & 8) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | ((BlockFlumeRailBase.EnumRailDirection)state.getValue(SHAPE)).getMetadata();

        if (((Boolean)state.getValue(POWERED)).booleanValue())
        {
            i |= 8;
        }

        return i;
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
                    case NORTH_SOUTH:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.EAST_WEST);
                    case EAST_WEST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.NORTH_SOUTH);
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
                }

            case CLOCKWISE_90:

                switch ((BlockFlumeRailBase.EnumRailDirection)state.getValue(SHAPE))
                {
                    case NORTH_SOUTH:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.EAST_WEST);
                    case EAST_WEST:
                        return state.withProperty(SHAPE, BlockFlumeRailBase.EnumRailDirection.NORTH_SOUTH);
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
        BlockFlumeRailBase.EnumRailDirection blockrailbase$enumraildirection = (BlockFlumeRailBase.EnumRailDirection)state.getValue(SHAPE);

        switch (mirrorIn)
        {
            case LEFT_RIGHT:

                switch (blockrailbase$enumraildirection)
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

                switch (blockrailbase$enumraildirection)
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
        return new BlockStateContainer(this, new IProperty[] {SHAPE, POWERED});
    }
}