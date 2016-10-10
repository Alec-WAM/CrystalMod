package com.alec_wam.CrystalMod.tiles.pipes.power.cu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.alec_wam.CrystalMod.tiles.pipes.AbstractPipeNetwork;
import com.alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import com.alec_wam.CrystalMod.tiles.pipes.power.IPowerInterface;

public class CUPowerPipeNetwork extends AbstractPipeNetwork {

	NetworkCUPowerManager powerManager;

	private final Map<ReceptorKey, ReceptorEntry> powerReceptors = new HashMap<ReceptorKey, ReceptorEntry>();

	@Override
	public void init(TileEntityPipe tile, Collection<TileEntityPipe> connections, World world) {
	    super.init(tile, connections, world);
	    powerManager = new NetworkCUPowerManager(this, world);
	    powerManager.receptorsChanged();
    }

    @Override
	public void destroyNetwork() {
	    if(powerManager != null) {
	      powerManager.onNetworkDestroyed();
	    }
	    super.destroyNetwork();
	}

    public NetworkCUPowerManager getPowerManager() {
    	return powerManager;
    }
	
	@Override
	public void addPipe(TileEntityPipe pipEntry) {
	    super.addPipe(pipEntry);
	    if(!(pipEntry instanceof TileEntityPipePowerCU))return;
	    TileEntityPipePowerCU pip = (TileEntityPipePowerCU)pipEntry;
	    
	    Set<EnumFacing> externalDirs = pip.getExternalConnections();
	    for (EnumFacing dir : externalDirs) {
	      IPowerInterface pr = pip.getExternalPowerReceptor(dir);
	      if(pr != null) {
	        BlockPos p = pip.getPos().offset(dir);
	        powerReceptorAdded(pip, dir, p.getX(), p.getY(), p.getZ(), pr);
	      }
	    }
	}
	


	public void powerReceptorAdded(TileEntityPipePowerCU powerPipe, EnumFacing direction, int x, int y, int z, IPowerInterface powerReceptor) {
		if(powerReceptor == null) {
		  return;
		}
		BlockPos location = new BlockPos(x, y, z);
		ReceptorKey key = new ReceptorKey(location, direction);
		ReceptorEntry re = powerReceptors.get(key);
		if(re == null) {
		  re = new ReceptorEntry(powerReceptor, location, powerPipe, direction);
		  powerReceptors.put(key, re);
		}
		if(powerManager != null) {
		  powerManager.receptorsChanged();
		}
	}

	public void powerReceptorRemoved(int x, int y, int z) {
	    BlockPos bc = new BlockPos(x, y, z);
	    List<ReceptorKey> remove = new ArrayList<ReceptorKey>();
	    for (ReceptorKey key : powerReceptors.keySet()) {
	      if(key != null && key.coord.equals(bc)) {
	        remove.add(key);
	      }
	    }
	    for (ReceptorKey key : remove) {
	      powerReceptors.remove(key);
	    }
	    powerManager.receptorsChanged();
	}

    public Collection<ReceptorEntry> getPowerReceptors() {
    	return powerReceptors.values();
    }

    @Override
    public void doNetworkTick() {
    	powerManager.applyRecievedPower();
    }

    public static class ReceptorEntry {

	    TileEntityPipePowerCU emmiter;
	    BlockPos coord;
	    EnumFacing direction;
	
	    IPowerInterface powerInterface;
	
	    public ReceptorEntry(IPowerInterface powerReceptor, BlockPos coord, TileEntityPipePowerCU emmiter, EnumFacing direction) {
	      powerInterface = powerReceptor;
	      this.coord = coord;
	      this.emmiter = emmiter;
	      this.direction = direction;
	    }
	
    }

    private static class ReceptorKey {
	    BlockPos coord;
	    EnumFacing direction;
	
	    ReceptorKey(BlockPos coord, EnumFacing direction) {
	      this.coord = coord;
	      this.direction = direction;
	    }
	
	    @Override
	    public int hashCode() {
	      final int prime = 31;
	      int result = 1;
	      result = prime * result + ((coord == null) ? 0 : coord.hashCode());
	      result = prime * result + ((direction == null) ? 0 : direction.hashCode());
	      return result;
	    }
	
	    @Override
	    public boolean equals(Object obj) {
	      if(this == obj) {
	        return true;
	      }
	      if(obj == null) {
	        return false;
	      }
	      if(getClass() != obj.getClass()) {
	        return false;
	      }
	      ReceptorKey other = (ReceptorKey) obj;
	      if(coord == null) {
	        if(other.coord != null) {
	          return false;
	        }
	      } else if(!coord.equals(other.coord)) {
	        return false;
	      }
	      if(direction != other.direction) {
	        return false;
	      }
	      return true;
	    }

    }
	
}
