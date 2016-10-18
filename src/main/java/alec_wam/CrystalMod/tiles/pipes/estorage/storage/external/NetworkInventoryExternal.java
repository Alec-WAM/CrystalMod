package alec_wam.CrystalMod.tiles.pipes.estorage.storage.external;

import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.IItemHandler;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage.FluidStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.IInsertListener;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.INetworkContainer;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.inventory.INetworkInventory;
import alec_wam.CrystalMod.util.FluidUtil;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ModLogger;

import com.google.common.collect.Lists;

public class NetworkInventoryExternal implements INetworkInventory {

	public TileEntityExternalInterface inter;
	public NetworkInventoryExternal(TileEntityExternalInterface inter){
		this.inter = inter;
	}
	
	public IItemHandler getInventory(){
		EnumFacing face = EnumFacing.getFront(inter.facing);
		return ItemUtil.getExternalItemHandler(inter.getWorld(), inter.getPos().offset(face), face.getOpposite());
	}
	
	public IFluidHandler getTank(){
		EnumFacing face = EnumFacing.getFront(inter.facing);
		return FluidUtil.getExternalFluidHandler(inter.getWorld(), inter.getPos().offset(face), face.getOpposite());
	}
	
	@Override
	public List<ItemStackData> getItems(ItemStorage storage) {
		List<ItemStackData> items = Lists.newArrayList();
		IItemHandler handler = getInventory();
		if (handler !=null) {
			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack stack = getInventory().getStackInSlot(i);
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
		IItemHandler handler = getInventory();
		if (handler !=null) {
			if (index < 0) {
				for (int i = 0; i < handler.getSlots(); i++) {
					int slot = i;
					ItemStack stack = getInventory().getStackInSlot(slot);
					ItemStackData itemData = new ItemStackData(stack, slot, pos, dim);
					if(storage.addToList(itemData)){
						changed.add(itemData);
					}
				}
			} else {
				int slot = index;
				ItemStack stack = getInventory().getStackInSlot(slot);
				ItemStackData itemData = new ItemStackData(stack, slot, pos, dim);
				if(storage.addToList(itemData)){
					changed.add(itemData);
				}
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
		/*if(matching){
			if (getInventory() !=null) {
				int amt = ItemUtil.doInsertItemMatching(getInventory(), stack, EnumFacing.getFront(inter.facing));
				if(amt > 0 && update){
					network.notifyInsert(stack);
				}
				return amt;
			}
		}else{*/
		if (getInventory() !=null) {
			int amt = ItemUtil.doInsertItem(getInventory(), stack, EnumFacing.getFront(inter.facing));
			if(amt > 0 && update){
				network.notifyInsert(stack);
			}
			return amt;
		}
		//}
		return 0;
	}

	@Override
	public int extractItem(EStorageNetwork network, ItemStack stack, int amount, boolean sim, boolean update) {
		IItemHandler handler = getInventory();
		if (handler !=null) {
			/*for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack invStack = getInventory().getStackInSlot(i);
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
					
					getInventory().setInventorySlotContents(i, invStack);
					
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
			}*/
			
			int remaining = amount;

	        ItemStack received = null;

	        for (int i = 0; i < handler.getSlots(); ++i) {
	            ItemStack slot = handler.getStackInSlot(i);

	            if (slot != null && ItemUtil.canCombine(slot, stack)) {
	                ItemStack got = handler.extractItem(i, remaining, sim);

	                if (got != null) {
	                    if (received == null) {
	                        received = got;
	                    } else {
	                        received.stackSize += got.stackSize;
	                    }

	                    remaining -= got.stackSize;

	                    if (remaining == 0) {
	                        break;
	                    }
	                }
	            }
	        }
	        if(received !=null && update){
				Iterator<IInsertListener> iter = network.listeners.iterator();
				while (iter.hasNext()) {
					iter.next().onItemExtracted(received, received.stackSize);
				}
			}
	        return received == null ? 0 : received.stackSize;
		}
		return 0;
	}

	@Override
	public List<FluidStackData> getFluids(FluidStorage storage) {
		List<FluidStackData> fluids = Lists.newArrayList();
		IFluidHandler handler = getTank();
		if (handler !=null) {
			IFluidTankProperties prop = handler.getTankProperties()[0];
			if (prop != null) {
				FluidStack stack = prop.getContents();
				if(stack !=null && storage.getFluidData(stack) == null){
					FluidStackData data = new FluidStackData(stack, 0, inter.getPos(), inter.getWorld().provider.getDimension());
					fluids.add(data);
				}
			}
		}
		return fluids;
	}

	@Override
	public int insertFluid(EStorageNetwork network, FluidStack stack, boolean matching, boolean sim, boolean sendUpdate) {
		IFluidHandler handler = getTank();
		if (handler !=null) {
			int ret = 0;
			if(matching){
				IFluidTankProperties prop = handler.getTankProperties()[0];
				if (prop != null) {
					FluidStack internalStack = prop.getContents();
					if(internalStack !=null){
						if(FluidUtil.canCombine(stack, internalStack)){
							ret = handler.fill(stack, !sim);
						}
					}
				}
			} else {
				ret = handler.fill(stack, !sim);
			}
			if(ret > 0 && sendUpdate){
				FluidStack copy = stack.copy();
				copy.amount = ret;
				Iterator<IInsertListener> iter = network.listeners.iterator();
				while (iter.hasNext()) {
					iter.next().onFluidInserted(copy);
				}
			}
			return ret;
		}
		return 0;
	}

	@Override
	public int extractFluid(EStorageNetwork network, FluidStack stack, int amount, boolean sim, boolean sendUpdate) {
		IFluidHandler handler = getTank();
		if (handler !=null) {
			FluidStack copy = stack.copy();
			copy.amount = amount;
	        FluidStack received = handler.drain(copy, !sim);
	        if(received !=null && sendUpdate){
				Iterator<IInsertListener> iter = network.listeners.iterator();
				while (iter.hasNext()) {
					iter.next().onFluidExtracted(received, received.amount);
				}
			}
	        return received == null ? 0 : received.amount;
		}
		return 0;
	}

	@Override
	public void updateFluids(EStorageNetwork network, int index) {
		FluidStorage storage = network.getFluidStorage();
		List<FluidStackData> changed = Lists.newArrayList();
		BlockPos pos = inter.getPos();
		int dim = inter.getWorld().provider.getDimension();
		IFluidHandler handler = getTank();
		if (handler !=null) {
			if (index < 0) {
				IFluidTankProperties prop = handler.getTankProperties()[0];
				if (prop != null) {
					FluidStack internalStack = prop.getContents();
					if(internalStack !=null){
						FluidStackData fluidData = new FluidStackData(internalStack, 0, pos, dim);
						if(storage.addToList(fluidData)){
							changed.add(fluidData);
						}
					}
				}
			} else {
				IFluidTankProperties prop = handler.getTankProperties()[0];
				if (prop != null) {
					FluidStack internalStack = prop.getContents();
					if(internalStack !=null){
						FluidStackData fluidData = new FluidStackData(internalStack, 0, pos, dim);
						if(storage.addToList(fluidData)){
							changed.add(fluidData);
						}
					}
				}
			}
		}else{
			changed.addAll(storage.clearListAtPos(pos, dim));
		}
		if (!changed.isEmpty()) {
			for (INetworkContainer panel : network.watchers) {
				panel.sendFluidsToAll(changed);
			}
		}
	}

}
