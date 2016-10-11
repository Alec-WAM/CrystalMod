package alec_wam.CrystalMod.items.tools;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.item.ItemAxe;

public class ItemCustomAxe extends ItemAxe {

	public ItemCustomAxe(ToolMaterial material) {
		super(material, 8.0F, -3.0F);
		setCreativeTab(CrystalMod.tabTools);
	}

}
