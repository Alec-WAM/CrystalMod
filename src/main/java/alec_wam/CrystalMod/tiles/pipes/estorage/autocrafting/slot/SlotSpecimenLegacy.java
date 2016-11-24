package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.slot;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotSpecimenLegacy extends Slot {
    public SlotSpecimenLegacy(IInventory inventory, int id, int x, int y, boolean allowSize) {
        super(inventory, id, x, y);
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return false;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return true;
    }

    @Override
    public void putStack(ItemStack stack) {
        if (!ItemStackTools.isNullStack(stack)) {
        	ItemStackTools.setStackSize(stack, 1);
        }

        super.putStack(stack);
    }
}
