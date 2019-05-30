package alec_wam.CrystalMod.items;

import alec_wam.CrystalMod.core.ItemVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import net.minecraft.item.ItemFood;

public class ItemCrystalBerry extends ItemFood {
	//Apple Food Value
	public ItemCrystalBerry(EnumCrystalColor type, ItemVariantGroup<EnumCrystalColor, ItemCrystalBerry> variantGroup, Properties properties) {
		super(4, 0.3F, false, properties);
	}

}
