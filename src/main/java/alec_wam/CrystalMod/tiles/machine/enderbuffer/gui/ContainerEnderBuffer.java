package alec_wam.CrystalMod.tiles.machine.enderbuffer.gui;

import alec_wam.CrystalMod.tiles.machine.enderbuffer.EnderBufferManager.EnderBuffer;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.TileEntityEnderBuffer;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerEnderBuffer extends Container {

	private EnderBuffer eBuffer;
	public ContainerEnderBuffer(EntityPlayer player, TileEntityEnderBuffer buffer)
    {
        eBuffer = buffer.getBuffer();
        eBuffer.onPlayerOpenContainer(player);
        IItemHandler inventory = eBuffer.sendInv;

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

    @Override
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        if(playerIn instanceof EntityPlayerMP){
        	eBuffer.watchers.remove((EntityPlayerMP)playerIn);
        }
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

        if (index < 10)
        {
            if (!this.mergeItemStack(stack, 10, this.inventorySlots.size(), true))
            {
                return ItemStackTools.getEmptyStack();
            }
        }
        else if (!this.mergeItemStack(stack, 0, 10, false))
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
