package alec_wam.CrystalMod.blocks;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;

public class BlockCrystalIngot extends BlockIngot {

	protected final BlockVariantGroup<EnumCrystalColorSpecial, BlockCrystalIngot> variantGroup;
	protected final EnumCrystalColorSpecial type;
	
	public BlockCrystalIngot(EnumCrystalColorSpecial type, BlockVariantGroup<EnumCrystalColorSpecial, BlockCrystalIngot> variantGroup,Properties properties) {
		super(properties);
		this.type = type;
		this.variantGroup = variantGroup;
	}

}
