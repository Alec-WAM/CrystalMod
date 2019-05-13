package alec_wam.CrystalMod.tiles.pipes.item;

import alec_wam.CrystalMod.init.ModItems;
import alec_wam.CrystalMod.tiles.pipes.EnumPipeUpgrades;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class ContainerItemPipe extends Container
{
    private TileEntityPipeItem pipe;
    private EnumFacing face;

    public ContainerItemPipe(EntityPlayer player, TileEntityPipeItem tileEntity, EnumFacing face)
    {
    	this.pipe = tileEntity;
    	this.face = face;
    	this.addSlot(new Slot(tileEntity.getInternalInventory(face), 0, 80, 35) {
    		@Override
    		public boolean isItemValid(ItemStack stack){
    			return stack.isEmpty() || stack.getItem() == ModItems.pipeFilter;
    		}
    		
    		@Override
    		public int getSlotStackLimit() {
    			return 1;
    		}
    	});
    	this.addSlot(new Slot(tileEntity.getInternalInventory(face), 1, 152, 58) {
    		@Override
    		public boolean isItemValid(ItemStack stack){
    			return stack.isEmpty() || ModItems.pipeUpgrades.getItems().contains(stack.getItem());
    		}
    		
    		@Override
    		public int getItemStackLimit(ItemStack stack) {
    			if(stack.getItem() == ModItems.pipeUpgrades.getItem(EnumPipeUpgrades.SPEED)){
    				return NetworkInventory.MAX_SPEED_UPGRADES;
    			}
    			return 1;
    		}
    		
    		@Override
    		public int getSlotStackLimit() {
    			return 1;
    		}
    	});
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
    	ItemStack itemstack = ItemStackTools.getEmptyStack();
    	int par2 = i;
		Slot slot = this.inventorySlots.get(par2);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (par2 > 1)
			{
				if (!this.mergeItemStack(itemstack1, 0, 2, false))
				{
					return ItemStackTools.getEmptyStack();
				}
			} 
			else if (!this.mergeItemStack(itemstack1, 2, 38, true))
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

			if (ItemStackTools.getStackSize(itemstack1) == ItemStackTools.getStackSize(itemstack))
			{
				return ItemStackTools.getEmptyStack();
			}

			slot.onTake(player, itemstack1);
		}

		return itemstack;
    }
    
    //Make the slot limit smarter
    @Override
    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
    	boolean flag = false;
    	int i = startIndex;
    	if (reverseDirection) {
    		i = endIndex - 1;
    	}

    	if (stack.isStackable()) {
    		while(!stack.isEmpty()) {
    			if (reverseDirection) {
    				if (i < startIndex) {
    					break;
    				}
    			} else if (i >= endIndex) {
    				break;
    			}

    			Slot slot = this.inventorySlots.get(i);
    			ItemStack itemstack = slot.getStack();
    			if (!itemstack.isEmpty() && areItemsAndTagsEqual(stack, itemstack)) {
    				int j = itemstack.getCount() + stack.getCount();
    				//Changed to getItemStackLimit(stack)
    				int maxSize = Math.min(slot.getItemStackLimit(stack), stack.getMaxStackSize());
    				if (j <= maxSize) {
    					stack.setCount(0);
    					itemstack.setCount(j);
    					slot.onSlotChanged();
    					flag = true;
    				} else if (itemstack.getCount() < maxSize) {
    					stack.shrink(maxSize - itemstack.getCount());
    					itemstack.setCount(maxSize);
    					slot.onSlotChanged();
    					flag = true;
    				}
    			}

    			if (reverseDirection) {
    				--i;
    			} else {
    				++i;
    			}
    		}
    	}

    	if (!stack.isEmpty()) {
    		if (reverseDirection) {
    			i = endIndex - 1;
    		} else {
    			i = startIndex;
    		}

    		while(true) {
    			if (reverseDirection) {
    				if (i < startIndex) {
    					break;
    				}
    			} else if (i >= endIndex) {
    				break;
    			}

    			Slot slot1 = this.inventorySlots.get(i);
    			ItemStack itemstack1 = slot1.getStack();
    			if (itemstack1.isEmpty() && slot1.isItemValid(stack)) {
    				//Changed to getItemStackLimit(stack)
    				if (stack.getCount() > slot1.getItemStackLimit(stack)) {
    					slot1.putStack(stack.split(slot1.getItemStackLimit(stack)));
    				} else {
    					slot1.putStack(stack.split(stack.getCount()));
    				}

    				slot1.onSlotChanged();
    				flag = true;
    				break;
    			}

    			if (reverseDirection) {
    				--i;
    			} else {
    				++i;
    			}
    		}
    	}

    	return flag;
    }
}
