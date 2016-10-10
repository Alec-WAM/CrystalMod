package com.alec_wam.CrystalMod.tiles.pipes.power.cu;

import java.util.EnumMap;

import com.alec_wam.CrystalMod.Config;
import com.alec_wam.CrystalMod.api.energy.ICEnergyConnection;
import com.alec_wam.CrystalMod.api.energy.ICEnergyReceiver;
import com.alec_wam.CrystalMod.blocks.ModBlocks;
import com.alec_wam.CrystalMod.tiles.pipes.AbstractPipeNetwork;
import com.alec_wam.CrystalMod.tiles.pipes.BlockPipe.PipeType;
import com.alec_wam.CrystalMod.tiles.pipes.ConnectionMode;
import com.alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import com.alec_wam.CrystalMod.tiles.pipes.power.IPowerInterface;
import com.alec_wam.CrystalMod.tiles.pipes.types.IPipeType;
import com.alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

public class TileEntityPipePowerCU extends TileEntityPipe implements ICEnergyReceiver, ICEnergyConnection {

	protected CUPowerPipeNetwork network;

	private int energyStoredCU;

	private int subtype;

	protected EnumMap<EnumFacing, Long> recievedTicks;
	
	public void setSubType(int sub){
		this.subtype = sub;
	}
	
	public int getSubType(){
		return subtype;
	}
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("subtype", subtype);
		nbt.setInteger("energyStoredCU", energyStoredCU);
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		this.subtype = nbt.getInteger("subtype");
		
		this.setCEnergyStored(nbt.getInteger("energyStoredCU"));
	}
	
	public boolean onNeighborBlockChange(Block blockId) {
	    if(network != null && network.powerManager != null) {
	      network.powerManager.receptorsChanged();
	    }
	    return super.onNeighborBlockChange(blockId);
	}
	
	public void setConnectionMode(EnumFacing dir, ConnectionMode mode) {
		  super.setConnectionMode(dir, mode);
		  recievedTicks = null;
	}
	
	public boolean setNetwork(AbstractPipeNetwork network) {
	    this.network = (CUPowerPipeNetwork) network;
	    super.setNetwork(network);
	    return true;
	}
	
	public void setCEnergyStored(int energy){
		energyStoredCU = MathHelper.clamp_int(energy, 0, getMaxCEnergyStored()); 
	}
	
	public int getCEnergyStored(){
		return energyStoredCU;
	}
	
	@Override
	public int getCEnergyStored(EnumFacing from) {
		return getCEnergyStored();
	}

	public int getMaxEnergyRecieved(EnumFacing dir) {
      ConnectionMode mode = getConnectionMode(dir);
      if((mode == ConnectionMode.OUTPUT || mode == ConnectionMode.DISABLED) /*|| !isRedstoneEnabled(dir)*/) {
        return 0;
      }
      return getMaxEnergyIO(subtype);
    }

	public int getMaxEnergyExtracted(EnumFacing dir) {
		ConnectionMode mode = getConnectionMode(dir);
		if(mode == ConnectionMode.INPUT || mode == ConnectionMode.DISABLED /*|| !isRedstoneEnabled(dir)*/) {
			return 0;
		}
		if(recievedEnergyThisTick(dir)) {
			return 0;
		}
		return getMaxEnergyIO(subtype);
	}
	
	private boolean recievedEnergyThisTick(EnumFacing dir) {
	    if(recievedTicks == null || dir == null || recievedTicks.get(dir) == null || getWorld() == null) {
	      return false;
	    }

	    long curTick = getWorld().getTotalWorldTime();
	    long recT = recievedTicks.get(dir);
	    if(curTick - recT <= 5) {
	      return true;
	    }
	    return false;
	}
	
	@Override
	public int getMaxCEnergyStored(EnumFacing from) {
		return getMaxCEnergyStored();
	}

	static int getMaxEnergyIO(int subtype) {
	    switch (subtype) {
	    case 1:
	      return Config.powerConduitTierTwoCU;
	    case 2:
	      return Config.powerConduitTierThreeCU;
	    case 3:
		      return Config.powerConduitTierThreeCU;
	    default:
	      return Config.powerConduitTierOneCU;
	    }
	}

	public int getMaxCEnergyStored() {
	    return getMaxEnergyIO(subtype);
	}
	
	@Override
	public boolean canConnectCEnergy(EnumFacing from) {
		return this.getConnectionMode(from) != ConnectionMode.DISABLED;
	}

	@Override
	public int fillCEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		if(getMaxEnergyRecieved(from) == 0 || maxExtract <= 0) {
	      return 0;
	    }
	    int freeSpace = getMaxCEnergyStored() - getCEnergyStored();
	    int result = Math.min(maxExtract, freeSpace);
	    if(!simulate && result > 0) {
	      setCEnergyStored(getCEnergyStored() + result);      

	      if(recievedTicks == null) {
	          recievedTicks = new EnumMap<EnumFacing, Long>(EnumFacing.class);
	        }
	        recievedTicks.put(from, getWorld().getTotalWorldTime());

	    }
	    return result;
	}

	@Override
	public IPipeType getPipeType() {
		return PowerCUType.INSTANCE;
	}

	@Override
	public AbstractPipeNetwork createNetwork() {
		return new CUPowerPipeNetwork();
	}

	public boolean canConnectToExternal(EnumFacing direction, boolean ignoreDisabled) {
	    IPowerInterface rec = getExternalPowerReceptor(direction);
	    
	    return rec != null && rec.canPipeConnect(direction);
	}
	
	public boolean canConnectToPipe(EnumFacing direction, TileEntityPipe conduit) {
	    boolean res = super.canConnectToPipe(direction, conduit);
	    if(!res) {
	      return false;
	    }
	    if( !(conduit instanceof TileEntityPipePowerCU)) {
	      return false;
	    }
	    return ((TileEntityPipePowerCU)conduit).getSubType() == getSubType();
	    /*TileEntityPipePowerCU pc = (TileEntityPipePowerCU)conduit;    
	    return pc.subtype == this.subtype;*/
	  }
	
	@Override
	public void externalConnectionAdded(EnumFacing direction) {
	    super.externalConnectionAdded(direction);
	    if(network != null) {
	      BlockPos p = getPos().offset(direction);
	      network.powerReceptorAdded(this, direction, p.getX(), p.getY(), p.getZ(),getExternalPowerReceptor(direction));
	    }
	}

	@Override
	public void externalConnectionRemoved(EnumFacing direction) {
	    super.externalConnectionRemoved(direction);
	    if(network != null) {
	      BlockPos p = getPos().offset(direction);
	      network.powerReceptorRemoved(p.getX(), p.getY(), p.getZ());
	    }
	}

    public IPowerInterface getExternalPowerReceptor(EnumFacing direction) {
    	if(getWorld() == null) {
		  return null;
		}
		TileEntity test = getWorld().getTileEntity(getPos().offset(direction));
		if(test == null) {
		  return null;
		}
		if(test instanceof TileEntityPipePowerCU) {
		  return null;
		}
		if (test instanceof ICEnergyConnection) {
		    return new PowerInterfaceCU((ICEnergyConnection) test);
		}     
		return null;
    }	
    
    public ItemStack getPipeDropped(){
    	ItemStack stack = new ItemStack(ModBlocks.crystalPipe, 1, PipeType.POWERCU.getMeta());
    	ItemNBTHelper.setInteger(stack, "Tier", getSubType());
    	return stack;
    }
}
