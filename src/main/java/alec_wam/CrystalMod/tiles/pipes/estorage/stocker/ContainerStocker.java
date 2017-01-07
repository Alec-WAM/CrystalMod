package alec_wam.CrystalMod.tiles.pipes.estorage.stocker;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerStocker extends Container {

	public ContainerStocker(InventoryPlayer inventory, TileEntityStocker stocker){
		for(int i = 0; i < 5; i++){
			addSlotToContainer(new Slot(stocker, i, 8, 19 + i * 18));
		}
		
		for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 126 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++)
        {
            addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 184));
        }
	}
	
	@Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStackTools.getEmptyStack();

        Slot slot = getSlot(index);

        if (slot != null && slot.getHasStack()) {
            stack = slot.getStack().copy();

            if (!mergeItemStack(stack, 5, inventorySlots.size(), true)) {
                return ItemStackTools.getEmptyStack();
            }

            if (ItemStackTools.isEmpty(stack)) {
                slot.putStack(ItemStackTools.getEmptyStack());
            } else {
                slot.onSlotChanged();
            }
        }

        return stack;
    }
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

}
