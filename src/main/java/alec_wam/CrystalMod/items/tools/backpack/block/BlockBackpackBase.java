package alec_wam.CrystalMod.items.tools.backpack.block;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.items.tools.backpack.IBackpack;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackBase;
import alec_wam.CrystalMod.tiles.machine.IFacingTile;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBackpackBase extends BlockContainer {

	private IBackpack type;
	
	public BlockBackpackBase(IBackpack type, Material mat) {
		super(mat);
		this.type = type;
		this.setCreativeTab(null);
		this.setHardness(0.8F);
        this.setSoundType(SoundType.CLOTH);
		this.setHarvestLevel("pickaxe", -1);
	}

	@Override
	public boolean isBlockNormalCube(IBlockState state)
    {
        return false;
    }

	@Override
	public boolean isNormalCube(IBlockState state)
    {
        return false;
    }

	@Override
	public boolean isFullCube(IBlockState state)
    {
        return false;
    }
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
	
	@SideOnly(Side.CLIENT)
    public boolean hasCustomBreakingProgress(IBlockState state)
    {
        return true;
    }
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityBackpack){
			TileEntityBackpack backpack = (TileEntityBackpack)tile;
			UUID owner = backpack.getOwner();
			if(owner == null || UUIDUtils.areEqual(player.getUniqueID(), owner)){
				if(type !=null){
					return type.getBlockHandler().onBlockActivated(world, pos, player, facing);
				}
			}
		}
        return false;
    }
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityBackpack){
			TileEntityBackpack backpack = (TileEntityBackpack)tile;
			backpack.setFacing(placer.getHorizontalFacing().getOpposite().getHorizontalIndex());
			
			if(ItemStackTools.isValid(stack) && stack.getItem() instanceof ItemBackpackBase)backpack.loadFromStack(stack);
			BlockUtil.markBlockForUpdate(world, pos);
		}
    }
	
	
	
	public static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.13, 0.0, 0.15, 0.87, 0.9, 0.77);
	public static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.13, 0.0, 0.23, 0.87, 0.9, 0.85);
	public static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(0.23, 0.0, 0.13, 0.85, 0.9, 0.87);
	public static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(0.15, 0.0, 0.13, 0.77, 0.9, 0.87);
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
    	TileEntity tile = source.getTileEntity(pos);
    	if(tile !=null && tile instanceof IFacingTile){
    		EnumFacing facing = EnumFacing.getHorizontal(((IFacingTile)tile).getFacing());
    		if(facing == EnumFacing.NORTH){
    			return AABB_NORTH;
    		}
    		if(facing == EnumFacing.SOUTH){
    			return AABB_SOUTH;
    		}
    		if(facing == EnumFacing.EAST){
    			return AABB_EAST;
    		}
    		if(facing == EnumFacing.WEST){
    			return AABB_WEST;
    		}
    	}
        return FULL_BLOCK_AABB;
    }
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
	    if (willHarvest) {
	      return true;
	    }
	    return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te,
	      @Nullable ItemStack stack) {
	    super.harvestBlock(worldIn, player, pos, state, te, stack);
	    worldIn.setBlockToAir(pos);
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
		List<ItemStack> items = Lists.newArrayList();
		items.add(getDrop(world, pos));
		return items;
	}
	
	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return getDrop(worldIn, pos);
    }
	
	public ItemStack getDrop(IBlockAccess world, BlockPos pos){
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEntityBackpack){
			TileEntityBackpack backpack = (TileEntityBackpack)tile;
			return backpack.getDroppedBackpack();
		}
		return ItemStackTools.getEmptyStack();
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return type.getBlockHandler().createTile(world);
	}
	
	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
		return canBlockStay(worldIn, pos);
    }
	
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        this.checkAndDropBlock(worldIn, pos, state);
    }

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        this.checkAndDropBlock(worldIn, pos, state);
    }

    protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!this.canBlockStay(worldIn, pos))
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
    }

    public boolean canBlockStay(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP);
    }

}
