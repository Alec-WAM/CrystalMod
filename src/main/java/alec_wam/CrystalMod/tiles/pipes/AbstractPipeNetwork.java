package alec_wam.CrystalMod.tiles.pipes;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class AbstractPipeNetwork {

  protected final Map<NetworkPos, TileEntityPipe> pipes = Maps.newHashMap();

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
    if(tile != null && tile.setNetwork(this)){
    	addPipe(tile);
    	for(TileEntityPipe pipe : connections){
    		if(pipe !=null){
    			if(pipe.getNetwork() == null){
				      if(pipe.setNetwork(this))addPipe(pipe);
    			} else if(pipe.getNetwork() !=null){
    				pipe.getNetwork().destroyNetwork();
    				if(pipe.setNetwork(this))addPipe(pipe);
    			}
    		}
    	}
    }
    //setNetwork(world, tile);
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
  
  public static NetworkPos getNetworkPos(TileEntityPipe pipe){
	  return new NetworkPos(pipe.getPos(), pipe.getWorld().provider.getDimension());
  }

  public void addPipe(TileEntityPipe con) {
	NetworkPos pos = getNetworkPos(con);
	if (!pipes.containsKey(pos)) {
		if (pipes.isEmpty()) {
			PipeNetworkTickHandler.instance.registerNetwork(this);
		}
		pipes.put(pos, con);
	}
  }

  public void removePipe(TileEntityPipe pipe){
	  NetworkPos pos = getNetworkPos(pipe);
	  ModLogger.debug("AbstractPipeNetwork: Removing pipe " + pipe.getPos());
	  if(pipes.containsKey(pos)){
		  pipes.remove(pos);
		  pipe.setNetwork(null);
	  }
	  if(pipes.isEmpty()){
		  destroyNetwork();
	  }
  }
  
  public void destroyNetwork() {
    for (TileEntityPipe con : pipes.values()) {
      con.setNetwork(null);
    }
    pipes.clear();
    PipeNetworkTickHandler.instance.unregisterNetwork(this);
  }

  public Collection<TileEntityPipe> getPipes() {
    return pipes.values();
  }

  public void notifyNetworkOfUpdate() {
    for (TileEntityPipe con : pipes.values()) {
    	if(con == null || con.getWorld() == null)continue;
      TileEntity te = con;
      BlockUtil.markBlockForUpdate(te.getWorld(), te.getPos());
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (TileEntityPipe con : pipes.values()) {
      sb.append(con.getPos().getX()+" "+con.getPos().getY()+" "+con.getPos().getZ());
      sb.append(", ");
    }
    return "AbstractPipeNetwork [pipes=" + sb.toString() + "]";
  }

  @Override
  public boolean equals(Object obj){
	  if(obj instanceof AbstractPipeNetwork){
		  AbstractPipeNetwork other = (AbstractPipeNetwork)obj;
		  return other.pipes == other.pipes;
	  }
	  return super.equals(obj);
  }
  
  public void doNetworkTick() {
  }
}
