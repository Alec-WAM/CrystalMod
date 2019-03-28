package alec_wam.CrystalMod.blocks;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import net.minecraft.block.BlockSapling;

public class BlockCrystalSapling extends BlockSapling {

	protected final BlockVariantGroup<EnumCrystalColorSpecial, BlockCrystalSapling> variantGroup;
	protected final EnumCrystalColorSpecial type;
	
	public BlockCrystalSapling(EnumCrystalColorSpecial type, BlockVariantGroup<EnumCrystalColorSpecial, BlockCrystalSapling> variantGroup, Properties properties) {
		super(new CrystalTree(type), properties);
		this.type = type;
		this.variantGroup = variantGroup;
	}

}
