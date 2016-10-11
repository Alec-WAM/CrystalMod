package alec_wam.CrystalMod.tiles.pipes.estorage;

import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import alec_wam.CrystalMod.tiles.pipes.types.IPipeType;

public class EStorageType implements IPipeType {

	public static final EStorageType INSTANCE = new EStorageType();
	
	@Override
	public String getCoreTexture(TileEntityPipe pipe) {
		return "crystalmod:blocks/pipe/storage_square";
	}
	
	public boolean useIOTextures(){
		return false;
	}

}
