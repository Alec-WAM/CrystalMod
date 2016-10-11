package alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd;

import alec_wam.CrystalMod.tiles.pipes.estorage.INetworkTile;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.inventory.INetworkInventory;

public interface INetworkItemProvider extends INetworkTile {

	public int getPriority();

	public void setPriority(int i);
	
	public INetworkInventory getNetworkInventory();
	
}
