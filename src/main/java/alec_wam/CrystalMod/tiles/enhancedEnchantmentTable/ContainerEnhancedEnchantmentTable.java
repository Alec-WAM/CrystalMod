package alec_wam.CrystalMod.tiles.enhancedEnchantmentTable;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerEnhancedEnchantmentTable extends Container
{
    private final TileEntityEnhancedEnchantmentTable table;

    public ContainerEnhancedEnchantmentTable(IInventory playerInventory, TileEntityEnhancedEnchantmentTable table)
    {
        this.table = table;

        this.addSlotToContainer(new Slot(table, 0, 39, 27) {
        	@Override
        	public boolean isItemValid(ItemStack stack)
	        {
	            return table.isItemValidForSlot(0, stack);
	        }
        });
        
        this.addSlotToContainer(new Slot(table, 1, 17, 63) {
        	@Override
        	public boolean isItemValid(ItemStack stack)
	        {
	            return table.isItemValidForSlot(1, stack);
	        }
        });
        
        this.addSlotToContainer(new Slot(table, 2, 62, 63) {
        	@Override
        	public boolean isItemValid(ItemStack stack)
	        {
	            return false;
	        }
        });

        for (int k = 0; k < 3; ++k)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            {
                this.addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 108 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l)
        {
            this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 166));
        }
    }
    
    @Override
    public void onContainerClosed(EntityPlayer playerIn)
    {
    	super.onContainerClosed(playerIn);
    }
    
    @Override
	public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.table.isUsableByPlayer(playerIn);
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    @Override
	@Nullable
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStackTools.getEmptyStack();
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index <= 2)
            {
                if (!this.mergeItemStack(itemstack1, 3, 39, false))
                {
                    return ItemStackTools.getEmptyStack();
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, 3, false))
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