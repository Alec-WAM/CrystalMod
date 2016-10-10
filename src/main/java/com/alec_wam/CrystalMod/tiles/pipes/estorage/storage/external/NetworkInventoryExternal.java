package com.alec_wam.CrystalMod.tiles.pipes.estorage.storage.external;

import java.util.Iterator;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;

import com.alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.IInsertListener;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork.ItemStackData;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.INetworkContainer;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.inventory.INetworkInventory;
import com.alec_wam.CrystalMod.tiles.pipes.item.InventoryWrapper;
import com.alec_wam.CrystalMod.util.ItemUtil;
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
	public List<ItemStackData> getItems(EStorageNetwork network) {
		List<ItemStackData> items = Lists.newArrayList();
		if (getInventory() !=null) {
			int[] array = getInventory().getSlotsForFace(EnumFacing.getFront(inter.facing));
			if(array == null)return items;
			
			for (int i = 0; i < array.length; i++) {
				ItemStack stack = getInventory().getStackInSlot(array[i]);
				if (stack != null) {
					if(network.getData(stack) == null){
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
						boolean edited = false;
						ItemStackData itemData = null;
						if (stack != null) {
							itemData = new ItemStackData(stack, slot, pos, dim);
							changed.add(itemData);
						}
						List<ItemStackData> copy = Lists.newArrayList(network.items.iterator());
						Iterator<ItemStackData> ii = copy.iterator();
						while(ii.hasNext()){
							ItemStackData data = ii.next();
							if (data.interPos != null
									&& data.interPos.equals(pos) && data.interDim == dim) {
								if (data.index == slot) {
									data.stack = stack;
									if (data.stack == null) {
										network.items.remove(data);
										changed.add(data);
									}
									edited = true;
									continue;
								}
							}
						}
						if (itemData != null && edited == false) {
							if(network.getData(itemData.stack) == null){
								network.items.add(itemData);
							}
						}
					}
				} else {
					int slot = array[index];
					ItemStack stack = getInventory().getStackInSlot(slot);
					ItemStackData itemData = null;
					if (stack != null) {
						itemData = new ItemStackData(stack, slot, pos, dim);
						changed.add(itemData);
					}
					boolean edited = false;
					List<ItemStackData> copy = Lists.newArrayList(network.items.iterator());
					Iterator<ItemStackData> ii = copy.iterator();
					while(ii.hasNext()){
						ItemStackData data = ii.next();
						if (data.interPos != null && data.interPos.equals(pos) && data.interDim == dim) {
							if (data.index == slot) {
								data.stack = stack;
								edited = true;
								if (data.stack == null) {
									network.items.remove(data);
									changed.add(data);
								}
								continue;
							}
						}
					}
					if (itemData != null && edited == false) {
						if(network.getData(itemData.stack) == null){
							network.items.add(itemData);
						}
					}
				}
			} else{
				changed.addAll(removeAllItems(network));
			}
		}else{
			changed.addAll(removeAllItems(network));
		}
		if (!changed.isEmpty()) {
			for (INetworkContainer panel : network.watchers) {
				panel.sendItemsToAll(changed);
			}
		}
	}

	public List<ItemStackData> removeAllItems(EStorageNetwork network){
		List<ItemStackData> changed = Lists.newArrayList();
		BlockPos pos = inter.getPos();
		int dim = inter.getWorld().provider.getDimension();
		List<ItemStackData> copy = Lists.newArrayList(network.items.iterator());
		Iterator<ItemStackData> ii = copy.iterator();
		while(ii.hasNext()){
			ItemStackData data = ii.next();
			if (data.interPos != null && data.interPos.equals(pos) && data.interDim == dim) {
				network.items.remove(data);
				changed.add(data);
			}
		}
		return changed;
	}
	
	@Override
	public int insertItem(EStorageNetwork network, ItemStack stack, boolean matching, boolean sim, boolean update) {
		if(matching){
			if (getInventory() !=null) {
				int amt = ItemUtil.doInsertItemMatching(getInventory(), stack, EnumFacing.getFront(inter.facing));
				/*if(amt > 0){
					updateItems(network, -1);
				}*/
				if(amt > 0 && update){
					network.notifyInsert(stack);
				}
				return amt;
			}
		}else{
			if (getInventory() !=null) {
				int amt = ItemUtil.doInsertItem(getInventory(), stack, EnumFacing.getFront(inter.facing));
				/*if(amt > 0){
					updateItems(network, -1);
				}*/
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
							//updateItems(network, array[i]);
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

}
