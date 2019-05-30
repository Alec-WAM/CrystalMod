package alec_wam.CrystalMod.tiles.energy.battery;

import alec_wam.CrystalMod.tiles.machine.crafting.SlotLockedOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBattery extends Container
{
    public TileEntityBattery battery;

    public ContainerBattery(EntityPlayer player, TileEntityBattery battery)
    {
    	this.battery = (battery);
    	this.addSlot(new Slot(battery, 0, 56, 35) {
    		@Override
    		public boolean isItemValid(ItemStack stack){
    			return battery.canCharge(stack);
    		}
    	});
    	this.addSlot(new SlotLockedOutput(player, battery, 1, 103, 35));
        this.addPlayerInventory(player.inventory);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    protected void addPlayerInventory(InventoryPlayer paramInventoryPlayer)
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                this.addSlot(new Slot(paramInventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(paramInventoryPlayer, i, 8 + i * 18, 142));
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i)
    {
    	ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(i);
        if (slot != null && slot.getHasStack()) {
           ItemStack itemstack1 = slot.getStack();
           itemstack = itemstack1.copy();
           if (i == 1) {
              if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
                 return ItemStack.EMPTY;
              }

              slot.onSlotChange(itemstack1, itemstack);
           } else if (i != 0) {
              if (battery.canCharge(itemstack1)) {
                 if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                 }
              }
              else if (i >= 1 && i < 29) {
                 if (!this.mergeItemStack(itemstack1, 29, 38, false)) {
                    return ItemStack.EMPTY;
                 }
              } else if (i >= 29 && i < 38 && !this.mergeItemStack(itemstack1, 2, 29, false)) {
                 return ItemStack.EMPTY;
              }
           } else if (!this.mergeItemStack(itemstack1, 2, 38, false)) {
              return ItemStack.EMPTY;
           }

           if (itemstack1.isEmpty()) {
              slot.putStack(ItemStack.EMPTY);
           } else {
              slot.onSlotChanged();
           }

           if (itemstack1.getCount() == itemstack.getCount()) {
              return ItemStack.EMPTY;
           }

           slot.onTake(player, itemstack1);
        }
		return itemstack;
    }
}
