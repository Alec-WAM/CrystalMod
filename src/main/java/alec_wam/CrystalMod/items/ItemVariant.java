package alec_wam.CrystalMod.items;

import alec_wam.CrystalMod.core.ItemVariantGroup;
import net.minecraft.item.Item;

public class ItemVariant<VARIANT extends Enum<VARIANT>> extends Item {

	protected final ItemVariantGroup<? extends Enum<VARIANT>, ? extends ItemVariant<VARIANT>> variantGroup;
	protected final VARIANT type;
	
	public ItemVariant(VARIANT type, ItemVariantGroup<? extends Enum<VARIANT>, ? extends ItemVariant<VARIANT>> variantGroup,Properties properties) {
		super(properties);
		this.type = type;
		this.variantGroup = variantGroup;
	}

}
