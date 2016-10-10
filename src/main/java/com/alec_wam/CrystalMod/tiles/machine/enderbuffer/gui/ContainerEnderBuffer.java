package com.alec_wam.CrystalMod.tiles.machine.enderbuffer.gui;

import com.alec_wam.CrystalMod.tiles.machine.enderbuffer.EnderBufferManager;
import com.alec_wam.CrystalMod.tiles.machine.enderbuffer.IEnderBufferList;
import com.alec_wam.CrystalMod.tiles.machine.enderbuffer.TileEntityEnderBuffer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerEnderBuffer extends Container {

	public ContainerEnderBuffer(EntityPlayer player, TileEntityEnderBuffer buffer)
    {
        IEnderBufferList mgr = EnderBufferManager.get(buffer.getWorld());

        IItemHandler inventory = mgr.getBuffer(buffer.code).sendInv;

        for (int j = 0; j < 2; ++j)
        {
            for (int k = 0; k < 5; ++k)
            {
                this.addSlotToContainer(new SlotItemHandler(inventory, k + j * 5, 120 + k * 18, 32 + j * 18));
            }
        }

        for (int py = 0; py < 3; ++py)
        {
            for (int px = 0; px < 9; ++px)
            {
                int slot = px + py * 9 + 9;
                this.addSlotToContainer(new Slot(player.inventory, slot, 47 + px * 18, 104 + py * 18 - 18));
            }
        }

        for (int slot = 0; slot < 9; ++slot)
        {
            this.addSlotToContainer(new Slot(player.inventory, slot, 47 + slot * 18, 144));
        }
    }

    public static class SlotNoAccess extends Slot
    {
        public SlotNoAccess(IInventory inventoryIn, int index, int xPosition, int yPosition)
        {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean canTakeStack(EntityPlayer playerIn)
        {
            return false;
        }

        @Override
        public boolean isItemValid(ItemStack stack)
        {
            return false;
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
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        Slot slot = this.inventorySlots.get(index);

        if (slot == null || !slot.getHasStack())
            return null;

        ItemStack stack = slot.getStack();
        ItemStack stackCopy = stack.copy();

        if (index < 2 * 5)
        {
            if (!this.mergeItemStack(stack, 2 * 5, this.inventorySlots.size(), true))
            {
                return null;
            }
        }
        else if (!this.mergeItemStack(stack, 0, 2 * 5, false))
        {
            return null;
        }

        if (stack.stackSize == 0)
        {
            slot.putStack(null);
        }
        else
        {
            slot.onSlotChanged();
        }

        return stackCopy;
    }

}
