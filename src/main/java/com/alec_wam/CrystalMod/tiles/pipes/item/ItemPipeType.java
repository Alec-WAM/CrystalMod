package com.alec_wam.CrystalMod.tiles.pipes.item;

import com.alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import com.alec_wam.CrystalMod.tiles.pipes.types.IPipeType;

public class ItemPipeType implements IPipeType {

	public static final ItemPipeType INSTANCE = new ItemPipeType();
	
	@Override
	public String getCoreTexture(TileEntityPipe pipe) {
		return "crystalmod:blocks/pipe/item_square";
	}

	@Override
	public boolean useIOTextures() {
		return true;
	}

}
