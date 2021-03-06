package alec_wam.CrystalMod.entities.boatflume;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.entities.boatflume.rails.BlockFlumeRailAscending;
import alec_wam.CrystalMod.util.FluidUtil;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockFlumeRailBase extends Block
{
    protected static final AxisAlignedBB FLAT_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
    protected static final AxisAlignedBB ASCENDING_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    protected final boolean isPowered;

    public static boolean isRailBlock(World worldIn, BlockPos pos)
    {
        return isRailBlock(worldIn.getBlockState(pos));
    }

    public static boolean isRailBlock(IBlockState state)
    {
        Block block = state.getBlock();
        return block instanceof BlockFlumeRailBase;
    }

    protected BlockFlumeRailBase(boolean isPowered)
    {
        super(Material.CIRCUITS);
        this.isPowered = isPowered;
		this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(CreativeTabs.TRANSPORTATION);
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        BlockFlumeRailBase.EnumRailDirection blockrailbase$enumraildirection = state.getBlock() == this ? getRailDirection(source, pos, state, null) : null;
        return blockrailbase$enumraildirection != null && blockrailbase$enumraildirection.isAscending() ? ASCENDING_AABB : FLAT_AABB;
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
    	IBlockState below = worldIn.getBlockState(pos.down());
    	return FluidUtil.isFluidSource(worldIn, pos, below);
    }

    /**
     * Called after the block is set in the Chunk data, but before the Tile Entity is set
     */
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            state = this.updateDir(worldIn, pos, state, true);

            if (this.isPowered)
            {
                state.neighborChanged(worldIn, pos, this, pos);
            }
        }
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote)
        {
            BlockFlumeRailBase.EnumRailDirection blockrailbase$enumraildirection = getRailDirection(worldIn, pos, worldIn.getBlockState(pos), null);
            boolean flag = false;

            if (!canPlaceBlockAt(worldIn, pos))
            {
                flag = true;
            }

            if (blockrailbase$enumraildirection == BlockFlumeRailBase.EnumRailDirection.ASCENDING_EAST && !worldIn.getBlockState(pos.east()).isSideSolid(worldIn, pos.east(), EnumFacing.UP))
            {
                flag = true;
            }
            else if (blockrailbase$enumraildirection == BlockFlumeRailBase.EnumRailDirection.ASCENDING_WEST && !worldIn.getBlockState(pos.west()).isSideSolid(worldIn, pos.west(), EnumFacing.UP))
            {
                flag = true;
            }
            else if (blockrailbase$enumraildirection == BlockFlumeRailBase.EnumRailDirection.ASCENDING_NORTH && !worldIn.getBlockState(pos.north()).isSideSolid(worldIn, pos.north(), EnumFacing.UP))
            {
                flag = true;
            }
            else if (blockrailbase$enumraildirection == BlockFlumeRailBase.EnumRailDirection.ASCENDING_SOUTH && !worldIn.getBlockState(pos.south()).isSideSolid(worldIn, pos.south(), EnumFacing.UP))
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

    protected void updateState(IBlockState p_189541_1_, World p_189541_2_, BlockPos p_189541_3_, Block p_189541_4_)
    {
    }

    protected IBlockState updateDir(World worldIn, BlockPos pos, IBlockState state, boolean p_176564_4_)
    {
        return worldIn.isRemote ? state : (new BlockFlumeRailBase.Rail(worldIn, pos, state)).place(worldIn.isBlockPowered(pos), p_176564_4_).getBlockState();
    }

    public EnumPushReaction getMobilityFlag(IBlockState state)
    {
        return EnumPushReaction.NORMAL;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);

        if (getRailDirection(worldIn, pos, state, null).isAscending())
        {
            worldIn.notifyNeighborsOfStateChange(pos.up(), this, false);
        }

        if (this.isPowered)
        {
            worldIn.notifyNeighborsOfStateChange(pos, this, false);
            worldIn.notifyNeighborsOfStateChange(pos.down(), this, false);
        }
    }

    public abstract IProperty<BlockFlumeRailBase.EnumRailDirection> getShapeProperty();

    /* ======================================== FORGE START =====================================*/
    /**
     * Return true if the rail can make corners.
     * Used by placement logic.
     * @param world The world.
     * @param pos Block's position in world
     * @return True if the rail can make corners.
     */
    public boolean isFlexible(IBlockAccess world, BlockPos pos)
    {
        return !this.isPowered;
    }

    /**
     * Returns true if the rail can make up and down slopes.
     * Used by placement logic.
     * @param world The world.
     * @param pos Block's position in world
     * @return True if the rail can make slopes.
     */
    public boolean canMakeSlopes(IBlockAccess world, BlockPos pos)
    {
        return false;
    }

    /**
     * Return the rail's direction.
     * Can be used to make the cart think the rail is a different shape,
     * for example when making diamond junctions or switches.
     * The cart parameter will often be null unless it it called from EntityFlumeBoat.
     *
     * @param world The world.
     * @param pos Block's position in world
     * @param state The BlockState
     * @param cart The cart asking for the metadata, null if it is not called by EntityFlumeBoat.
     * @return The direction.
     */
    public EnumRailDirection getRailDirection(IBlockAccess world, BlockPos pos, IBlockState state, @javax.annotation.Nullable EntityFlumeBoat flume)
    {
        return state.getValue(getShapeProperty());
    }
    
    /**
     * Returns the speed of the rail at the specified position.
     * @param world The world.
     * @param flume The flume on the rail, may be null.
     * @param pos Block's position in world
     * @return The speed of the current rail.
     */
    public float getSpeed(World world, EntityFlumeBoat flume, BlockPos pos)
    {
        return 0.0012f;
    }
    
    /**
     * Returns the max speed of the rail at the specified position.
     * @param world The world.
     * @param flume The flume on the rail, may be null.
     * @param pos Block's position in world
     * @return The max speed of the current rail.
     */
    public float getMaxSpeed(World world, EntityFlumeBoat flume, BlockPos pos)
    {
        return 0.4f;
    }
    
    public Vec3d handleMotion(World world, EntityFlumeBoat flume, BlockPos pos, Vec3d motion){
    	return motion;
    }

    /**
     * This function is called by any minecart that passes over this rail.
     * It is called once per update tick that the minecart is on the rail.
     * @param world The world.
     * @param flume The flume on the rail.
     * @param pos Block's position in world
     */
    public void onFlumePass(World world, EntityFlumeBoat flume, BlockPos pos)
    {
    }

    /**
     * Rotate the block. For vanilla blocks this rotates around the axis passed in (generally, it should be the "face" that was hit).
     * Note: for mod blocks, this is up to the block and modder to decide. It is not mandated that it be a rotation around the
     * face, but could be a rotation to orient *to* that face, or a visiting of possible rotations.
     * The method should return true if the rotation was successful though.
     *
     * @param world The world
     * @param pos Block position in world
     * @param axis The axis to rotate around
     * @return True if the rotation was successful, False if the rotation failed, or is not possible
     */
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
    {
        IBlockState state = world.getBlockState(pos);
        for (IProperty prop : state.getProperties().keySet())
        {
            if (prop.getName().equals("shape"))
            {
                world.setBlockState(pos, state.cycleProperty(prop));
                return true;
            }
        }
        return false;
    }

    /* ======================================== FORGE END =====================================*/

    public static enum EnumRailDirection implements IStringSerializable
    {
        NORTH_SOUTH(0, "north_south"),
        EAST_WEST(1, "east_west"),
        ASCENDING_EAST(2, "ascending_east"),
        ASCENDING_WEST(3, "ascending_west"),
        ASCENDING_NORTH(4, "ascending_north"),
        ASCENDING_SOUTH(5, "ascending_south"),
        SOUTH_EAST(6, "south_east"),
        SOUTH_WEST(7, "south_west"),
        NORTH_WEST(8, "north_west"),
        NORTH_EAST(9, "north_east");

        private static final BlockFlumeRailBase.EnumRailDirection[] META_LOOKUP = new BlockFlumeRailBase.EnumRailDirection[values().length];
        private final int meta;
        private final String name;

        private EnumRailDirection(int meta, String name)
        {
            this.meta = meta;
            this.name = name;
        }

        public int getMetadata()
        {
            return this.meta;
        }

        public String toString()
        {
            return this.name;
        }

        public boolean isAscending()
        {
            return this == ASCENDING_NORTH || this == ASCENDING_EAST || this == ASCENDING_SOUTH || this == ASCENDING_WEST;
        }
        
        public boolean isCorner()
        {
            return this == NORTH_EAST || this == NORTH_WEST || this == SOUTH_EAST || this == SOUTH_WEST;
        }

        public static BlockFlumeRailBase.EnumRailDirection byMetadata(int meta)
        {
            if (meta < 0 || meta >= META_LOOKUP.length)
            {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public String getName()
        {
            return this.name;
        }

        static
        {
            for (BlockFlumeRailBase.EnumRailDirection blockrailbase$enumraildirection : values())
            {
                META_LOOKUP[blockrailbase$enumraildirection.getMetadata()] = blockrailbase$enumraildirection;
            }
        }
    }

    public class Rail
    {
        private final World world;
        private final BlockPos pos;
        private final BlockFlumeRailBase block;
        private IBlockState state;
        private final boolean isPowered;
        private final List<BlockPos> connectedRails = Lists.<BlockPos>newArrayList();
        private final boolean canMakeSlopes;

        public Rail(World worldIn, BlockPos pos, IBlockState state)
        {
            this.world = worldIn;
            this.pos = pos;
            this.state = state;
            this.block = (BlockFlumeRailBase)state.getBlock();
            BlockFlumeRailBase.EnumRailDirection blockrailbase$enumraildirection = block.getRailDirection(worldIn, pos, state, null);
            this.isPowered = !this.block.isFlexible(worldIn, pos);
            this.canMakeSlopes = this.block.canMakeSlopes(worldIn, pos);
            this.updateConnectedRails(blockrailbase$enumraildirection);
        }

        public List<BlockPos> getConnectedRails()
        {
            return this.connectedRails;
        }

        private void updateConnectedRails(BlockFlumeRailBase.EnumRailDirection railDirection)
        {
            this.connectedRails.clear();

            switch (railDirection)
            {
                case NORTH_SOUTH:
                    this.connectedRails.add(this.pos.north());
                    this.connectedRails.add(this.pos.south());
                    break;
                case EAST_WEST:
                    this.connectedRails.add(this.pos.west());
                    this.connectedRails.add(this.pos.east());
                    break;
                case ASCENDING_EAST:
                    this.connectedRails.add(this.pos.west());
                    this.connectedRails.add(this.pos.east().up());
                    break;
                case ASCENDING_WEST:
                    this.connectedRails.add(this.pos.west().up());
                    this.connectedRails.add(this.pos.east());
                    break;
                case ASCENDING_NORTH:
                    this.connectedRails.add(this.pos.north().up());
                    this.connectedRails.add(this.pos.south());
                    break;
                case ASCENDING_SOUTH:
                    this.connectedRails.add(this.pos.north());
                    this.connectedRails.add(this.pos.south().up());
                    break;
                case SOUTH_EAST:
                    this.connectedRails.add(this.pos.east());
                    this.connectedRails.add(this.pos.south());
                    break;
                case SOUTH_WEST:
                    this.connectedRails.add(this.pos.west());
                    this.connectedRails.add(this.pos.south());
                    break;
                case NORTH_WEST:
                    this.connectedRails.add(this.pos.west());
                    this.connectedRails.add(this.pos.north());
                    break;
                case NORTH_EAST:
                    this.connectedRails.add(this.pos.east());
                    this.connectedRails.add(this.pos.north());
            }
        }

        private void removeSoftConnections()
        {
            for (int i = 0; i < this.connectedRails.size(); ++i)
            {
                BlockFlumeRailBase.Rail blockrailbase$rail = this.findRailAt((BlockPos)this.connectedRails.get(i));

                if (blockrailbase$rail != null && blockrailbase$rail.isConnectedToRail(this))
                {
                    this.connectedRails.set(i, blockrailbase$rail.pos);
                }
                else
                {
                    this.connectedRails.remove(i--);
                }
            }
        }

        private boolean hasRailAt(BlockPos pos)
        {
            return BlockFlumeRailBase.isRailBlock(this.world, pos) || BlockFlumeRailBase.isRailBlock(this.world, pos.up()) || BlockFlumeRailBase.isRailBlock(this.world, pos.down());
        }

        @Nullable
        private BlockFlumeRailBase.Rail findRailAt(BlockPos pos)
        {
            IBlockState iblockstate = this.world.getBlockState(pos);

            if (BlockFlumeRailBase.isRailBlock(iblockstate))
            {
                return iblockstate.getBlock() instanceof BlockFlumeRailAscending ? null : BlockFlumeRailBase.this.new Rail(this.world, pos, iblockstate);
            }
            else
            {
                BlockPos lvt_2_1_ = pos.up();
                iblockstate = this.world.getBlockState(lvt_2_1_);

                if (BlockFlumeRailBase.isRailBlock(iblockstate))
                {
                    return iblockstate.getBlock() instanceof BlockFlumeRailAscending ? null : BlockFlumeRailBase.this.new Rail(this.world, lvt_2_1_, iblockstate);
                }
                else
                {
                    lvt_2_1_ = pos.down();
                    iblockstate = this.world.getBlockState(lvt_2_1_);
                    return BlockFlumeRailBase.isRailBlock(iblockstate) ? iblockstate.getBlock() instanceof BlockFlumeRailAscending ? null : BlockFlumeRailBase.this.new Rail(this.world, lvt_2_1_, iblockstate) : null;
                }
            }
        }

        private boolean isConnectedToRail(BlockFlumeRailBase.Rail rail)
        {
            return this.isConnectedTo(rail.pos);
        }

        private boolean isConnectedTo(BlockPos posIn)
        {
            for (int i = 0; i < this.connectedRails.size(); ++i)
            {
                BlockPos blockpos = (BlockPos)this.connectedRails.get(i);

                if (blockpos.getX() == posIn.getX() && blockpos.getZ() == posIn.getZ())
                {
                    return true;
                }
            }

            return false;
        }

        /**
         * Counts the number of rails adjacent to this rail.
         */
        public int countAdjacentRails()
        {
            int i = 0;

            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
            {
                if (this.hasRailAt(this.pos.offset(enumfacing)))
                {
                    ++i;
                }
            }

            return i;
        }

        private boolean canConnectTo(BlockFlumeRailBase.Rail rail)
        {
            return this.isConnectedToRail(rail) || this.connectedRails.size() != 2;
        }

        private void connectTo(BlockFlumeRailBase.Rail p_150645_1_)
        {
            this.connectedRails.add(p_150645_1_.pos);
            BlockPos blockpos = this.pos.north();
            BlockPos blockpos1 = this.pos.south();
            BlockPos blockpos2 = this.pos.west();
            BlockPos blockpos3 = this.pos.east();
            boolean flag = this.isConnectedTo(blockpos);
            boolean flag1 = this.isConnectedTo(blockpos1);
            boolean flag2 = this.isConnectedTo(blockpos2);
            boolean flag3 = this.isConnectedTo(blockpos3);
            BlockFlumeRailBase.EnumRailDirection blockrailbase$enumraildirection = null;

            if (flag || flag1)
            {
                blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            if (flag2 || flag3)
            {
                blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.EAST_WEST;
            }

            if (!this.isPowered)
            {
                if (flag1 && flag3 && !flag && !flag2)
                {
                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.SOUTH_EAST;
                }

                if (flag1 && flag2 && !flag && !flag3)
                {
                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.SOUTH_WEST;
                }

                if (flag && flag2 && !flag1 && !flag3)
                {
                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.NORTH_WEST;
                }

                if (flag && flag3 && !flag1 && !flag2)
                {
                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.NORTH_EAST;
                }
            }

            if (blockrailbase$enumraildirection == BlockFlumeRailBase.EnumRailDirection.NORTH_SOUTH && canMakeSlopes)
            {
                if (BlockFlumeRailBase.isRailBlock(this.world, blockpos.up()))
                {
                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.ASCENDING_NORTH;
                }

                if (BlockFlumeRailBase.isRailBlock(this.world, blockpos1.up()))
                {
                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.ASCENDING_SOUTH;
                }
            }

            if (blockrailbase$enumraildirection == BlockFlumeRailBase.EnumRailDirection.EAST_WEST && canMakeSlopes)
            {
                if (BlockFlumeRailBase.isRailBlock(this.world, blockpos3.up()))
                {
                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.ASCENDING_EAST;
                }

                if (BlockFlumeRailBase.isRailBlock(this.world, blockpos2.up()))
                {
                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.ASCENDING_WEST;
                }
            }

            if (blockrailbase$enumraildirection == null)
            {
                blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            IProperty<BlockFlumeRailBase.EnumRailDirection> shape = this.block.getShapeProperty();
            if(shape.getAllowedValues().contains(blockrailbase$enumraildirection)){
            	this.state = this.state.withProperty(shape, blockrailbase$enumraildirection);
            	this.world.setBlockState(this.pos, this.state, 3);
            }
        }

        private boolean hasNeighborRail(BlockPos p_180361_1_)
        {
            BlockFlumeRailBase.Rail blockrailbase$rail = this.findRailAt(p_180361_1_);

            if (blockrailbase$rail == null)
            {
                return false;
            }
            else
            {
                blockrailbase$rail.removeSoftConnections();
                return blockrailbase$rail.canConnectTo(this);
            }
        }

        public BlockFlumeRailBase.Rail place(boolean p_180364_1_, boolean p_180364_2_)
        {
            BlockPos blockpos = this.pos.north();
            BlockPos blockpos1 = this.pos.south();
            BlockPos blockpos2 = this.pos.west();
            BlockPos blockpos3 = this.pos.east();
            boolean flag = this.hasNeighborRail(blockpos);
            boolean flag1 = this.hasNeighborRail(blockpos1);
            boolean flag2 = this.hasNeighborRail(blockpos2);
            boolean flag3 = this.hasNeighborRail(blockpos3);
            BlockFlumeRailBase.EnumRailDirection blockrailbase$enumraildirection = null;

            if ((flag || flag1) && !flag2 && !flag3)
            {
                blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            if ((flag2 || flag3) && !flag && !flag1)
            {
                blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.EAST_WEST;
            }

            if (!this.isPowered)
            {
                if (flag1 && flag3 && !flag && !flag2)
                {
                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.SOUTH_EAST;
                }

                if (flag1 && flag2 && !flag && !flag3)
                {
                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.SOUTH_WEST;
                }

                if (flag && flag2 && !flag1 && !flag3)
                {
                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.NORTH_WEST;
                }

                if (flag && flag3 && !flag1 && !flag2)
                {
                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.NORTH_EAST;
                }
            }

            if (blockrailbase$enumraildirection == null)
            {
                if (flag || flag1)
                {
                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.NORTH_SOUTH;
                }

                if (flag2 || flag3)
                {
                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.EAST_WEST;
                }

                if (!this.isPowered)
                {
                    if (p_180364_1_)
                    {
                        if (flag1 && flag3)
                        {
                            blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.SOUTH_EAST;
                        }

                        if (flag2 && flag1)
                        {
                            blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.SOUTH_WEST;
                        }

                        if (flag3 && flag)
                        {
                            blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.NORTH_EAST;
                        }

                        if (flag && flag2)
                        {
                            blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.NORTH_WEST;
                        }
                    }
                    else
                    {
                        if (flag && flag2)
                        {
                            blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.NORTH_WEST;
                        }

                        if (flag3 && flag)
                        {
                            blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.NORTH_EAST;
                        }

                        if (flag2 && flag1)
                        {
                            blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.SOUTH_WEST;
                        }

                        if (flag1 && flag3)
                        {
                            blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.SOUTH_EAST;
                        }
                    }
                }
            }

            if (blockrailbase$enumraildirection == BlockFlumeRailBase.EnumRailDirection.NORTH_SOUTH && canMakeSlopes)
            {
                if (BlockFlumeRailBase.isRailBlock(this.world, blockpos.up()))
                {
                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.ASCENDING_NORTH;
                }

                if (BlockFlumeRailBase.isRailBlock(this.world, blockpos1.up()))
                {
                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.ASCENDING_SOUTH;
                }
            }

            if (blockrailbase$enumraildirection == BlockFlumeRailBase.EnumRailDirection.EAST_WEST && canMakeSlopes)
            {
                if (BlockFlumeRailBase.isRailBlock(this.world, blockpos3.up()))
                {
                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.ASCENDING_EAST;
                }

                if (BlockFlumeRailBase.isRailBlock(this.world, blockpos2.up()))
                {
                    blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.ASCENDING_WEST;
                }
            }

            if (blockrailbase$enumraildirection == null)
            {
                blockrailbase$enumraildirection = BlockFlumeRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            this.updateConnectedRails(blockrailbase$enumraildirection);
            IProperty<BlockFlumeRailBase.EnumRailDirection> shape = this.block.getShapeProperty();
            if(shape.getAllowedValues().contains(blockrailbase$enumraildirection)){
            	this.state = this.state.withProperty(shape, blockrailbase$enumraildirection);
            }

            if (p_180364_2_ || this.world.getBlockState(this.pos) != this.state)
            {
                this.world.setBlockState(this.pos, this.state, 3);

                for (int i = 0; i < this.connectedRails.size(); ++i)
                {
                    BlockFlumeRailBase.Rail blockrailbase$rail = this.findRailAt((BlockPos)this.connectedRails.get(i));

                    if (blockrailbase$rail != null)
                    {
                        blockrailbase$rail.removeSoftConnections();

                        if (blockrailbase$rail.canConnectTo(this))
                        {
                            blockrailbase$rail.connectTo(this);
                        }
                    }
                }
            }

            return this;
        }

        public IBlockState getBlockState()
        {
            return this.state;
        }
    }
}