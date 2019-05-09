package alec_wam.CrystalMod.blocks.plants;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import net.minecraft.block.BlockReed;

public class BlockReedVariant<VARIANT extends Enum<VARIANT>> extends BlockReed {

	protected final BlockVariantGroup<? extends Enum<VARIANT>, ? extends BlockReedVariant<VARIANT>> variantGroup;
	protected final VARIANT type;
	
	public BlockReedVariant(VARIANT type, BlockVariantGroup<? extends Enum<VARIANT>, ? extends BlockReedVariant<VARIANT>> variantGroup,Properties properties) {
		super(properties);
		this.type = type;
		this.variantGroup = variantGroup;
	}

}
