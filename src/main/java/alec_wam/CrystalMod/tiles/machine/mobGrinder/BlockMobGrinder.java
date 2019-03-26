package alec_wam.CrystalMod.tiles.machine.mobGrinder;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.tiles.machine.BlockMachine;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockMobGrinder extends BlockMachine {

	public BlockMobGrinder() {
		super(Material.IRON);
		this.setResistance(2000.0f);
		this.setHardness(2f);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}
	//TODO Add NBT item drop
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityMobGrinder();
	}

}
