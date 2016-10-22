package alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.array;

import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.ItemHDD;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerHDDArray extends Container {

	public ContainerHDDArray(InventoryPlayer inventoryPlayer, TileHDDArray array) {
		for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 2; j++)
            {
				this.addSlotToContainer(new Slot(array, j+(i*2), 71+(j*18), 7+(i*18)){
					public boolean isItemValid(ItemStack stack)
				    {
				        return stack !=null && stack.getItem() instanceof ItemHDD;
				    }
				});
            }
        }
		
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

            if (index < 8)
            {
                if (!this.mergeItemStack(itemstack1, 8, inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (itemstack1 !=null && itemstack1.getItem() instanceof ItemHDD)
            {
            	if(!this.mergeItemStack(itemstack1, 0, 8, false))
                return null;
            	
            	slot.onSlotChange(itemstack1, itemstack);
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
