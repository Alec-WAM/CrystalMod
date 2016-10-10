package com.alec_wam.CrystalMod.tiles.matter.imps;

import net.minecraft.block.BlockPlanks;
import net.minecraft.item.ItemStack;

import com.alec_wam.CrystalMod.tiles.matter.MatterStack;

public class MatterPlanks extends Matter {

	public MatterPlanks(String name) {
		super(name);
	}

	public String getUnlocalizedName(MatterStack stack) {
		String nam = BlockPlanks.EnumType.byMetadata(stack.getMeta()).getUnlocalizedName();
		return super.getUnlocalizedName(stack)+"."+nam;
	}

	public int getMeta(ItemStack stack) {
		return BlockPlanks.EnumType.byMetadata(stack.getItemDamage()).getMetadata();
	}
	
}
