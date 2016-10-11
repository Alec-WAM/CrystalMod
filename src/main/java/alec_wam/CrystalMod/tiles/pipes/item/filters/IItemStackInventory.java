package alec_wam.CrystalMod.tiles.pipes.item.filters;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IItemStackInventory extends IInventory {

	public void initializeInventory(ItemStack masterStack);

	public boolean canInventoryBeManipulated();

	public boolean hasGhostSlots();
	
}
