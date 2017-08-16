package alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd;

import alec_wam.CrystalMod.api.FluidStackList;
import alec_wam.CrystalMod.api.ItemStackList;
import alec_wam.CrystalMod.api.estorage.INetworkInventory;
import alec_wam.CrystalMod.api.estorage.storage.IItemProvider;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class NetworkInventoryHDDInterface implements INetworkInventory {

	public TileEntityHDDInterface inter;
	public NetworkInventoryHDDInterface(TileEntityHDDInterface inter){
		this.inter = inter;
	}
	
	@Override
	public ItemStackList getItems() {
		ItemStack hddStack = inter.getStackInSlot(0);
		ItemStackList list = new ItemStackList();
		if (!ItemStackTools.isNullStack(hddStack) && hddStack.getItem() instanceof ItemHDD) {
			for (int i = 0; i < ItemHDD.getItemLimit(hddStack); i++) {
				ItemStack stack = ItemHDD.getItem(hddStack, i);
				if (ItemStackTools.isValid(stack)) {
					list.add(stack);
				}
			}
		}
		return list;
	}

	@Override
	public ItemStack insertItem(EStorageNetwork network, ItemStack stack, int amount, boolean sim) {
		ItemStack remaining = ItemHandlerHelper.copyStackWithSize(stack, amount);
		ItemStack hdd = inter.getStackInSlot(0);
		if(!ItemStackTools.isNullStack(hdd) && hdd.getItem() instanceof IItemProvider){
			IItemProvider provider = (IItemProvider)hdd.getItem();
			final int preSize = ItemStackTools.getStackSize(remaining);
			remaining = provider.insert(hdd, remaining, ItemStackTools.getStackSize(remaining), sim);
			
			if(!sim && (ItemStackTools.getStackSize(remaining) !=preSize)){
				inter.markDirty();
			}
		}
		return remaining;
	}

	@Override
	public ItemStack extractItem(EStorageNetwork network, ItemStack stack, int amount, ExtractFilter filter, boolean sim) {
		ItemStack received = ItemStackTools.getEmptyStack();
		ItemStack hdd = inter.getStackInSlot(0);
		if (!ItemStackTools.isNullStack(hdd) && hdd.getItem() instanceof IItemProvider) {
			IItemProvider provider = ((IItemProvider)hdd.getItem());
			ItemStack took = provider.extract(hdd, stack, amount, filter, sim);
			if(!ItemStackTools.isNullStack(took)){
				received = took;
				
				if(!sim){
					inter.markDirty();
				}
			}
		}
		return received;
	}
	
	public static int getIndex(ItemStack hdd, ItemStack stack, ExtractFilter filter){
		if(!ItemStackTools.isNullStack(hdd)){
			int itemCount = ItemHDD.getItemLimit(hdd);
			for(int i = 0; i < itemCount; i++){
				ItemStack foundStack = ItemHDD.getItem(hdd, i);
				if(!ItemStackTools.isNullStack(foundStack) && filter.canExtract(stack, foundStack)){
					return i;
				}
			}
		}
		return -1;
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
