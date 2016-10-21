package alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd;

import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import alec_wam.CrystalMod.api.estorage.INetworkInventory;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage.FluidStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.IInsertListener;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.INetworkContainer;
import alec_wam.CrystalMod.util.ItemUtil;

import com.google.common.collect.Lists;

public class NetworkInventoryHDDInterface implements INetworkInventory {

	public TileEntityHDDInterface inter;
	public NetworkInventoryHDDInterface(TileEntityHDDInterface inter){
		this.inter = inter;
	}
	
	@Override
	public List<ItemStackData> getItems(ItemStorage storage) {
		List<ItemStackData> items = Lists.newArrayList();
		ItemStack hddStack = inter.getStackInSlot(0);
		if (hddStack != null && hddStack.getItem() instanceof ItemHDD) {
			for (int i = 0; i < ItemHDD.getItemLimit(hddStack); i++) {
				ItemStack stack = ItemHDD.getItem(hddStack, i);
				if (stack != null && stack.stackSize > 0) {
					if(storage.getItemData(stack) == null){
						ItemStackData data = new ItemStackData(stack, inter.getPos(), inter.getWorld().provider.getDimension());
						items.add(data);
					}
				}
			}
		}
		return items;
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
							
							if(update){
								network.notifyInsert(stack);
							}

							stored.stackSize += stack.stackSize;
							ItemHDD.setItem(hdd, index, stored);
							network.getItemStorage().invalidate();
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
					network.getItemStorage().invalidate();
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
					inter.markDirty();
					network.getItemStorage().invalidate();
					
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

	//TODO Add Fluid HDD
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

}
