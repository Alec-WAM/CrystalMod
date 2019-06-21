package alec_wam.CrystalMod.tiles;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

public abstract class ContainerBlockCustom extends ContainerBlock {
	
	protected ContainerBlockCustom(Properties builder) {
		super(builder);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof IInventory && !(tileentity instanceof INBTDrop)) {
				InventoryHelper.dropInventoryItems(worldIn, pos, ((IInventory)tileentity));
				worldIn.updateComparatorOutputLevel(pos, this);
			}

			super.onReplaced(state, worldIn, pos, newState, isMoving);
		} 
	}
	
	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState blockState, LivingEntity entityliving, ItemStack itemStack)
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
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		BlockPos pos = builder.get(LootParameters.field_216286_f);
		ServerWorld world = builder.func_216018_a();
		TileEntity tile = world.getTileEntity(pos);
	    return Lists.newArrayList(tile !=null ? getNBTDrop(world, pos, tile) : new ItemStack(this));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity instanceof INBTDrop ? getNBTDrop(worldIn, pos, tileentity) : super.getItem(worldIn, pos, state);
	}
	
	@Override	
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid)
    {
		if(willHarvest){
			return true;
		}
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }
	
	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
		super.harvestBlock(worldIn, player, pos, state, te, stack);
		worldIn.removeBlock(pos, false);
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
