package alec_wam.CrystalMod.tiles.endertorch;

import java.util.Random;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEnderTorch extends BlockContainer {

    protected static final AxisAlignedBB STANDING_AABB = new AxisAlignedBB(0.4000000059604645D, 0.0D, 0.4000000059604645D, 0.6000000238418579D, 0.6000000238418579D, 0.6000000238418579D);

	public BlockEnderTorch() {
		super(Material.IRON);
        this.setTickRandomly(true);
		this.setCreativeTab(CrystalMod.tabTools);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
		return STANDING_AABB;
    }
	
	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(ModBlocks.enderTorch);
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEnderTorch();
	}
	
	@Nullable
	@Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
		return NULL_AABB;
    }

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
    	TileEntity tile = worldIn.getTileEntity(pos);
    	if(tile == null || !(tile instanceof TileEnderTorch))return false;
    	TileEnderTorch torch = (TileEnderTorch)tile;
    	if(ItemStackTools.isEmpty(playerIn.getHeldItem(hand))){
        	if(!worldIn.isRemote){
        		double incrs = 1.0D;
        		if(playerIn.isSneaking()){
        			incrs = 0.5D;
        		}
        		torch.range+=incrs;
        		torch.range%=16.0D;
        		ChatUtil.sendNoSpam(playerIn, "Range: "+torch.range);
        		torch.markDirty();
        	}
        	return true;
    	}
        return false;
    }
    
    private boolean canPlaceOn(World worldIn, BlockPos pos)
    {
        IBlockState state = worldIn.getBlockState(pos);
        if (state.isSideSolid(worldIn, pos, EnumFacing.UP))
        {
            return true;
        }
        else
        {
            return state.getBlock().canPlaceTorchOnTop(state, worldIn, pos);
        }
    }
    
    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        if (this.canPlaceAt(worldIn, pos, EnumFacing.UP))
        {
            return true;
        }

        return false;
    }

    private boolean canPlaceAt(World worldIn, BlockPos pos, EnumFacing facing)
    {
        BlockPos blockpos = pos.offset(facing.getOpposite());
        boolean flag = facing.getAxis().isHorizontal();
        return flag && worldIn.isSideSolid(blockpos, facing, true) || facing.equals(EnumFacing.UP) && this.canPlaceOn(worldIn, blockpos);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.checkForDrop(worldIn, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos pos2)
    {
    	this.onNeighborChangeInternal(worldIn, pos, state);
    }

    protected boolean onNeighborChangeInternal(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!this.checkForDrop(worldIn, pos, state))
        {
            return true;
        }
        else
        {
            EnumFacing enumfacing = EnumFacing.UP;
            EnumFacing.Axis enumfacing$axis = enumfacing.getAxis();
            EnumFacing enumfacing1 = enumfacing.getOpposite();
            boolean flag = false;

            if (enumfacing$axis.isHorizontal() && !worldIn.isSideSolid(pos.offset(enumfacing1), enumfacing, true))
            {
                flag = true;
            }
            else if (enumfacing$axis.isVertical() && !this.canPlaceOn(worldIn, pos.offset(enumfacing1)))
            {
                flag = true;
            }

            if (flag)
            {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    protected boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state)
    {
        if (state.getBlock() == this && this.canPlaceAt(worldIn, pos, EnumFacing.UP))
        {
            return true;
        }
        else
        {
            if (worldIn.getBlockState(pos).getBlock() == this)
            {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }

            return false;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        double d0 = pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
        double d1 = pos.getY() + 0.7D + (rand.nextDouble() - 0.5D) * 0.2D;
        double d2 = pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
        float f = rand.nextFloat() * 0.6F + 0.4F;
        worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1, d2, f * 0.9F, f * 0.3F, f, new int[0]);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

}
