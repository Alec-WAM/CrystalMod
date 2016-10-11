package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotDisabled extends Slot {
    public SlotDisabled(IInventory inventory, int id, int x, int y) {
        super(inventory, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }
}