package alec_wam.CrystalMod.tiles.pipes.power.rf;

import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import alec_wam.CrystalMod.tiles.pipes.types.IPipeType;
import alec_wam.CrystalMod.util.Util;

public class PowerRFType implements IPipeType {

	public static final PowerRFType INSTANCE = new PowerRFType();
	
	@Override
	public String getCoreTexture(TileEntityPipe pipe) {
		int tier = 0;
		if(Util.notNullAndInstanceOf(pipe, TileEntityPipePowerRF.class)){
			tier = ((TileEntityPipePowerRF)pipe).getSubType();
		}
		return "crystalmod:blocks/pipe/rfpower_square_"+tier;
	}

	@Override
	public boolean useIOTextures() {
		return true;
	}

}
