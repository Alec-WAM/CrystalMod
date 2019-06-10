package alec_wam.CrystalMod.blocks;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import net.minecraft.block.LeavesBlock;

public class BlockCrystalLeaves extends LeavesBlock {

	protected final BlockVariantGroup<EnumCrystalColorSpecial, BlockCrystalLeaves> variantGroup;
	protected final EnumCrystalColorSpecial type;
	
	public BlockCrystalLeaves(EnumCrystalColorSpecial type, BlockVariantGroup<EnumCrystalColorSpecial, BlockCrystalLeaves> variantGroup, Properties properties) {
		super(properties);
		this.type = type;
		this.variantGroup = variantGroup;
	}
	
	//TODO Handle Leaves drops
	/*@Override
	public IItemProvider getItemDropped(BlockState state, World worldIn, BlockPos pos, int fortune) {
		return ModBlocks.crystalSaplingGroup.getBlock(type);
	}

	@Override
	protected int getSaplingDropChance(BlockState p_196472_1_) {
		return 20;
	}*/

}
