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
			if (hddStack != null && hddStack.getItem() instanceof ItemHDD) {
				for (int i = 0; i < ItemHDD.getItemLimit(hddStack); i++) {
					ItemStack stack = ItemHDD.getItem(hddStack, i);
					if (stack != null && stack.stackSize > 0) {
						list.add(stack);
					}
				}
			}
		}
		return list;
	}
	
	public void getItems(List<ItemStackData> items, ItemStack hddStack, ItemStorage storage){
		if (hddStack != null && hddStack.getItem() instanceof ItemHDD) {
			for (int i = 0; i < ItemHDD.getItemLimit(hddStack); i++) {
				ItemStack stack = ItemHDD.getItem(hddStack, i);
				if (stack != null && stack.stackSize > 0) {
					if(storage.getItemData(stack) == null && !contains(items, stack)){
						ItemStackData data = new ItemStackData(stack);
						items.add(data);
					}
				}
			}
		}
	}
	
	public boolean contains(List<ItemStackData> list, ItemStack stack){
		Iterator<ItemStackData> ii = list.iterator();
		while(ii.hasNext()){
			ItemStackData data = ii.next();
			if(data.stack !=null){
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
			if(hdd !=null && hdd.getItem() instanceof IItemProvider){
				IItemProvider provider = (IItemProvider)hdd.getItem();
				final int preSize = remaining.stackSize;
				remaining = provider.insert(hdd, remaining, remaining.stackSize, sim);
				
				if(!sim && (remaining == null || remaining.stackSize !=preSize)){
					array.markDirty();
					BlockUtil.markBlockForUpdate(array.getWorld(), array.getPos());
				}
				
				if (remaining == null || remaining.stackSize < 0) {
	                break;
				}
			}
		}
		return remaining;
	}

	@Override
	public ItemStack extractItem(EStorageNetwork network, ItemStack stack, int amount, ExtractFilter filter, boolean sim) {
		int needed = amount;
		ItemStack received = null;
		search : for(int i = 0; i < array.getSizeInventory(); i++){
			ItemStack hdd = array.getStackInSlot(i);
			if (hdd != null	&& hdd.getItem() instanceof IItemProvider) {
				IItemProvider provider = ((IItemProvider)hdd.getItem());
				ItemStack took = provider.extract(hdd, stack, needed, filter, sim);
				if(took != null){
					if(received == null){
						received = took;
					} else {
						received.stackSize+=took.stackSize;
					}
					
					if(!sim){
						array.markDirty();
						BlockUtil.markBlockForUpdate(array.getWorld(), array.getPos());
					}
					needed-=took.stackSize;
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
