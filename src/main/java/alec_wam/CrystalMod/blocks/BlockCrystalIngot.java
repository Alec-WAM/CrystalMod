package alec_wam.CrystalMod.blocks;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class BlockCrystalIngot extends BlockVariant<EnumCrystalColorSpecial>{

	public BlockCrystalIngot(EnumCrystalColorSpecial type, BlockVariantGroup<? extends Enum<EnumCrystalColorSpecial>, ? extends BlockVariant<EnumCrystalColorSpecial>> variantGroup, Properties properties) {
		super(type, variantGroup, properties);
	}
	
	@Override
	public boolean isBeaconBase(IBlockState state, IWorldReader world, BlockPos pos, BlockPos beacon)
    {
		return true;
    }

}
