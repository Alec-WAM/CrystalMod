package alec_wam.CrystalMod.items.tools.backpack.gui;

import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.EnderBufferManager;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.EnderBufferManager.EnderBuffer;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.IEnderBufferList;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.TileEntityEnderBuffer;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerBackpackEnderBuffer extends Container {

	public ContainerBackpackEnderBuffer(InventoryBackpack backpackInv, EnderBuffer buffer)
    {
        IItemHandler inventory = buffer.sendInv;

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
                this.addSlotToContainer(new Slot(backpackInv.getPlayer().inventory, slot, 47 + px * 18, 104 + py * 18 - 18));
            }
        }

        for (int slot = 0; slot < 9; ++slot)
        {
            this.addSlotToContainer(new Slot(backpackInv.getPlayer().inventory, slot, 47 + slot * 18, 144));
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
                return ItemStackTools.getEmptyStack();
            }
        }
        else if (!this.mergeItemStack(stack, 0, 2 * 5, false))
        {
            return ItemStackTools.getEmptyStack();
        }

        if (ItemStackTools.isEmpty(stack))
        {
            slot.putStack(ItemStackTools.getEmptyStack());
        }
        else
        {
            slot.onSlotChanged();
        }

        return stackCopy;
    }

}
