package alec_wam.CrystalMod.tiles.spawner;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.item.Item;

public class ItemEmptyMobEssence extends Item {

	public ItemEmptyMobEssence(){
		super();
		this.setMaxStackSize(1);
		this.setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "emptymobessence");
	}
	
}
