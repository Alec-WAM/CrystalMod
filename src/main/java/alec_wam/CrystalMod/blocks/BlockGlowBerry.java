package alec_wam.CrystalMod.blocks;

import java.awt.Color;
import java.util.EnumSet;
import java.util.Random;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.blocks.crops.BlockCrystalPlant.PlantType;
import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGlowBerry extends Block
{
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumSet.of(EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST));

	protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.5625D, 0.3125D, 0.3125D, 0.9375D, 0.75D, 0.6875D);
    protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0625D, 0.3125D, 0.3125D, 0.4375D, 0.75D, 0.6875D);
    protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.3125D, 0.3125D, 0.0625D, 0.6875D, 0.75D, 0.4375D);
    protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.3125D, 0.3125D, 0.5625D, 0.6875D, 0.75D, 0.9375D);

    public final PlantType TYPE;

    public BlockGlowBerry(PlantType type)
    {
        super(Material.PLANTS);
        this.setSoundType(SoundType.PLANT);
        this.TYPE = type;
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.setCreativeTab(null);
        this.setLightLevel(1.0f);
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state){
    	return EnumBlockRenderType.MODEL;
    }
    
    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!this.canBlockStay(worldIn, pos, state))
        {
            this.dropBlock(worldIn, pos, state);
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        double x = (double)((float)pos.getX() + 0.4F + rand.nextFloat() * 0.1F);
        double y = (double)((float)pos.getY() + 0.4F + rand.nextFloat() * 0.01F);
        double z = (double)((float)pos.getZ() + 0.4F + rand.nextFloat() * 0.1F);
        int i = Color.YELLOW.getRGB();

        if (i != -1)
        {
            double r = (double)(i >> 16 & 255) / 255.0D;
            double g = (double)(i >> 8 & 255) / 255.0D;
            double b = (double)(i >> 0 & 255) / 255.0D;

            for (int j = 0; j < 1; ++j)
            {
                worldIn.spawnParticle(EnumParticleTypes.SPELL_MOB, x, y, z, r, g, b, new int[0]);
            }
        }
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
    	EnumFacing facing = state.getValue(FACING);
    	if(facing == EnumFacing.DOWN){
    		pos = pos.offset(facing.getOpposite());
            IBlockState iblockstate = worldIn.getBlockState(pos);
            if(!iblockstate.isSideSolid(worldIn, pos, facing))return false;
            return true;
    	}
    	pos = pos.offset(facing);
        IBlockState iblockstate = worldIn.getBlockState(pos);
        if(!iblockstate.isSideSolid(worldIn, pos, facing.getOpposite()))return false;
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
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
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
    	EnumFacing facing = state.getValue(FACING);
    	if(facing == EnumFacing.NORTH){
    		return new AxisAlignedBB(0.180D, 0.0D, 0.06D, 0.820D, 0.75D, 0.6875D);
    	}
    	if(facing == EnumFacing.SOUTH){
    		return new AxisAlignedBB(0.180D, 0.0D, 0.3155D, 0.820D, 0.75D, 0.9375D);
    	}
    	if(facing == EnumFacing.EAST){
    		return new AxisAlignedBB(0.3155D, 0.0D, 0.180D, 0.9375D, 0.75D, 0.820D);
    	}
    	if(facing == EnumFacing.WEST){
    		return new AxisAlignedBB(0.06D, 0.0D, 0.180D, 0.6875D, 0.75D, 0.820D);
    	}
    	return new AxisAlignedBB(0.180D, 0.0D, 0.2D, 0.820D, 0.75D, 0.8D);
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        //EnumFacing enumfacing = EnumFacing.fromAngle((double)placer.rotationYaw);
        //worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
    	if (facing == EnumFacing.UP)
        {
    		facing = EnumFacing.DOWN;
        }
    	if (facing == EnumFacing.DOWN)
        {
    		return this.getDefaultState().withProperty(FACING, facing);
        }

        return this.getDefaultState().withProperty(FACING, facing.getOpposite());
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos from)
    {
        if (!this.canBlockStay(worldIn, pos, state))
        {
            this.dropBlock(worldIn, pos, state);
        }
    }

    private void dropBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        this.dropBlockAsItem(worldIn, pos, state, 0);
    }

    @Override
    public java.util.List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        java.util.List<ItemStack> dropped = Lists.newArrayList();
        dropped.add(new ItemStack(ModItems.glowBerry, 1, TYPE.getMeta()));
        return dropped;
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(ModItems.glowBerry, 1, TYPE.getMeta());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getIndex();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING});
    }
}