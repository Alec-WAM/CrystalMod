package alec_wam.CrystalMod.api.estorage.storage;

import alec_wam.CrystalMod.api.estorage.INetworkInventory.ExtractFilter;
import net.minecraft.item.ItemStack;

public interface IItemProvider {

	public ItemStack insert(ItemStack container, ItemStack insert, int amount, boolean sim);
	
	public ItemStack extract(ItemStack hddStack, ItemStack remove, int amount, ExtractFilter filter, boolean sim);
	
}
