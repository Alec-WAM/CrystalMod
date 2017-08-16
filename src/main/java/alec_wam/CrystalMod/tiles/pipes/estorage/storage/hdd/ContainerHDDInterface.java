package alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerHDDInterface extends Container {

	public ContainerHDDInterface(InventoryPlayer inventoryPlayer, TileEntityHDDInterface inter) {
		this.addSlotToContainer(new Slot(inter, 0, 8, 14){
			@Override
			public boolean isItemValid(ItemStack stack)
		    {
		        return !ItemStackTools.isNullStack(stack) && stack.getItem() instanceof ItemHDD;
		    }
		});
		this.addSlotToContainer(new Slot(inter, 1, 8, 57));
		
		for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++)
        {
            addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStackTools.getEmptyStack();
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 0)
            {
                if (!this.mergeItemStack(itemstack1, 2, 38, true))
                {
                    return ItemStackTools.getEmptyStack();
                }
            }
            else if (itemstack1 !=null && itemstack1.getItem() instanceof ItemHDD)
            {
            	if(!this.mergeItemStack(itemstack1, 0, 1, false))
                return ItemStackTools.getEmptyStack();
            	
            	slot.onSlotChange(itemstack1, itemstack);
            }
            else if (!this.mergeItemStack(itemstack1, 1, 2, false))
            {
                return ItemStackTools.getEmptyStack();
            }

            if (ItemStackTools.isEmpty(itemstack1))
            {
                slot.putStack(ItemStackTools.getEmptyStack());
            }
            else
            {
                slot.onSlotChanged();
            }

            if (ItemStackTools.getStackSize(itemstack1) == ItemStackTools.getStackSize(itemstack))
            {
                return ItemStackTools.getEmptyStack();
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

}
