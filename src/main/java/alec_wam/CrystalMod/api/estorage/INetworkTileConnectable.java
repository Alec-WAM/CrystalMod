package alec_wam.CrystalMod.api.estorage;

import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;

public interface INetworkTileConnectable extends INetworkTile {

	public boolean canConnect(EStorageNetwork network);
	
}
