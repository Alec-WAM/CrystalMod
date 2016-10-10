package com.alec_wam.CrystalMod.tiles.pipes.liquid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import com.alec_wam.CrystalMod.tiles.pipes.AbstractPipeNetwork;
import com.alec_wam.CrystalMod.tiles.pipes.ConnectionMode;
import com.alec_wam.CrystalMod.tiles.pipes.item.RoundRobinIterator;

public class LiquidPipeNetwork extends AbstractPipeNetwork {
	public static int MAX_EXTRACT = 500;
	public static int MAX_IO = Fluid.BUCKET_VOLUME;
	
	List<NetworkTank> tanks = new ArrayList<NetworkTank>();
	Map<NetworkTankKey, NetworkTank> tankMap = new HashMap<NetworkTankKey, NetworkTank>();

	Map<NetworkTank, RoundRobinIterator<NetworkTank>> iterators;

	boolean filling;
	
	public void connectionChanged(TileEntityPipeLiquid pipe, EnumFacing conDir) {
	    NetworkTankKey key = new NetworkTankKey(pipe, conDir);
	    NetworkTank tank = new NetworkTank(pipe, conDir);
	    tanks.remove(tank); // remove old tank, NB: =/hash is only calced on location and dir
	    tankMap.remove(key);
	    tanks.add(tank);
	    tankMap.put(key, tank);
	}
	
	public boolean extractFrom(TileEntityPipeLiquid pipe, EnumFacing conDir) {
	    NetworkTank tank = getTank(pipe, conDir);
	    if(tank == null || !tank.isValid()) {
	      return false;
	    }
	    IFluidHandler fluidhandler = tank.externalTank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, conDir.getOpposite());
	    
	    FluidStack drained = fluidhandler.drain(MAX_EXTRACT, false);
	    if(drained == null || drained.amount <= 0 || !pipe.matchedFilter(drained, conDir, true)) {
	      return false;
	    }
	    int amountAccepted = fillFrom(tank, drained.copy(), true);
	    if(amountAccepted <= 0) {
	      return false;
	    }
	    drained = fluidhandler.drain(amountAccepted, true);
	    if(drained == null || drained.amount <= 0) {
	      return false;
	    }
	    return true;
	}

	private NetworkTank getTank(TileEntityPipeLiquid pipe, EnumFacing conDir) {
	    return tankMap.get(new NetworkTankKey(pipe, conDir));
	}

	public int fillFrom(TileEntityPipeLiquid pipe, EnumFacing conDir, FluidStack resource, boolean doFill) {
		return fillFrom(getTank(pipe, conDir), resource, doFill);
	}

	public int fillFrom(NetworkTank tank, FluidStack resource, boolean doFill) {

	    if(filling) {
	      return 0;
	    }

	    try {

	      filling = true;

	      if(resource == null || tank == null || !tank.pipe.matchedFilter(resource, tank.pipDir, true)) {
	        return 0;
	      }
	      resource = resource.copy();
	      resource.amount = Math.min(resource.amount, MAX_IO);
	      int filled = 0;
	      int remaining = resource.amount;
	      for (NetworkTank target : getIteratorForTank(tank)) {
	        if(!target.equals(tank) && target.acceptsOuput && target.isValid() && target.pipe.matchedFilter(resource, target.pipDir, false)) {
	          IFluidHandler fHandler = target.externalTank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, target.tankDir);	
	          int vol = fHandler.fill(resource.copy(), doFill);
	          remaining -= vol;
	          filled += vol;
	          if(remaining <= 0) {
	            return filled;
	          }
	          resource.amount = remaining;
	        }
	      }
	      return filled;

	    } finally {
	      filling = false;
	    }
	}

	private Iterable<NetworkTank> getIteratorForTank(NetworkTank tank) {
	    if(iterators == null) {
	      iterators = new HashMap<NetworkTank, RoundRobinIterator<NetworkTank>>();
	    }
	    RoundRobinIterator<NetworkTank> res = iterators.get(tank);
	    if(res == null) {
	      res = new RoundRobinIterator<NetworkTank>(tanks);
	      iterators.put(tank, res);
	    }
	    return res;
	}

  	public IFluidTankProperties[] getTankInfo(TileEntityPipeLiquid pipe, EnumFacing conDir) {
	    List<IFluidTankProperties> res = new ArrayList<IFluidTankProperties>(tanks.size());
	    NetworkTank tank = getTank(pipe, conDir);
	    for (NetworkTank target : tanks) {
	      if(!target.equals(tank) && target.isValid()) {
	    	IFluidHandler fHandler = target.externalTank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, target.tankDir);  
	        IFluidTankProperties[] tTanks = fHandler.getTankProperties();
	        if(tTanks != null) {
	          for (IFluidTankProperties info : tTanks) {
	            res.add(info);
	          }
	        }
	      }
	    }
	    return res.toArray(new IFluidTankProperties[res.size()]);
  	}

  	static class NetworkTankKey {

	    EnumFacing pipDir;
	    BlockPos pipeLoc;

	    public NetworkTankKey(TileEntityPipeLiquid pipe, EnumFacing pipDir) {
	      this(pipe.getPos(), pipDir);
	    }

	    public NetworkTankKey(BlockPos pipeLoc, EnumFacing pipDir) {
	      this.pipDir = pipDir;
	      this.pipeLoc = pipeLoc;
	    }

	    @Override
	    public int hashCode() {
	      final int prime = 31;
	      int result = 1;
	      result = prime * result + ((pipDir == null) ? 0 : pipDir.hashCode());
	      result = prime * result + ((pipeLoc == null) ? 0 : pipeLoc.hashCode());
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
	      NetworkTankKey other = (NetworkTankKey) obj;
	      if(pipDir != other.pipDir) {
	        return false;
	      }
	      if(pipeLoc == null) {
	        if(other.pipeLoc != null) {
	          return false;
	        }
	      } else if(!pipeLoc.equals(other.pipeLoc)) {
	        return false;
	      }
	      return true;
	    }

  	}

  	static class NetworkTank {

	    TileEntityPipeLiquid pipe;
	    EnumFacing pipDir;
	    TileEntity externalTank;
	    EnumFacing tankDir;
	    BlockPos pipeLoc;
	    boolean acceptsOuput;

	    public NetworkTank(TileEntityPipeLiquid pipe, EnumFacing pipDir) {
	      this.pipe = pipe;
	      this.pipDir = pipDir;
	      pipeLoc = pipe.getPos();
	      tankDir = pipDir.getOpposite();
	      externalTank = TileEntityPipeLiquid.getExternalFluidHandler(pipe.getWorld(), pipeLoc.offset(pipDir), pipDir);
	      acceptsOuput = pipe.getConnectionMode(pipDir).acceptsOutput();
	    }

	    public boolean isValid() {
	      return externalTank != null && externalTank.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, tankDir) && pipe.getConnectionMode(pipDir) != ConnectionMode.DISABLED;
	    }

	    @Override
	    public int hashCode() {
	      final int prime = 31;
	      int result = 1;
	      result = prime * result + ((pipDir == null) ? 0 : pipDir.hashCode());
	      result = prime * result + ((pipeLoc == null) ? 0 : pipeLoc.hashCode());
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
	      NetworkTank other = (NetworkTank) obj;
	      if(pipDir != other.pipDir) {
	        return false;
	      }
	      if(pipeLoc == null) {
	        if(other.pipeLoc != null) {
	          return false;
	        }
	      } else if(!pipeLoc.equals(other.pipeLoc)) {
	        return false;
	      }
	      return true;
	    }

	 }

	
}
