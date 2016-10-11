package alec_wam.CrystalMod.items.backpack.container;

import alec_wam.CrystalMod.items.backpack.InventoryBackpackModular;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

public class InventoryCraftingBackpack extends InventoryCrafting {

	private InventoryBackpackModular inventory;
	private Container eventHandler;
	private int offset;
	
	public InventoryCraftingBackpack(Container eventHandlerIn, int width, int height, InventoryBackpackModular inventory, int offset) {
		super(eventHandlerIn, width, height);
		this.inventory = inventory;
		eventHandler = eventHandlerIn;
		this.offset = offset;
	}
	
	@Override
    public ItemStack getStackInSlot (int slot)
    {
        // the 9 slots + 1 output slot that's not accessible, we therefore have to add 1 to the slot accessed
        return slot >= this.getSizeInventory() ? null : inventory.getStackInSlot(slot+1+offset);
    }
	
	@Override
    public ItemStack decrStackSize (int slotID, int par2)
    {
        ItemStack stack = inventory.getStackInSlot(slotID + 1 + offset);
        if (stack != null)
        {
            ItemStack itemstack;

            if (stack.stackSize <= par2)
            {
                itemstack = stack.copy();
                stack = null;
                inventory.setStackInSlot(slotID + 1 + offset, null);
                this.eventHandler.onCraftMatrixChanged(this);
                return itemstack;
            }
            else
            {
                itemstack = stack.splitStack(par2);

                if (stack.stackSize == 0)
                {
                    stack = null;
                }

                this.eventHandler.onCraftMatrixChanged(this);
                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }
	
	@Override
    public void setInventorySlotContents (int slot, ItemStack itemstack)
    {
        inventory.setStackInSlot(slot + 1 + offset, itemstack);
        this.eventHandler.onCraftMatrixChanged(this);
    }

}
