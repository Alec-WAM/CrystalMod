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
import net.minecraftforge.items.ItemHandlerHelper;
import alec_wam.CrystalMod.api.FluidStackList;
import alec_wam.CrystalMod.api.ItemStackList;
import alec_wam.CrystalMod.api.estorage.IInsertListener;
import alec_wam.CrystalMod.api.estorage.INetworkContainer;
import alec_wam.CrystalMod.api.estorage.INetworkInventory;
import alec_wam.CrystalMod.api.estorage.INetworkInventory.FluidExtractFilter;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage.FluidStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
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
	public ItemStackList getItems() {
		ItemStackList list = new ItemStackList();
		IItemHandler handler = getInventory();
		if (handler !=null) {
			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack stack = getInventory().getStackInSlot(i);
				if (stack != null && stack.stackSize > 0) {
					list.add(stack);
				}
			}
		}
		return list;
	}
	
	@Override
	public ItemStack insertItem(EStorageNetwork network, ItemStack stack, int amount, boolean sim) {
		if (getInventory() !=null) {
			return ItemHandlerHelper.insertItem(getInventory(), ItemHandlerHelper.copyStackWithSize(stack, amount), sim);
		}
		return ItemHandlerHelper.copyStackWithSize(stack, amount);
	}

	@Override
	public ItemStack extractItem(EStorageNetwork network, ItemStack stack, int amount, ExtractFilter filter, boolean sim) {
		IItemHandler handler = getInventory();
		if (handler !=null) {
			final int needed = amount;
			int remaining = amount;

	        ItemStack received = null;

	        for (int i = 0; i < handler.getSlots(); ++i) {
	            ItemStack slot = handler.getStackInSlot(i);

	            if (slot != null && filter.canExtract(stack, slot)) {
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
	        return received;
		}
		return null;
	}

	@Override
	public FluidStackList getFluids() {
		FluidStackList fluids = new FluidStackList();
		IFluidHandler handler = getTank();
		if (handler !=null) {
			IFluidTankProperties prop = handler.getTankProperties()[0];
			if (prop != null) {
				FluidStack stack = prop.getContents();
				if(stack !=null && stack.amount > 0){
					fluids.add(stack);
				}
			}
		}
		return fluids;
	}

	@Override
	public int insertFluid(EStorageNetwork network, FluidStack stack, boolean matching, boolean sim) {
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
			if(ret > 0){
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
	public FluidStack extractFluid(EStorageNetwork network, FluidStack stack, int amount, FluidExtractFilter filter, boolean sim) {
		IFluidHandler handler = getTank();
		if (handler !=null) {
			FluidStack copy = stack.copy();
			copy.amount = amount;
	        FluidStack received = handler.drain(copy, !sim);
	        if(received !=null){
				Iterator<IInsertListener> iter = network.listeners.iterator();
				while (iter.hasNext()) {
					iter.next().onFluidExtracted(received, received.amount);
				}
			}
	        return received;
		}
		return null;
	}

}
