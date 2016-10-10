package com.alec_wam.CrystalMod.tiles.pipes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.alec_wam.CrystalMod.util.BlockUtil;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class AbstractPipeNetwork {

  protected final List<TileEntityPipe> pipes = new ArrayList<TileEntityPipe>();

  public void init(TileEntityPipe tile, Collection<TileEntityPipe> connections, World world) {

    if(world.isRemote) {
      throw new UnsupportedOperationException();
    }

    // Destroy all existing redstone networks around this block
    for (TileEntityPipe con : connections) {
      AbstractPipeNetwork network = con.getNetwork();
      if(network != null) {
        network.destroyNetwork();
      }
    }
    setNetwork(world, tile);
    notifyNetworkOfUpdate();
  }

  protected void setNetwork(World world, TileEntityPipe tile) {

    if(tile != null && tile.setNetwork(this)) {
      addPipe(tile);
      TileEntity te = tile;
      Collection<TileEntityPipe> connections = PipeUtil.getConnectedPipes(world, te.getPos(), tile.getPipeType());
      for (TileEntityPipe con : connections) {
        if(con.getNetwork() == null) {
          setNetwork(world, con);
        } else if(con.getNetwork() != this) {
          con.getNetwork().destroyNetwork();
          setNetwork(world, con);
        }
      }
    }
  }

  public void addPipe(TileEntityPipe con) {
    if(!pipes.contains(con)) {
      if(pipes.isEmpty()) {
        PipeNetworkTickHandler.instance.registerNetwork(this);
      }
      pipes.add(con);
    }
  }

  public void destroyNetwork() {
    for (TileEntityPipe con : pipes) {
      con.setNetwork(null);
    }
    pipes.clear();
    PipeNetworkTickHandler.instance.unregisterNetwork(this);
  }

  public List<TileEntityPipe> getPipes() {
    return pipes;
  }

  public void notifyNetworkOfUpdate() {
    for (TileEntityPipe con : pipes) {
    	if(con == null || con.getWorld() == null)continue;
      TileEntity te = con;
      BlockUtil.markBlockForUpdate(te.getWorld(), te.getPos());
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (TileEntityPipe con : pipes) {
      sb.append(con.getPos().getX()+" "+con.getPos().getY()+" "+con.getPos().getZ());
      sb.append(", ");
    }
    return "AbstractPipeNetwork [pipes=" + sb.toString() + "]";
  }

  public void doNetworkTick() {
  }
}
