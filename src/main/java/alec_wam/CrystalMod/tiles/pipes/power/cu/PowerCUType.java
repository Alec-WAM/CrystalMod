package alec_wam.CrystalMod.tiles.pipes.power.cu;

import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import alec_wam.CrystalMod.tiles.pipes.types.IPipeType;
import alec_wam.CrystalMod.util.Util;

public class PowerCUType implements IPipeType {

	public static final PowerCUType INSTANCE = new PowerCUType();
	
	@Override
	public String getCoreTexture(TileEntityPipe pipe) {
		int tier = 0;
		if(Util.notNullAndInstanceOf(pipe, TileEntityPipePowerCU.class)){
			tier = ((TileEntityPipePowerCU)pipe).getSubType().ordinal();
		}
		return "crystalmod:blocks/pipe/power_square_"+tier;
	}

	@Override
	public boolean useIOTextures() {
		return true;
	}

}
