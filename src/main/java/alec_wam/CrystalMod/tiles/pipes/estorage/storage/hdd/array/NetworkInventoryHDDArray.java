package alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.array;

import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import alec_wam.CrystalMod.api.FluidStackList;
import alec_wam.CrystalMod.api.ItemStackList;
import alec_wam.CrystalMod.api.estorage.IInsertListener;
import alec_wam.CrystalMod.api.estorage.INetworkInventory;
import alec_wam.CrystalMod.api.estorage.INetworkInventory.ExtractFilter;
import alec_wam.CrystalMod.api.estorage.storage.IItemProvider;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.ItemHDD;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.NetworkInventoryHDDInterface;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;

public class NetworkInventoryHDDArray implements INetworkInventory {

	public TileHDDArray array;
	public NetworkInventoryHDDArray(TileHDDArray array){
		this.array = array;
	}
	
	@Override
	public ItemStackList getItems() {
		ItemStackList list = new ItemStackList();
		for(int s = 0; s < array.getSizeInventory(); s++){
			ItemStack hddStack = array.getStackInSlot(s);
			if (!ItemStackTools.isNullStack(hddStack) && hddStack.getItem() instanceof ItemHDD) {
				for (int i = 0; i < ItemHDD.getItemLimit(hddStack); i++) {
					ItemStack stack = ItemHDD.getItem(hddStack, i);
					if (ItemStackTools.isValid(stack)) {
						list.add(stack);
					}
				}
			}
		}
		return list;
	}
	
	public boolean contains(List<ItemStackData> list, ItemStack stack){
		Iterator<ItemStackData> ii = list.iterator();
		while(ii.hasNext()){
			ItemStackData data = ii.next();
			if(!ItemStackTools.isNullStack(data.stack)){
				if(ItemUtil.canCombine(stack, data.stack)){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public ItemStack insertItem(EStorageNetwork network, ItemStack stack, int amount, boolean sim) {
		ItemStack remaining = ItemHandlerHelper.copyStackWithSize(stack, amount);
		for(int i = 0; i < array.getSizeInventory(); i++){
			ItemStack hdd = array.getStackInSlot(i);
			if(!ItemStackTools.isNullStack(hdd) && hdd.getItem() instanceof IItemProvider){
				IItemProvider provider = (IItemProvider)hdd.getItem();
				final int preSize = ItemStackTools.getStackSize(remaining);
				remaining = provider.insert(hdd, remaining, ItemStackTools.getStackSize(remaining), sim);
				
				if(!sim && (ItemStackTools.getStackSize(remaining) !=preSize)){
					array.markDirty();
					BlockUtil.markBlockForUpdate(array.getWorld(), array.getPos());
				}
				
				if (!ItemStackTools.isValid(remaining)) {
	                break;
				}
			}
		}
		return remaining;
	}

	@Override
	public ItemStack extractItem(EStorageNetwork network, ItemStack stack, int amount, ExtractFilter filter, boolean sim) {
		int needed = amount;
		ItemStack received = ItemStackTools.getEmptyStack();
		search : for(int i = 0; i < array.getSizeInventory(); i++){
			ItemStack hdd = array.getStackInSlot(i);
			if (!ItemStackTools.isNullStack(hdd) && hdd.getItem() instanceof IItemProvider) {
				IItemProvider provider = ((IItemProvider)hdd.getItem());
				ItemStack took = provider.extract(hdd, stack, needed, filter, sim);
				if(!ItemStackTools.isNullStack(took)){
					if(ItemStackTools.isNullStack(received)){
						received = took;
					} else {
						ItemStackTools.incStackSize(received, ItemStackTools.getStackSize(took));
					}
					
					if(!sim){
						array.markDirty();
						BlockUtil.markBlockForUpdate(array.getWorld(), array.getPos());
					}
					needed-=ItemStackTools.getStackSize(took);
					if(needed <= 0){
						break search;
					}
				}
			}
		}
		return received;
	}

	@Override
	public FluidStackList getFluids() {
		return null;
	}

	@Override
	public int insertFluid(EStorageNetwork network, FluidStack stack, boolean matching, boolean sim) {
		return 0;
	}

	@Override
	public FluidStack extractFluid(EStorageNetwork network, FluidStack stack, int amount, FluidExtractFilter filter, boolean sim) {
		return null;
	}

}
