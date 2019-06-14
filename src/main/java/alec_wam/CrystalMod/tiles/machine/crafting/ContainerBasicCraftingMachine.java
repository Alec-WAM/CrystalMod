package alec_wam.CrystalMod.tiles.machine.crafting;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBasicCraftingMachine<T extends TileEntityCraftingMachine> extends Container
{
	public T machine;
    public ContainerBasicCraftingMachine(int windowId, PlayerEntity player, T machine)
    {
    	super(null, windowId);
    	this.machine = (machine);
    	this.addSlot(new Slot(machine, 0, 56, 35) {
    		@Override
    		public boolean isItemValid(ItemStack stack){
    			return machine.isItemValidInput(stack);
    		}
    	});
    	this.addSlot(new SlotLockedOutput(player, machine, 1, 116, 35));
        this.addPlayerInventory(player.inventory);
    }

    @Override
    public boolean canInteractWith(PlayerEntity player)
    {
        return (this.machine != null && this.machine.isUsableByPlayer(player));
    }

    protected void addPlayerInventory(PlayerInventory paramPlayerInventory)
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                this.addSlot(new Slot(paramPlayerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(paramPlayerInventory, i, 8 + i * 18, 142));
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int i)
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
              if (machine.isItemValidInput(itemstack1)) {
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
