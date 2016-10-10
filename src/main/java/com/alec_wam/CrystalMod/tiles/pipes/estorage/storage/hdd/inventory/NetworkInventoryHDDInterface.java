package com.alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import com.alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.IInsertListener;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork.ItemStackData;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.INetworkContainer;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.ItemHDD;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.TileEntityHDDInterface;
import com.alec_wam.CrystalMod.util.ItemUtil;
import com.google.common.collect.Lists;

public class NetworkInventoryHDDInterface implements INetworkInventory {

	public TileEntityHDDInterface inter;
	public NetworkInventoryHDDInterface(TileEntityHDDInterface inter){
		this.inter = inter;
	}
	
	@Override
	public List<ItemStackData> getItems(EStorageNetwork network) {
		List<ItemStackData> items = Lists.newArrayList();
		ItemStack hddStack = inter.getStackInSlot(0);
		if (hddStack != null && hddStack.getItem() instanceof ItemHDD) {
			for (int i = 0; i < ItemHDD.getItemLimit(hddStack); i++) {
				ItemStack stack = ItemHDD.getItem(hddStack, i);
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
		ItemStack hddStack = inter.getStackInSlot(0);
		if (hddStack != null && hddStack.getItem() instanceof ItemHDD) {
			if (index < 0) {
				if(index == -1){
					for (int i = 0; i < ItemHDD.getItemLimit(hddStack); i++) {
						ItemStack stack = ItemHDD.getItem(hddStack, i);
						boolean edited = false;
						ItemStackData itemData = null;
						if (stack != null) {
							itemData = new ItemStackData(stack, i, pos, dim);
							changed.add(itemData);
						}
						ArrayList<ItemStackData> copy = new ArrayList<ItemStackData>();
						for (ItemStackData data : network.items) {
							if (data.stack != null) {
								copy.add(data);
							}
						}
	
						for (ItemStackData data : copy) {
							if (data.interPos != null
									&& data.interPos.equals(pos) && data.interDim == dim) {
								if (data.index == i) {
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
				}
				if(index == -2){
					ArrayList<ItemStackData> copy = new ArrayList<ItemStackData>();
					for (ItemStackData data : network.items) {
						if (data.stack != null) {
							copy.add(data);
						}
					}
					for (ItemStackData data : copy) {
						if (data.interPos != null && data.interPos.equals(pos) && data.interDim == dim) {
							network.items.remove(data);
							changed.add(data);
						}
					}
				}
			} else {
				ItemStack stack = ItemHDD.getItem(hddStack, index);
				ItemStackData itemData = null;
				if (stack != null) {
					itemData = new ItemStackData(stack, index, pos, dim);
					changed.add(itemData);
				}
				boolean edited = false;
				ArrayList<ItemStackData> copy = new ArrayList<ItemStackData>();
				for (ItemStackData data : network.items) {
					if (data.stack != null) {
						copy.add(data);
					}
				}
				for (ItemStackData data : copy) {
					if (data.interPos != null && data.interPos.equals(pos) && data.interDim == dim) {
						if (data.index == index) {
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
		} else {
			ArrayList<ItemStackData> copy = new ArrayList<ItemStackData>();
			for (ItemStackData data : network.items) {
				if (data.stack != null) {
					copy.add(data);
				}
			}
			for (ItemStackData data : copy) {
				if (data.interPos != null && data.interPos.equals(pos) && data.interDim == dim) {
					network.items.remove(data);
					changed.add(data);
				}
			}
		}
		if (!changed.isEmpty()) {
			for (INetworkContainer panel : network.watchers) {
				panel.sendItemsToAll(changed);
			}
		}
	}

	@Override
	public int insertItem(EStorageNetwork network, ItemStack stack, boolean matching, boolean sim, boolean update) {
		ItemStack hdd = inter.getStackInSlot(0);
		if(matching){
			if (hdd != null	&& hdd.getItem() instanceof ItemHDD) {
				boolean hasItem = ItemHDD.hasItem(hdd, stack);
				if (hasItem) {
					int index = ItemHDD.getItemIndex(hdd, stack);
					if (index > -1) {
						ItemStack stored = ItemHDD.getItem(hdd, index);
						if (ItemUtil.canCombine(stored, stack)) {
							if (sim){
								return stack.stackSize;
							}
							
							/*Iterator<IInsertListener> iter = network.listeners.iterator();
							while (iter.hasNext()) {
								iter.next().onItemInserted(stack);
							}*/
							if(update){
								network.notifyInsert(stack);
							}

							stored.stackSize += stack.stackSize;
							ItemHDD.setItem(hdd, index, stored);
							updateItems(network, index);
							inter.markDirty();
							return stack.stackSize;
						}
					}
				}
			}
		}else{
			if (hdd != null	&& hdd.getItem() instanceof ItemHDD) {
				int index = ItemHDD.getEmptyIndex(hdd);
				if (index > -1) {
					if (sim){
						return stack.stackSize;
					}
					network.notifyInsert(stack);
					ItemHDD.setItem(hdd, index, stack);
					updateItems(network, index);
					inter.markDirty();
					return stack.stackSize;
				}
			}
		}
		return 0;
	}

	@Override
	public int extractItem(EStorageNetwork network, ItemStack stack, int amount, boolean sim, boolean update) {
		ItemStack hdd = inter.getStackInSlot(0);
		if (hdd != null	&& hdd.getItem() instanceof ItemHDD) {
			boolean hasItem = ItemHDD.hasItem(hdd, stack);
			if (hasItem) {
				int index = ItemHDD.getItemIndex(hdd, stack);
				if (index > -1) {
					ItemStack stored = ItemHDD.getItem(hdd, index);
					int realCount = Math.min(amount, stored.stackSize);
					if (sim) {
						return realCount;
					}
					stored.stackSize -= realCount;
					if (stored.stackSize <= 0) {
						stored = null;
					}
					ItemHDD.setItem(hdd, index,	stored);
					updateItems(network, index);
					inter.markDirty();
					if(stack !=null && update){
						Iterator<IInsertListener> iter = network.listeners.iterator();
						while (iter.hasNext()) {
							iter.next().onItemExtracted(stack, realCount);
						}
					}
					return realCount;
				}
			}
		}
		return -1;
	}

}
