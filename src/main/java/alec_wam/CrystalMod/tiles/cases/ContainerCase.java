package alec_wam.CrystalMod.tiles.cases;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCase extends Container
{
    private final TileEntityCaseBase caseTile;

    public ContainerCase(IInventory playerInventory, TileEntityCaseBase caseTile)
    {
        this.caseTile = caseTile;

        this.addSlotToContainer(new Slot(caseTile, 0, 80, 18) {
        	@Override
        	public boolean isItemValid(ItemStack stack)
	        {
	            return caseTile.isItemValidForSlot(0, stack);
	        }
        });
        this.addSlotToContainer(new Slot(caseTile, 1, 62, 36){
        	@Override
        	public boolean isItemValid(ItemStack stack)
	        {
	            return caseTile.isItemValidForSlot(1, stack);
	        }
        });
        this.addSlotToContainer(new Slot(caseTile, 2, 80, 36){
        	@Override
        	public boolean isItemValid(ItemStack stack)
	        {
	            return caseTile.isItemValidForSlot(2, stack);
	        }
        });
        this.addSlotToContainer(new Slot(caseTile, 3, 98, 36){
        	@Override
        	public boolean isItemValid(ItemStack stack)
	        {
	            return caseTile.isItemValidForSlot(3, stack);
	        }
        });
        this.addSlotToContainer(new Slot(caseTile, 4, 80, 54){
        	@Override
        	public boolean isItemValid(ItemStack stack)
	        {
	            return caseTile.isItemValidForSlot(4, stack);
	        }
        });

        for (int k = 0; k < 3; ++k)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            {
                this.addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 90 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l)
        {
            this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 148));
        }
    }
    
    @Override
    public void onContainerClosed(EntityPlayer playerIn)
    {
    	super.onContainerClosed(playerIn);
    	if(caseTile !=null){
    		caseTile.onClosed();
    	}
    }
    
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.caseTile.isUsableByPlayer(playerIn);
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

            if (index <= 4)
            {
                if (!this.mergeItemStack(itemstack1, 5, 41, false))
                {
                    return ItemStackTools.getEmptyStack();
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, 5, false))
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

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }
}