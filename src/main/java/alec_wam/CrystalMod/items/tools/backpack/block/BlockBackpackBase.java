package alec_wam.CrystalMod.items.tools.backpack.block;

import alec_wam.CrystalMod.items.tools.backpack.IBackpack;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockBackpackBase extends BlockContainer {

	private IBackpack type;
	
	public BlockBackpackBase() {
		super(Material.CLOTH);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return type.createTileEntity(worldIn, meta);
	}

}
