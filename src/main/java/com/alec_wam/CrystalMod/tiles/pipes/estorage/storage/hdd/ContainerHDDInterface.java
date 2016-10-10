package com.alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerHDDInterface extends Container {

	public ContainerHDDInterface(InventoryPlayer inventoryPlayer, TileEntityHDDInterface inter) {
		this.addSlotToContainer(new Slot(inter, 0, 8, 14){
			public boolean isItemValid(ItemStack stack)
		    {
		        return stack !=null && stack.getItem() instanceof ItemHDD;
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
	
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 0)
            {
                if (!this.mergeItemStack(itemstack1, 2, 38, true))
                {
                    return null;
                }
            }
            else if (itemstack1 !=null && itemstack1.getItem() instanceof ItemHDD)
            {
            	if(!this.mergeItemStack(itemstack1, 0, 1, false))
                return null;
            	
            	slot.onSlotChange(itemstack1, itemstack);
            }
            else if (!this.mergeItemStack(itemstack1, 1, 2, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(playerIn, itemstack1);
        }

        return itemstack;
    }

}
