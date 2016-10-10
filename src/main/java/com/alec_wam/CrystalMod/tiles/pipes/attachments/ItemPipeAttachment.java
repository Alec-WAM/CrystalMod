package com.alec_wam.CrystalMod.tiles.pipes.attachments;

import java.util.List;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.items.ModItems;
import com.alec_wam.CrystalMod.util.ItemNBTHelper;
import com.google.common.base.Strings;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemPipeAttachment extends Item {

	public ItemPipeAttachment(){
		super();
		this.setCreativeTab(CrystalMod.tabCovers);
		ModItems.registerItem(this, "pipeattachment");
	}
	
	public static String getID(ItemStack stack){
		if(ItemNBTHelper.verifyExistance(stack, "ID")){
			return ItemNBTHelper.getString(stack, "ID", "");
		}
		return "";
	}
	
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list){
		for(String id : AttachmentUtil.getIds()){
			ItemStack stack = new ItemStack(item);
			ItemNBTHelper.setString(stack, "ID", id);
			list.add(stack);
		}
	}
	
	public String getUnlocalizedName(ItemStack stack){
		String id = getID(stack);
		return super.getUnlocalizedName() + (!Strings.isNullOrEmpty(id) ? "." + getID(stack) : "");
	}
	
}
