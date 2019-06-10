package alec_wam.CrystalMod.tiles.pipes.energy.rf;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.tiles.pipes.energy.BlockPipeEnergy;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockPipeEnergyRF extends BlockPipeEnergy {

	public BlockPipeEnergyRF(EnumCrystalColorSpecial type, BlockVariantGroup<EnumCrystalColorSpecial, BlockPipeEnergyRF> variantGroup, Properties properties) {
		super(type, variantGroup, properties);
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityPipeEnergyRF(type);
	}
	
}
