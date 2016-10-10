package com.alec_wam.CrystalMod.items.armor;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.util.ItemUtil;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ItemCustomArmor extends ItemArmor {

	public final String textureName;
	public final ItemStack repair;
	public ItemCustomArmor(ArmorMaterial materialIn, EntityEquipmentSlot equipmentSlotIn, String name, ItemStack repair) {
		super(materialIn, 0, equipmentSlotIn);
		setCreativeTab(CrystalMod.tabTools);
		textureName = name;
		this.repair = repair;
	}
	
	@Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
    {
		int suffix = this.armorType == EntityEquipmentSlot.LEGS ? 2 : 1;
        return "crystalmod:textures/model/armor/" + textureName + "_layer_" + suffix + ".png";
    }
	
	@Override
	public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
		return ItemUtil.stackMatchUseOre(par2ItemStack, repair) ? true : super.getIsRepairable(par1ItemStack, par2ItemStack);
	}

}
