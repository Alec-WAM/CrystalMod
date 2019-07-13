package alec_wam.CrystalMod.tiles.machine.pump;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ContainerFluidPump extends Container
{
	public TileEntityFluidPump machine;
    public ContainerFluidPump(int windowId, PlayerEntity player, TileEntityFluidPump machine)
    {
    	super(null, windowId);
    	this.machine = (machine);
    	this.addSlot(new Slot(machine, 0, 153, 55) {
    		@Override
    		public boolean isItemValid(ItemStack stack){
    			return machine.isItemValidForSlot(0, stack);
    		}
    	});
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
                this.addSlot(new Slot(paramPlayerInventory, j + i * 9 + 9, 18 + j * 18, 86 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(paramPlayerInventory, i, 18 + i * 18, 144));
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int i)
    {
    	ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(i);
        if (slot != null && slot.getHasStack()) {
           ItemStack itemstack1 = slot.getStack();
           itemstack = itemstack1.copy();
           if (i > 0) {
        	   if(itemstack1.getItem() == Items.ENCHANTED_BOOK){
        		   if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
            		   return ItemStack.EMPTY;
            	   }
        	   } 
           } else if (!this.mergeItemStack(itemstack1, 1, 37, false)) {
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
