package alec_wam.CrystalMod.world;

import alec_wam.CrystalMod.blocks.crystexium.BlockCrystexPortal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PortalMaker
{
	public final World world;
    public final EnumFacing.Axis axis;
    public final EnumFacing rightDir;
    public final EnumFacing leftDir;
    public int portalBlockCount;
    public BlockPos bottomLeft;
    public int height;
    public int width;
    public IBlockState stateEdges, statePortal;

    public PortalMaker(World worldIn, BlockPos p_i45694_2_, EnumFacing.Axis p_i45694_3_, IBlockState stateEdges, IBlockState statePortal)
    {
        this.world = worldIn;
        this.axis = p_i45694_3_;
        
        this.stateEdges = stateEdges;
        this.statePortal = statePortal;

        if (p_i45694_3_ == EnumFacing.Axis.X)
        {
            this.leftDir = EnumFacing.EAST;
            this.rightDir = EnumFacing.WEST;
        }
        else
        {
            this.leftDir = EnumFacing.NORTH;
            this.rightDir = EnumFacing.SOUTH;
        }

        for (BlockPos blockpos = p_i45694_2_; p_i45694_2_.getY() > blockpos.getY() - 21 && p_i45694_2_.getY() > 0 && this.isEmptyBlock(worldIn.getBlockState(p_i45694_2_.down())); p_i45694_2_ = p_i45694_2_.down())
        {
            ;
        }

        int i = this.getDistanceUntilEdge(p_i45694_2_, this.leftDir) - 1;
        if (i >= 0)
        {
            this.bottomLeft = p_i45694_2_.offset(this.leftDir, i);
            this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);

            if (/*this.width < 2 || this.width > 21*/this.width !=5)
            {
                this.bottomLeft = null;
                this.width = 0;
            }
        }

        if (this.bottomLeft != null)
        {
            this.height = this.calculatePortalHeight();
        }
    }

    protected int getDistanceUntilEdge(BlockPos p_180120_1_, EnumFacing p_180120_2_)
    {
        int i;

        for (i = 0; i < 5/*22*/; ++i)
        {
            BlockPos blockpos = p_180120_1_.offset(p_180120_2_, i);

            if (!this.isEmptyBlock(this.world.getBlockState(blockpos)))
            {
            	break;
            }
            if(!this.world.getBlockState(blockpos.down()).equals(stateEdges)){
            	break;
            }
        }

        IBlockState state = this.world.getBlockState(p_180120_1_.offset(p_180120_2_, i));
        return state.equals(stateEdges) ? i : 0;
    }

    public int getHeight()
    {
        return this.height;
    }

    public int getWidth()
    {
        return this.width;
    }

    protected int calculatePortalHeight()
    {
        label24:

        for (this.height = 0; this.height < 5/*21*/; ++this.height)
        {
            for (int i = 0; i < this.width; ++i)
            {
                BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i).up(this.height);
                IBlockState state = this.world.getBlockState(blockpos);

                if (!this.isEmptyBlock(state))
                {
                    break label24;
                }

                if (state.equals(statePortal))
                {
                    ++this.portalBlockCount;
                }

                if (i == 0)
                {
                    state = this.world.getBlockState(blockpos.offset(this.leftDir));

                    if (!state.equals(stateEdges))
                    {
                        break label24;
                    }
                }
                else if (i == this.width - 1)
                {
                    state = this.world.getBlockState(blockpos.offset(this.rightDir));

                    if (!state.equals(stateEdges))
                    {
                        break label24;
                    }
                }
            }
        }

        for (int j = 0; j < this.width; ++j)
        {
            if (!this.world.getBlockState(this.bottomLeft.offset(this.rightDir, j).up(this.height)).equals(stateEdges))
            {
                this.height = 0;
                break;
            }
        }

        if (/*this.height <= 21 && this.height >= 3*/this.height == 5)
        {
            return this.height;
        }
        else
        {
            this.bottomLeft = null;
            this.width = 0;
            this.height = 0;
            return 0;
        }
    }

    public boolean isEmptyBlock(IBlockState blockIn)
    {
        return blockIn.getMaterial() == Material.AIR || blockIn == Blocks.FIRE || blockIn.equals(statePortal);
    }

    public boolean isValid()
    {
        return this.bottomLeft != null && this.width == 5 && this.height == 5/*this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21*/;
    }

    public void placePortalBlocks()
    {
        for (int i = 0; i < this.width; ++i)
        {
            BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i);

            for (int j = 0; j < this.height; ++j)
            {
                this.world.setBlockState(blockpos.up(j), statePortal.withProperty(BlockCrystexPortal.AXIS, this.axis), 2);
            }
        }
    }
}
