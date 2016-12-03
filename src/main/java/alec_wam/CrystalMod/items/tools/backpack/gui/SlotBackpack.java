package alec_wam.CrystalMod.items.tools.backpack.gui;

import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackBase;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackNormal;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackNormal.CrystalBackpackType;
import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotBackpack extends Slot {

	public SlotBackpack(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}
	
	public boolean isItemValid(ItemStack stack){
		if(!ItemStackTools.isValid(stack))return false;
		boolean isBackpack = (stack.getItem() instanceof ItemBackpackBase);
		if(isBackpack && this.inventory !=null){
			if(this.inventory instanceof InventoryBackpack){
				InventoryBackpack inv = (InventoryBackpack)inventory;
				ItemStack masterBackpack = inv.getBackpack();
				if(ItemStackTools.isValid(masterBackpack) && masterBackpack.getItem() instanceof ItemBackpackNormal){
					CrystalBackpackType type = CrystalBackpackType.byMetadata(masterBackpack.getMetadata());
					if(type == CrystalBackpackType.PURE){
						if(stack.getItem() instanceof ItemBackpackNormal){
							CrystalBackpackType type2 = CrystalBackpackType.byMetadata(stack.getMetadata());
							return type2 !=CrystalBackpackType.PURE;
						}
						return true;
					}
				}
			}
		}
		
		return !isBackpack;
	}

}
