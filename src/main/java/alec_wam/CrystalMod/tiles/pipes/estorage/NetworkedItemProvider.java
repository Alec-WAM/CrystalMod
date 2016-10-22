package alec_wam.CrystalMod.tiles.pipes.estorage;

import alec_wam.CrystalMod.api.estorage.INetworkItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetworkedItemProvider {

  private INetworkItemProvider inter;
  BlockPos location;
  World world;

  NetworkedItemProvider(INetworkItemProvider inter, World world, BlockPos location) {
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
}
