package com.alec_wam.CrystalMod.tiles.accessories.clock;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockAlarmClock extends BlockContainer {

	public BlockAlarmClock() {
		super(Material.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityAlarmClock();
	}

}
