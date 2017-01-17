package alec_wam.CrystalMod.tiles.pipes.estorage.stocker;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotGhost extends Slot {

    public SlotGhost(IInventory handler, int id, int x, int y) {
        super(handler, id, x, y);
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
        if (ItemStackTools.isValid(stack)) {
        	ItemStackTools.setStackSize(stack, 1);
        }

        super.putStack(stack);
    }
}
