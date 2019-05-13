package alec_wam.CrystalMod.blocks.plants;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import net.minecraft.block.BlockTallFlower;

public class BlockTallFlowerVariant<VARIANT extends Enum<VARIANT>> extends BlockTallFlower {

	protected final BlockVariantGroup<? extends Enum<VARIANT>, ? extends BlockTallFlowerVariant<VARIANT>> variantGroup;
	protected final VARIANT type;
	
	public BlockTallFlowerVariant(VARIANT type, BlockVariantGroup<? extends Enum<VARIANT>, ? extends BlockTallFlowerVariant<VARIANT>> variantGroup,Properties properties) {
		super(properties);
		this.type = type;
		this.variantGroup = variantGroup;
	}

}