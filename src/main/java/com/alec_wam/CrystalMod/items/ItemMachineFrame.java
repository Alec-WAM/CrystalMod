package com.alec_wam.CrystalMod.items;

import com.alec_wam.CrystalMod.CrystalMod;

import net.minecraft.item.Item;

public class ItemMachineFrame extends Item {

	public ItemMachineFrame(){
		super();
		this.setCreativeTab(CrystalMod.tabBlocks);
		ModItems.registerItem(this, "machineFrame");
	}
	
}
