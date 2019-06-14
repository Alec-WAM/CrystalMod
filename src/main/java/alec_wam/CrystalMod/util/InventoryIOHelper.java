package alec_wam.CrystalMod.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class InventoryIOHelper {

	public static boolean isFull(IItemHandler itemHandler)
    {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++)
        {
            ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (stackInSlot.isEmpty() || stackInSlot.getCount() != stackInSlot.getMaxStackSize())
            {
                return false;
            }
        }
        return true;
    }
}
