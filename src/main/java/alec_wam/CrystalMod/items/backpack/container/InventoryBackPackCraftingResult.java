package alec_wam.CrystalMod.items.backpack.container;

import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import alec_wam.CrystalMod.items.backpack.InventoryBackpackModular;

public class InventoryBackPackCraftingResult extends InventoryCraftResult {

	private InventoryBackpackModular inventory;
	private int offset;
	
	public InventoryBackPackCraftingResult(InventoryBackpackModular inv, int offset){
		this.inventory = inv;
		this.offset = offset;
	}
	
	public ItemStack getStackInSlot(int index)
    {
        return inventory.getStackInSlot(0 + offset);
    }
	
	@Override
    public void setInventorySlotContents (int par1, ItemStack par2ItemStack)
    {
		inventory.setStackInSlot(0 + offset, par2ItemStack);
    }
	
	@Override
    public ItemStack decrStackSize (int par1, int par2)
    {
        ItemStack stack = inventory.getStackInSlot(0 + offset);
        if (stack != null)
        {
            ItemStack itemstack = stack;
            inventory.setStackInSlot(0 + offset, null);
            return itemstack;
        }
        else
        {
            return null;
        }
    }
	
}
