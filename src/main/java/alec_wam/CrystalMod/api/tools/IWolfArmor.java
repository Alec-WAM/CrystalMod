package alec_wam.CrystalMod.api.tools;

import alec_wam.CrystalMod.entities.accessories.WolfAccessories.WolfArmor;
import net.minecraft.item.ItemStack;

public interface IWolfArmor {
	public WolfArmor getWolfArmor(ItemStack stack);
}
