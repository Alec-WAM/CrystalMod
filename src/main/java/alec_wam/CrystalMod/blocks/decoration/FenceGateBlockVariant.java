package alec_wam.CrystalMod.blocks.decoration;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import net.minecraft.block.FenceGateBlock;

public class FenceGateBlockVariant<VARIANT extends Enum<VARIANT>> extends FenceGateBlock {

	protected final BlockVariantGroup<? extends Enum<VARIANT>, ? extends FenceGateBlockVariant<VARIANT>> variantGroup;
	protected final VARIANT type;
	
	public FenceGateBlockVariant(VARIANT type, BlockVariantGroup<? extends Enum<VARIANT>, ? extends FenceGateBlockVariant<VARIANT>> variantGroup, Properties properties) {
		super(properties);
		this.type = type;
		this.variantGroup = variantGroup;
	}

}
