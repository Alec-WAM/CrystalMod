package alec_wam.CrystalMod.tiles.machine.power.redstonereactor;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerRedstoneReactor extends Container
{
    private final TileRedstoneReactor reactor;

    public ContainerRedstoneReactor(IInventory playerInventory, TileRedstoneReactor reactor)
    {
        this.reactor = reactor;

        this.addSlotToContainer(new Slot(reactor, 0, 16, 27));
        this.addSlotToContainer(new Slot(reactor, 1, 16, 63) {
        	@Override
        	public int getSlotStackLimit()
            {
        		return 1;
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
	public void addListener(IContainerListener crafter){
		super.addListener(crafter);
	}
    
    @Override
	public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.reactor.isUsableByPlayer(playerIn);
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

            if(index !=0 && itemstack1.getItem() == ModItems.congealedRedstone){
	            if (!this.mergeItemStack(itemstack1, 0, 1, false))
	            {
	                return ItemStackTools.getEmptyStack();
	            }
            }            
            else if(index !=1 && itemstack1.getItem() == ModItems.reactorUpgrade){
	            if (!this.mergeItemStack(itemstack1, 1, 2, false))
	            {
	                return ItemStackTools.getEmptyStack();
	            }
            }     
            else if (index >= 2 && index < 29)
			{
				if (!this.mergeItemStack(itemstack1, 29, 38, false))
				{
					return ItemStackTools.getEmptyStack();
				}
			}
            else if (!this.mergeItemStack(itemstack1, 2, 29, false))
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