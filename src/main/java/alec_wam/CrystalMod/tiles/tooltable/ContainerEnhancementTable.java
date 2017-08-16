package alec_wam.CrystalMod.tiles.tooltable;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.api.enhancements.KnowledgeManager;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerEnhancementTable extends Container
{
    private final TileEnhancementTable table;

    public ContainerEnhancementTable(IInventory playerInventory, TileEnhancementTable table)
    {
        this.table = table;

        this.addSlotToContainer(new Slot(table, 0, 80, 20));
        
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
	public void addListener(IContainerListener crafter){
		super.addListener(crafter);
		if(crafter !=null && crafter instanceof EntityPlayerMP){
			KnowledgeManager.syncData((EntityPlayerMP)crafter);
		}
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

            if (index == 0)
            {
                if (!this.mergeItemStack(itemstack1, 1, 37, false))
                {
                    return ItemStackTools.getEmptyStack();
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, 1, false))
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