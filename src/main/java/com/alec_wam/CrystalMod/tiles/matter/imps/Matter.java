package com.alec_wam.CrystalMod.tiles.matter.imps;

import net.minecraft.item.ItemStack;

import com.alec_wam.CrystalMod.tiles.matter.MatterStack;

public class Matter{
	
	private String name;
	
	public Matter(String name){
		this.name = name;
	}
	
	public String getUnlocalizedName(MatterStack stack) {
		return name;
	}

	public String getUnlocalizedName() {
		return name;
	}

	public int getMeta(ItemStack stack) {
		return 0;
	}

}
