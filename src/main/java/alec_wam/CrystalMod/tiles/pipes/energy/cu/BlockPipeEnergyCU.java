package alec_wam.CrystalMod.tiles.pipes.energy.cu;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.tiles.pipes.energy.BlockPipeEnergy;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockPipeEnergyCU extends BlockPipeEnergy {

	public BlockPipeEnergyCU(EnumCrystalColorSpecial type, BlockVariantGroup<EnumCrystalColorSpecial, BlockPipeEnergyCU> variantGroup, Properties properties) {
		super(type, variantGroup, properties);
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityPipeEnergyCU(type);
	}
	
}
