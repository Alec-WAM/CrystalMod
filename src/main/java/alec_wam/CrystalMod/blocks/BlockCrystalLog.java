package alec_wam.CrystalMod.blocks;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import net.minecraft.block.LogBlock;
import net.minecraft.block.material.MaterialColor;

public class BlockCrystalLog extends LogBlock {

	protected final BlockVariantGroup<EnumCrystalColorSpecial, BlockCrystalLog> variantGroup;
	protected final EnumCrystalColorSpecial type;
	
	public BlockCrystalLog(EnumCrystalColorSpecial type, BlockVariantGroup<EnumCrystalColorSpecial, BlockCrystalLog> variantGroup, Properties properties) {
		super(MaterialColor.WOOD, properties);
		this.type = type;
		this.variantGroup = variantGroup;
	}

}
