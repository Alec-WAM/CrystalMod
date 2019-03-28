package alec_wam.CrystalMod.blocks;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.init.ModBlocks;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCrystalLeaves extends BlockLeaves {

	protected final BlockVariantGroup<EnumCrystalColorSpecial, BlockCrystalLeaves> variantGroup;
	protected final EnumCrystalColorSpecial type;
	
	public BlockCrystalLeaves(EnumCrystalColorSpecial type, BlockVariantGroup<EnumCrystalColorSpecial, BlockCrystalLeaves> variantGroup, Properties properties) {
		super(properties);
		this.type = type;
		this.variantGroup = variantGroup;
	}
	
	@Override
	public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
		return ModBlocks.crystalSaplingGroup.getBlock(type);
	}
	
	@Override
	protected void dropApple(World p_196474_1_, BlockPos p_196474_2_, IBlockState p_196474_3_, int p_196474_4_) {
		
	}

	@Override
	protected int getSaplingDropChance(IBlockState p_196472_1_) {
		return 20;
	}

}
