package alec_wam.CrystalMod.items.tools.backpack;

import net.minecraft.item.ItemStack;

public interface IBackpackOpenSource {

	public ItemStack getBackpack();

	public void openMainInventory();
	
	public void openUpgradesInventory(int tab);
	
}
