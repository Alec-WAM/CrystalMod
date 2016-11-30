package alec_wam.CrystalMod.items.tools.backpack.gui;

import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotBackpack extends Slot {

	public SlotBackpack(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}
	
	public boolean isItemValid(ItemStack stack){
		return !(stack.getItem() instanceof ItemBackpackBase);
	}

}
