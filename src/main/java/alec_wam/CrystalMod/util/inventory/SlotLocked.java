package alec_wam.CrystalMod.util.inventory;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotLocked extends Slot {

	public SlotLocked(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}
	
	@Override
    public boolean isItemValid(ItemStack stack){
        return false;
    }

    @Override
    public void putStack(ItemStack stack){

    }


    @Override
    public ItemStack decrStackSize(int i){
        return ItemStackTools.getEmptyStack();
    }

    @Override
    public boolean canTakeStack(EntityPlayer player){
        return false;
    }

}
