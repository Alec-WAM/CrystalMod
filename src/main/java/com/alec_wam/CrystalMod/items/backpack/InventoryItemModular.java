package com.alec_wam.CrystalMod.items.backpack;

import com.alec_wam.CrystalMod.util.NBTUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryItemModular extends InventoryItem
{
    protected ItemStack modularItemStack;

    public InventoryItemModular(ItemStack containerStack, int mainInvSize, boolean allowCustomStackSizes, EntityPlayer player)
    {
        super(containerStack, mainInvSize, 64, allowCustomStackSizes, player.worldObj.isRemote, player);

        this.modularItemStack = containerStack;
        this.containerUUID = NBTUtils.getUUIDFromItemStack(containerStack, "UUID", true);
        this.hostInventory = null;

        this.readFromContainerItemStack();
    }

    public ItemStack getModularItemStack()
    {
        //System.out.println("InventoryItemModular#getModularItemStack() - " + (this.isRemote ? "client" : "server"));
        if (this.hostInventory != null && this.containerUUID != null)
        {
            return getItemStackByUUID(this.hostInventory, this.containerUUID, "UUID");
        }

        return this.modularItemStack;
    }

    public void setModularItemStack(ItemStack stack)
    {
        this.modularItemStack = stack;
    }

    @Override
    public ItemStack getContainerItemStack()
    {
        //System.out.println("InventoryItemModular#getContainerItemStack() - " + (this.isRemote ? "client" : "server"));
        return super.getContainerItemStack();
    }

    public void readFromSelectedModuleStack()
    {
        super.readFromContainerItemStack();
    }

    @Override
    public void readFromContainerItemStack()
    {
        //System.out.println("InventoryItemModular#readFromContainerItemStack() - " + (this.isRemote ? "client" : "server"));
        //this.setMainInventoryStackLimit();

        // This also does "this.moduleInventory.readFromContainerItemStack();"
        //this.moduleInventory.setContainerItemStack(this.getModularItemStack());

        this.readFromSelectedModuleStack();
    }

    @Override
    protected void writeToContainerItemStack()
    {
        //System.out.println("InventoryItemModular#writeToContainerItemStack() - " + (this.isRemote ? "client" : "server"));
        super.writeToContainerItemStack();
    }

    @Override
    public int getInventoryStackLimit()
    {
        return super.getInventoryStackLimit();
    }

    @Override
    public boolean isItemValidForSlot(int slotNum, ItemStack stack)
    {
        if (stack == null)
        {
            return super.isItemValidForSlot(slotNum, stack);
        }

        ItemStack modularStack = this.getModularItemStack();
        // Don't allow nesting the same type of items as the container item inside itself
        if (modularStack != null && modularStack.getItem() == stack.getItem())
        {
            return false;
        }

        return super.isItemValidForSlot(slotNum, stack);
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        ItemStack stack = this.getModularItemStack();
        if (stack == null)
        {
            //System.out.println("isUseableByPlayer(): false - containerStack == null");
            return false;
        }

        return super.isUseableByPlayer(player);
    }
}
