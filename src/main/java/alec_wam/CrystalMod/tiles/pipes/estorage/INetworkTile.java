package alec_wam.CrystalMod.tiles.pipes.estorage;

public interface INetworkTile {
	
	public void setNetwork(EStorageNetwork network);
	
	public EStorageNetwork getNetwork();
	
	public void onDisconnected();
	
}
