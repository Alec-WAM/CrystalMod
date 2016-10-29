package alec_wam.CrystalMod.api.estorage;

import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;

public interface INetworkTile {
	
	public void setNetwork(EStorageNetwork network);
	
	public EStorageNetwork getNetwork();
	
	public void onDisconnected();
	
}
