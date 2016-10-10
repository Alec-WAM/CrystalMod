package com.alec_wam.CrystalMod.items.backpack;

import com.alec_wam.CrystalMod.util.ItemNBTHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryBackpackModular extends InventoryItemModular{

	public InventoryBackpackModular(ItemStack containerStack, int mainInvSize, boolean allowCustomStackSizes, EntityPlayer player) {
		super(containerStack, mainInvSize, allowCustomStackSizes, player);
	}
	
	public void setStackInSlot(int slot, ItemStack stack){
		boolean flag = stack != null && stack.isItemEqual(this.getStackInSlot(slot)) && ItemStack.areItemStackTagsEqual(stack, this.getStackInSlot(slot));
		super.setStackInSlot(slot, stack);
		
		if (slot == (BackpackUtils.MAIN_SIZE+BackpackUtils.CRAFTING_SIZE) && !flag)
        {
            ItemNBTHelper.setInteger(this.getModularItemStack(), "Furnace.TotalCooktime", 200);
            ItemNBTHelper.setInteger(this.getModularItemStack(), "Furnace.Cooktime", 0);
            this.onContentsChanged(slot);
        }
	}

}
