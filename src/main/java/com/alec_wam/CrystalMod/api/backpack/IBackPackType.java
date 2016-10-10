package com.alec_wam.CrystalMod.api.backpack;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IBackPackType {

	public abstract String getType(ItemStack stack);
	
	public abstract String getID();
	
	public abstract ItemStack onRightClick(ItemStack stack, World world, EntityPlayer player);
	
	public abstract void addInfo(ItemStack stack, EntityPlayer player, List<String> list, boolean adv);
	
}
