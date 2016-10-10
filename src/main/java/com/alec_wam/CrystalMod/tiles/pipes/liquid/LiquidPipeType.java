package com.alec_wam.CrystalMod.tiles.pipes.liquid;

import com.alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import com.alec_wam.CrystalMod.tiles.pipes.types.IPipeType;

public class LiquidPipeType implements IPipeType {

	public static final LiquidPipeType INSTANCE = new LiquidPipeType();
	
	@Override
	public String getCoreTexture(TileEntityPipe pipe) {
		return "crystalmod:blocks/pipe/fluid_square";
	}

	@Override
	public boolean useIOTextures() {
		return true;
	}

}
