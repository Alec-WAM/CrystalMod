package alec_wam.CrystalMod.api.estorage;

import alec_wam.CrystalMod.tiles.pipes.estorage.INetworkTile;

public interface INetworkItemProvider extends INetworkTile {

	public int getPriority();

	public void setPriority(int i);
	
	public INetworkInventory getNetworkInventory();
	
}
