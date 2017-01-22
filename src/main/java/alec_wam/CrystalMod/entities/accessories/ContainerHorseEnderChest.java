package alec_wam.CrystalMod.entities.accessories;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerHorseEnderChest extends Container
{
    private final IInventory horseInventory;
    private final InventoryEnderChest enderchestInventory;
    private final AbstractHorse theHorse;

    public ContainerHorseEnderChest(IInventory playerInventory, final IInventory horseInventoryIn, final AbstractHorse horse, EntityPlayer player)
    {
        this.horseInventory = horseInventoryIn;
        this.enderchestInventory = player.getInventoryEnderChest();
        this.theHorse = horse;
        
        int i = 3;
        horseInventoryIn.openInventory(player);
        int j = -18;
        this.addSlotToContainer(new Slot(horseInventoryIn, 0, 8, 18)
        {
            public boolean isItemValid(@Nullable ItemStack stack)
            {
                return super.isItemValid(stack) && stack.getItem() == Items.SADDLE && !this.getHasStack();
            }
        });
        
        if (enderchestInventory !=null)
        {
            for (int k = 0; k < 3; ++k)
            {
                for (int l = 0; l < 9; ++l)
                {
                    this.addSlotToContainer(new Slot(enderchestInventory, l + k * 9, 8 + l * 18, 69 + k * 18));
                }
            }
        }

        for (int i1 = 0; i1 < 3; ++i1)
        {
            for (int k1 = 0; k1 < 9; ++k1)
            {
                this.addSlotToContainer(new Slot(playerInventory, k1 + i1 * 9 + 9, 8 + k1 * 18, 131 + i1 * 18));
            }
        }

        for (int j1 = 0; j1 < 9; ++j1)
        {
            this.addSlotToContainer(new Slot(playerInventory, j1, 8 + j1 * 18, 189));
        }
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.enderchestInventory.isUsableByPlayer(playerIn) && this.theHorse.isEntityAlive() && this.theHorse.getDistanceToEntity(playerIn) < 8.0F && HorseAccessories.hasEnderChest(this.theHorse);
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
            itemstack = ItemStackTools.safeCopy(itemstack1);

            if (index < this.enderchestInventory.getSizeInventory()+1)
            {
                if (!this.mergeItemStack(itemstack1, enderchestInventory.getSizeInventory()+1, this.inventorySlots.size(), true))
                {
                    return ItemStackTools.getEmptyStack();
                }
            }
            else if (this.getSlot(0).isItemValid(itemstack1))
            {
                if (!this.mergeItemStack(itemstack1, 0, 1, false))
                {
                    return ItemStackTools.getEmptyStack();
                }
            }
            else if (this.enderchestInventory.getSizeInventory() <= 1 || !this.mergeItemStack(itemstack1, 1, this.enderchestInventory.getSizeInventory(), false))
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

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
        this.enderchestInventory.closeInventory(playerIn);
        this.horseInventory.closeInventory(playerIn);
    }
}