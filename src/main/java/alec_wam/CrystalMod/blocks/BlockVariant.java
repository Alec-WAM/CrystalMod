package alec_wam.CrystalMod.blocks;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import net.minecraft.block.Block;

public class BlockVariant<VARIANT extends Enum<VARIANT>> extends Block {

	protected final BlockVariantGroup<? extends Enum<VARIANT>, ? extends BlockVariant<VARIANT>> variantGroup;
	protected final VARIANT type;
	
	public BlockVariant(VARIANT type, BlockVariantGroup<? extends Enum<VARIANT>, ? extends BlockVariant<VARIANT>> variantGroup,Properties properties) {
		super(properties);
		this.type = type;
		this.variantGroup = variantGroup;
	}

}
