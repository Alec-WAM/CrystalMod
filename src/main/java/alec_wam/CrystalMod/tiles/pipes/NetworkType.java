package alec_wam.CrystalMod.tiles.pipes;

import alec_wam.CrystalMod.tiles.pipes.item.TileEntityPipeItem;

public enum NetworkType {
	ITEM(TileEntityPipeItem.class), FLUID(null), POWERFU(null), POWERCU(null), STORAGE(null), REDSTONE(null);
	
	final Class<? extends TileEntityPipeBase> tileClass;
	NetworkType(Class<? extends TileEntityPipeBase> tileClass){
		this.tileClass = tileClass;
	}
	
	public Class<? extends TileEntityPipeBase> getTile(){
		return tileClass;
	}
}
