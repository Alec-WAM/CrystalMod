package alec_wam.CrystalMod.items.tools.backpack.upgrade;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.IBackpack;
import alec_wam.CrystalMod.items.tools.backpack.IBackpackInventory;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackBase;
import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.items.tools.backpack.types.NormalInventoryBackpack;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ItemBackpackUpgrade.BackpackUpgrade;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBackpackUpgrades extends Container
{
    private final InventoryBackpackUpgrades upgradeInventory;

    public ContainerBackpackUpgrades(InventoryPlayer playerInventory, InventoryBackpackUpgrades upgradeInventory)
    {
        this.upgradeInventory = upgradeInventory;
        int i = 51;

        int slotPos = 44+(9*(5-upgradeInventory.getSize()));
        for (int j = 0; j < upgradeInventory.getSize(); ++j)
        {
            this.addSlotToContainer(new Slot(upgradeInventory, j, slotPos + j * 18, 20));
        }

        for (int l = 0; l < 3; ++l)
        {
            for (int k = 0; k < 9; ++k)
            {
                this.addSlotToContainer(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, l * 18 + 51));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1)
        {
            this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 109));
        }
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.upgradeInventory.isUsableByPlayer(playerIn);
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    @Nullable
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStackTools.getEmptyStack();
        Slot slot = (Slot)this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < this.upgradeInventory.getSizeInventory())
            {
                if (!this.mergeItemStack(itemstack1, this.upgradeInventory.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return ItemStackTools.getEmptyStack();
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, this.upgradeInventory.getSizeInventory(), false))
            {
                return ItemStackTools.getEmptyStack();
            }

            if (ItemStackTools.isEmpty(itemstack1))
            {
                slot.putStack(ItemStackTools.getEmptyStack());
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
    	if(slotId >=0){
    		ItemStack backpack = this.upgradeInventory.getBackpack();
    		if(ItemStackTools.isValid(backpack)){
    			IBackpack type = BackpackUtil.getType(backpack);
    			if(type !=null){
    				Slot slot = getSlot(slotId);
    				ItemStack stack = slot.getStack();
    				if(ItemStackTools.isValid(stack)){
    					if(stack.getItem() instanceof ItemBackpackUpgrade){
    						if(stack.getMetadata() == BackpackUpgrade.POCKETS.getMetadata()){
    							if(type instanceof IBackpackInventory){
    			    				InventoryBackpack inv = ((IBackpackInventory)type).getInventory(backpack);
    			    				if(inv !=null && inv instanceof NormalInventoryBackpack){
    			    					NormalInventoryBackpack normalInv = (NormalInventoryBackpack)inv;
    			    					if(ItemStackTools.isValid(normalInv.getToolStack(0)) || ItemStackTools.isValid(normalInv.getToolStack(1))){
    			    						return null;
    			    					}
    			    				}
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    	return super.slotClick(slotId, dragType, clickTypeIn, player);
    }
    
    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
        upgradeInventory.guiSaveSafe(playerIn);
    }
}