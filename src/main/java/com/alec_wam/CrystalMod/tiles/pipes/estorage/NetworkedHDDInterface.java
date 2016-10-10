package com.alec_wam.CrystalMod.tiles.pipes.estorage;

import com.alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.INetworkItemProvider;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetworkedHDDInterface {

  private INetworkItemProvider inter;
  BlockPos location;
  
  World world;

  NetworkedHDDInterface(INetworkItemProvider inter, World world, BlockPos location) {
    this.inter = inter;
    this.location = location;
    this.world = world;
  }

  int getPriority() {
    return inter == null ? 0 : inter.getPriority();
  }

  public INetworkItemProvider getInterface() {
    return inter;
  }
  
  public void updateItems(EStorageNetwork network, int index){
	if(this.inter.getNetworkInventory() !=null){
		this.inter.getNetworkInventory().updateItems(network, index);
	}
  }
}
