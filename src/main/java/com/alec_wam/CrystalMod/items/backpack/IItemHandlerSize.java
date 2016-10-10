package com.alec_wam.CrystalMod.items.backpack;

import net.minecraft.item.ItemStack;

public interface IItemHandlerSize
{
    public int getInventoryStackLimit();

    public int getItemStackLimit(ItemStack stack);
}