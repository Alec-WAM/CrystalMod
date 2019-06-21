package alec_wam.CrystalMod.blocks.decoration;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import net.minecraft.block.FenceBlock;

public class FenceBlockVariant<VARIANT extends Enum<VARIANT>> extends FenceBlock {

	protected final BlockVariantGroup<? extends Enum<VARIANT>, ? extends FenceBlockVariant<VARIANT>> variantGroup;
	protected final VARIANT type;
	
	public FenceBlockVariant(VARIANT type, BlockVariantGroup<? extends Enum<VARIANT>, ? extends FenceBlockVariant<VARIANT>> variantGroup, Properties properties) {
		super(properties);
		this.type = type;
		this.variantGroup = variantGroup;
	}

}
