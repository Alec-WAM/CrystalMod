package com.alec_wam.CrystalMod.items.tools;

import com.alec_wam.CrystalMod.CrystalMod;

import net.minecraft.item.ItemPickaxe;

public class ItemCustomPickaxe extends ItemPickaxe {

	public ItemCustomPickaxe(ToolMaterial material) {
		super(material);
		setCreativeTab(CrystalMod.tabTools);
	}

}
