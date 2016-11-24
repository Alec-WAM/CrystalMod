package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCrafter extends Container {

	public ContainerCrafter(EntityPlayer player, TileCrafter crafter){
		for (int i = 0; i < crafter.getPatterns().getSlots(); ++i) {
			if(i >= 8){
				addSlotToContainer(new SlotItemHandler(crafter.getPatterns(), i, ((i-8) * 18)+17, 40));
			}else
            addSlotToContainer(new SlotItemHandler(crafter.getPatterns(), i, (i * 18)+17, 22));
        }
		
		for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++)
        {
            addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));
        }
	}
	
	@Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStackTools.getEmptyStack();

        Slot slot = getSlot(index);

        if (slot != null && slot.getHasStack()) {
            stack = slot.getStack().copy();

            if (index < 16) {
                if (!mergeItemStack(stack, 16, inventorySlots.size(), true)) {
                    return ItemStackTools.getEmptyStack();
                }
            } else if (!mergeItemStack(stack, 0, 16, false)) {
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
