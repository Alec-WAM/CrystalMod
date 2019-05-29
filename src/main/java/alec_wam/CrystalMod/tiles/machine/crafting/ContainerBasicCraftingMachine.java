package alec_wam.CrystalMod.tiles.machine.crafting;

import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.SlotLockedOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBasicCraftingMachine<T extends TileEntityMachine> extends Container
{
	public static abstract class SlotItemChecker<T> {
		public abstract boolean canProcessItem(T machine, ItemStack stack);
	}
    public T machine;
    private final SlotItemChecker<T> checker;
    public ContainerBasicCraftingMachine(EntityPlayer player, T machine, SlotItemChecker<T> checker)
    {
    	this.machine = (machine);
    	this.checker = checker;
    	this.addSlot(new Slot(machine, 0, 56, 35) {
    		@Override
    		public boolean isItemValid(ItemStack stack){
    			return checker.canProcessItem(machine, stack);
    		}
    	});
    	this.addSlot(new SlotLockedOutput(player, machine, 1, 116, 35));
        this.addPlayerInventory(player.inventory);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return (this.machine != null && this.machine.isUsableByPlayer(player));
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
              if (checker.canProcessItem(machine, itemstack1)) {
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
