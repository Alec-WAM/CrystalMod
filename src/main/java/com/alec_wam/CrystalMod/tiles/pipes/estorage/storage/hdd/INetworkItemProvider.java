package com.alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd;

import com.alec_wam.CrystalMod.tiles.pipes.estorage.INetworkTile;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.inventory.INetworkInventory;

public interface INetworkItemProvider extends INetworkTile {

	public int getPriority();

	public void setPriority(int i);
	
	public INetworkInventory getNetworkInventory();
	
}
