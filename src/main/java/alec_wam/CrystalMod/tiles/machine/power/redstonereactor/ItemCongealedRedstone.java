package alec_wam.CrystalMod.tiles.machine.power.redstonereactor;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.item.Item;

public class ItemCongealedRedstone extends Item {

	public ItemCongealedRedstone(){
		super();
		this.setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "congealedredstone");
	}
	
}
