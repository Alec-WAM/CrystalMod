package alec_wam.CrystalMod.tiles.darkinfection;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockDarkInfection extends BlockContainer {

	public BlockDarkInfection() {
		super(Material.ROCK);
		setHardness(0.3F);
		setCreativeTab(CrystalMod.tabBlocks);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileDarkInfection();
	}
	
}
