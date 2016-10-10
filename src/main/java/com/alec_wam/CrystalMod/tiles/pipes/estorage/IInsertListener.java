package com.alec_wam.CrystalMod.tiles.pipes.estorage;

import net.minecraft.item.ItemStack;

public interface IInsertListener {

	public void onItemInserted(ItemStack stack);
	
	public void onItemExtracted(ItemStack stack, int amount);
}
