package alec_wam.CrystalMod.tiles.pipes.estorage.storage.external;

import java.util.Iterator;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage.FluidStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.IInsertListener;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.INetworkContainer;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.inventory.INetworkInventory;
import alec_wam.CrystalMod.tiles.pipes.item.InventoryWrapper;
import alec_wam.CrystalMod.util.ItemUtil;

import com.google.common.collect.Lists;

public class NetworkInventoryExternal implements INetworkInventory {

	public TileEntityExternalInterface inter;
	public NetworkInventoryExternal(TileEntityExternalInterface inter){
		this.inter = inter;
	}
	
	public ISidedInventory getInventory(){
		TileEntity external = inter.getWorld().getTileEntity(inter.getPos().offset(EnumFacing.getFront(inter.facing)));
		if(external !=null){
			if(external instanceof ISidedInventory){
				return (ISidedInventory)external;
			}else if(external instanceof IInventory){
				return new InventoryWrapper((IInventory)external);
			}
		}
		return null; 
	}
	
	@Override
	public List<ItemStackData> getItems(ItemStorage storage) {
		List<ItemStackData> items = Lists.newArrayList();
		if (getInventory() !=null) {
			int[] array = getInventory().getSlotsForFace(EnumFacing.getFront(inter.facing));
			if(array == null)return items;
			
			for (int i = 0; i < array.length; i++) {
				ItemStack stack = getInventory().getStackInSlot(array[i]);
				if (stack != null) {
					if(storage.getItemData(stack) == null){
						ItemStackData data = new ItemStackData(stack, i, inter.getPos(), inter.getWorld().provider.getDimension());
						items.add(data);
					}
				}
			}
		}
		return items;
	}

	@Override
	public void updateItems(EStorageNetwork network, int index) {
		ItemStorage storage = network.getItemStorage();
		List<ItemStackData> changed = Lists.newArrayList();
		BlockPos pos = inter.getPos();
		int dim = inter.getWorld().provider.getDimension();
		if (getInventory() !=null) {
			int[] array = getInventory().getSlotsForFace(EnumFacing.getFront(inter.facing));
			if(array != null){
				if (index < 0) {
					for (int i = 0; i < array.length; i++) {
						int slot = array[i];
						ItemStack stack = getInventory().getStackInSlot(slot);
						ItemStackData itemData = new ItemStackData(stack, slot, pos, dim);
						if(storage.addToList(itemData)){
							changed.add(itemData);
						}
					}
				} else {
					int slot = array[index];
					ItemStack stack = getInventory().getStackInSlot(slot);
					ItemStackData itemData = new ItemStackData(stack, slot, pos, dim);
					if(storage.addToList(itemData)){
						changed.add(itemData);
					}
				}
			} else{
				changed.addAll(storage.clearListAtPos(pos, dim));
			}
		}else{
			changed.addAll(storage.clearListAtPos(pos, dim));
		}
		if (!changed.isEmpty()) {
			for (INetworkContainer panel : network.watchers) {
				panel.sendItemsToAll(changed);
			}
		}
	}
	
	@Override
	public int insertItem(EStorageNetwork network, ItemStack stack, boolean matching, boolean sim, boolean update) {
		if(matching){
			if (getInventory() !=null) {
				int amt = ItemUtil.doInsertItemMatching(getInventory(), stack, EnumFacing.getFront(inter.facing));
				if(amt > 0 && update){
					network.notifyInsert(stack);
				}
				return amt;
			}
		}else{
			if (getInventory() !=null) {
				int amt = ItemUtil.doInsertItem(getInventory(), stack, EnumFacing.getFront(inter.facing));
				if(amt > 0 && update){
					network.notifyInsert(stack);
				}
				return amt;
			}
		}
		return 0;
	}

	@Override
	public int extractItem(EStorageNetwork network, ItemStack stack, int amount, boolean sim, boolean update) {
		if (getInventory() !=null) {
			int[] array = getInventory().getSlotsForFace(EnumFacing.getFront(inter.facing));
			if(array != null){
				for (int i = 0; i < array.length; i++) {
					ItemStack invStack = getInventory().getStackInSlot(array[i]);
					if(invStack !=null && ItemUtil.areStacksEqual(stack, invStack)){
						int realCount = Math.min(amount, invStack.stackSize);
						if (sim) {
							return realCount;
						}
						final int oldSize = invStack.stackSize;
						ItemStack copy = invStack.copy();
						copy.stackSize = realCount;
						invStack.stackSize -= realCount;
						if (invStack.stackSize <= 0) {
							invStack = null;
						}
						
						getInventory().setInventorySlotContents(array[i], invStack);
						
						if(invStack == null || oldSize !=invStack.stackSize){
							getInventory().markDirty();
						}
						
						if(copy !=null && update){
							Iterator<IInsertListener> iter = network.listeners.iterator();
							while (iter.hasNext()) {
								iter.next().onItemExtracted(copy, realCount);
							}
						}
						return realCount;
					}
				}
			}
		}
		return 0;
	}

	//TODO Add External Tank Func
	@Override
	public List<FluidStackData> getFluids(FluidStorage storage) {
		return Lists.newArrayList();
	}

	@Override
	public int insertFluid(EStorageNetwork network, FluidStack stack, boolean matching, boolean sim, boolean sendUpdate) {
		return 0;
	}

	@Override
	public int extractFluid(EStorageNetwork network, FluidStack stack, int amount, boolean sim, boolean sendUpdate) {
		return 0;
	}

	@Override
	public void updateFluids(EStorageNetwork network, int index) {
		
	}

}
