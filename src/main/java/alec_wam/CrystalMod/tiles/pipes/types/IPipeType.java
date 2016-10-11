package alec_wam.CrystalMod.tiles.pipes.types;

import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;

public interface IPipeType {

	public String getCoreTexture(TileEntityPipe pipe);
	
	public boolean useIOTextures();
	
}
