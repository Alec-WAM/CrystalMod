package alec_wam.CrystalMod.tiles;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class BlockContainerCustom extends BlockContainer {
	
	protected BlockContainerCustom(Properties builder) {
		super(builder);
	}

	@Override
	public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof IInventory) {
				InventoryHelper.dropInventoryItems(worldIn, pos, ((IInventory)tileentity));
				worldIn.updateComparatorOutputLevel(pos, this);
			}

			super.onReplaced(state, worldIn, pos, newState, isMoving);
		} 
	}
	
	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState blockState, EntityLivingBase entityliving, ItemStack itemStack)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof INBTDrop)
        {
        	INBTDrop nbtTile = (INBTDrop) te;
            if(itemStack.hasTag()){
            	nbtTile.readFromItemNBT(itemStack);
                BlockUtil.markBlockForUpdate(world, pos);
            }
        }
    }
	
	@Override
	public void getDrops(IBlockState state, NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune) {
	    TileEntity tile = world.getTileEntity(pos);
	    drops.add(tile !=null ? getNBTDrop(world, pos, tile) : new ItemStack(this));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity instanceof INBTDrop ? getNBTDrop(worldIn, pos, tileentity) : super.getItem(worldIn, pos, state);
	}
	
	@Override	
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest, IFluidState fluid)
    {
		if(willHarvest){
			return true;
		}
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }
	
	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
		super.harvestBlock(worldIn, player, pos, state, te, stack);
		worldIn.removeBlock(pos);
	}
	
	protected ItemStack getNBTDrop(IBlockReader world, BlockPos pos, TileEntity tileEntity) {
		ItemStack stack = new ItemStack(this);
		if(tileEntity instanceof INBTDrop){
			INBTDrop nbtTile = (INBTDrop)tileEntity;
			nbtTile.writeToItemNBT(stack);
		}
		return stack;
	}
	
}
