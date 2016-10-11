package alec_wam.CrystalMod.tiles.pipes.power.rf;

import java.util.EnumMap;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.tiles.pipes.AbstractPipeNetwork;
import alec_wam.CrystalMod.tiles.pipes.ConnectionMode;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import alec_wam.CrystalMod.tiles.pipes.BlockPipe.PipeType;
import alec_wam.CrystalMod.tiles.pipes.power.IPowerInterface;
import alec_wam.CrystalMod.tiles.pipes.types.IPipeType;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

public class TileEntityPipePowerRF extends TileEntityPipe implements IEnergyReceiver, IEnergyConnection {

	protected RFPowerPipeNetwork network;

	private int energyStoredRF;

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
		nbt.setInteger("energyStoredRF", energyStoredRF);
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		this.subtype = nbt.getInteger("subtype");
		
		this.setEnergyStored(nbt.getInteger("energyStoredRF"));
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
	    this.network = (RFPowerPipeNetwork) network;
	    super.setNetwork(network);
	    return true;
	}
	
	public void setEnergyStored(int energy){
		energyStoredRF = MathHelper.clamp_int(energy, 0, getMaxEnergyStored()); 
	}
	
	public int getEnergyStored(){
		return energyStoredRF;
	}
	
	@Override
	public int getEnergyStored(EnumFacing from) {
		return getEnergyStored();
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
	public int getMaxEnergyStored(EnumFacing from) {
		return getMaxEnergyStored();
	}

	static int getMaxEnergyIO(int subtype) {
	    switch (subtype) {
	    case 1:
	      return Config.powerConduitTierTwoRF;
	    case 2:
	      return Config.powerConduitTierThreeRF;
	    case 3:
		      return Config.powerConduitTierThreeRF;
	    default:
	      return Config.powerConduitTierOneRF;
	    }
	}

	public int getMaxEnergyStored() {
	    return getMaxEnergyIO(subtype);
	}
	
	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return this.getConnectionMode(from) != ConnectionMode.DISABLED;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		if(getMaxEnergyRecieved(from) == 0 || maxExtract <= 0) {
	      return 0;
	    }
	    int freeSpace = getMaxEnergyStored() - getEnergyStored();
	    int result = Math.min(maxExtract, freeSpace);
	    if(!simulate && result > 0) {
	      setEnergyStored(getEnergyStored() + result);      

	      if(recievedTicks == null) {
	          recievedTicks = new EnumMap<EnumFacing, Long>(EnumFacing.class);
	        }
	        recievedTicks.put(from, getWorld().getTotalWorldTime());

	    }
	    return result;
	}

	@Override
	public IPipeType getPipeType() {
		return PowerRFType.INSTANCE;
	}

	@Override
	public AbstractPipeNetwork createNetwork() {
		return new RFPowerPipeNetwork();
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
	    if( !(conduit instanceof TileEntityPipePowerRF)) {
	      return false;
	    }
	    return ((TileEntityPipePowerRF)conduit).getSubType() == getSubType();
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
		if(test instanceof TileEntityPipePowerRF) {
		  return null;
		}
		if (test instanceof IEnergyConnection) {
		    return new PowerInterfaceRF((IEnergyConnection) test);
		}     
		return null;
    }	
    
    public ItemStack getPipeDropped(){
    	ItemStack stack = new ItemStack(ModBlocks.crystalPipe, 1, PipeType.POWERRF.getMeta());
    	ItemNBTHelper.setInteger(stack, "Tier", getSubType());
    	return stack;
    }
}
