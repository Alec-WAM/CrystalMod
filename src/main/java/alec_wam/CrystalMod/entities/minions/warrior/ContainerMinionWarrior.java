package alec_wam.CrystalMod.entities.minions.warrior;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerMinionWarrior extends Container {
	private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
    
	public ContainerMinionWarrior(EntityPlayer player, EntityMinionWarrior warrior){
		IInventory playerInventory = player.inventory;
		this.addSlotToContainer(new Slot(warrior.inventory, 0, 98, 62) {
			@Override
			public boolean isItemValid(ItemStack stack)
            {
                return ToolUtil.isSword(stack);
            }
			
			@Nullable
            @SideOnly(Side.CLIENT)
            public String getSlotTexture()
            {
                return "crystalmod:items/icon_sword";
            }
		});
		
		this.addSlotToContainer(new Slot(warrior.inventory, 1, 116, 62) {
			@Override
			public boolean isItemValid(ItemStack stack)
            {
                return ItemStackTools.isValid(stack) && stack.getItem() instanceof ItemBow;
            }
			
			@Nullable
            @SideOnly(Side.CLIENT)
            public String getSlotTexture()
            {
                return "crystalmod:items/icon_bow";
            }
		});
		
		this.addSlotToContainer(new Slot(warrior.inventory, 2, 134, 62) {
			@Override
			public boolean isItemValid(ItemStack stack)
            {
                return ItemStackTools.isValid(stack) && stack.getItem() instanceof ItemFood;
            }
		});
		
		for (int k = 0; k < 4; ++k)
        {
            final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[k];
            this.addSlotToContainer(new Slot(warrior.armorInventory, (3 - k), 8, 8 + k * 18)
            {
                /**
                 * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1
                 * in the case of armor slots)
                 */
                public int getSlotStackLimit()
                {
                    return 1;
                }
                /**
                 * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace
                 * fuel.
                 */
                public boolean isItemValid(ItemStack stack)
                {
                    return stack.getItem().isValidArmor(stack, entityequipmentslot, player);
                }
                /**
                 * Return whether this slot's stack can be taken from this slot.
                 */
                public boolean canTakeStack(EntityPlayer playerIn)
                {
                    ItemStack itemstack = this.getStack();
                    return !itemstack.isEmpty() && !playerIn.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.canTakeStack(playerIn);
                }
                @Nullable
                @SideOnly(Side.CLIENT)
                public String getSlotTexture()
                {
                    return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
                }
            });
        }

        for (int l = 0; l < 3; ++l)
        {
            for (int j1 = 0; j1 < 9; ++j1)
            {
                this.addSlotToContainer(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1)
        {
            this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
        }
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);

            /*if(index > 3){
            	if(ToolUtil.isSword(itemstack1)){
            		if (!this.mergeItemStack(itemstack1, 0, 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
            	}
            	if(EntityMinionWarrior.isBow(itemstack1)){
            		if (!this.mergeItemStack(itemstack1, 1, 2, false))
                    {
                        return ItemStack.EMPTY;
                    }
            	}
            	if(itemstack1.getItem() instanceof ItemFood){
            		if (!this.mergeItemStack(itemstack1, 2, 3, false))
                    {
                        return ItemStack.EMPTY;
                    }
            	}
            }*/
            if (index <= 3)
            {
                if (!this.mergeItemStack(itemstack1, 7, 43, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !((Slot)this.inventorySlots.get(6 - entityequipmentslot.getIndex())).getHasStack())
            {
                int i = 6 - entityequipmentslot.getIndex();

                if (!this.mergeItemStack(itemstack1, i, i + 1, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (index >= 6 && index < 33)
            {
            	if (!this.mergeItemStack(itemstack1, 0, 3, false))
                {
	            	if (!this.mergeItemStack(itemstack1, 33, 43, false))
	                {
	                    return ItemStack.EMPTY;
	                }
                }
            }
            else if (index >= 33 && index < 43)
            {
                if (!this.mergeItemStack(itemstack1, 0, 33, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, 43, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }
        }

        return itemstack;
    }
	
}
