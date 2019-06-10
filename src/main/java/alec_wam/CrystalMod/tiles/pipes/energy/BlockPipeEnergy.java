package alec_wam.CrystalMod.tiles.pipes.energy;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.tiles.pipes.BlockPipe;

public class BlockPipeEnergy extends BlockPipe {

	protected final BlockVariantGroup<EnumCrystalColorSpecial, ? extends BlockPipeEnergy> variantGroup;
	public final EnumCrystalColorSpecial type;
	
	public BlockPipeEnergy(EnumCrystalColorSpecial type, BlockVariantGroup<EnumCrystalColorSpecial, ? extends BlockPipeEnergy> variantGroup, Properties properties) {
		super(null, properties);
		this.type = type;
		this.variantGroup = variantGroup;
	}
	
}
