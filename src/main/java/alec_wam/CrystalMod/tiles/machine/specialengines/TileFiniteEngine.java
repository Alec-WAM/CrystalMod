package alec_wam.CrystalMod.tiles.machine.specialengines;

import alec_wam.CrystalMod.api.energy.CapabilityCrystalEnergy;
import alec_wam.CrystalMod.api.energy.ICEnergyStorage;
import alec_wam.CrystalMod.handler.EventHandler;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.machine.power.CustomEnergyStorage;
import alec_wam.CrystalMod.tiles.machine.power.converter.PowerUnits;
import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.CapabilityEnergy;

public class TileFiniteEngine extends TileEntityMod implements IMessageHandler {

	public CustomEnergyStorage energyStorage;
	protected int lastSyncPowerStored = -1;
	public boolean isRunning;
	public boolean lastIsRunning;
	
	public TileFiniteEngine() {
		energyStorage = new CustomEnergyStorage(100000, 200, 200){
			@Override
			public boolean canExtract(){
				return false;
			}
		};
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		energyStorage.writeToNBT(nbt);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		energyStorage.readFromNBT(nbt);
	}
	
	@Override
	public void update(){
		super.update();
		
		if(!getWorld().isRemote){
			boolean powerChanged = (lastSyncPowerStored != energyStorage.getEnergyStored() && shouldDoWorkThisTick(5));
		    if(powerChanged) {
		      lastSyncPowerStored = energyStorage.getEnergyStored();
		      NBTTagCompound nbt = new NBTTagCompound();
		      nbt.setInteger("Power", energyStorage.getEnergyStored());
		      CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdatePower", nbt), this);
		    }
		    
		    boolean runningChanged = (lastIsRunning != isRunning && shouldDoWorkThisTick(5));
		    if(runningChanged) {
		      lastIsRunning = isRunning;
		      NBTTagCompound nbt = new NBTTagCompound();
		      nbt.setBoolean("Running", isRunning);
		      CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateRunning", nbt), this);
		    }
		    
		    if(getWorld().isBlockPowered(getPos())){
		    	if(energyStorage.getEnergyStored() >= 50){
		    		energyStorage.setEnergyStored(energyStorage.getEnergyStored()-50);
		    		isRunning = true;
		    		EventHandler.addFiniteEngine(getWorld().provider.getDimension(), getPos());
		    		if(lastIsRunning !=isRunning){
			    		BlockUtil.markBlockForUpdate(getWorld(), getPos());
					}
		    	} else {
		    		isRunning = false;
		    		EventHandler.removeFiniteEngine(getWorld().provider.getDimension(), getPos());
		    		if(lastIsRunning !=isRunning){
			    		BlockUtil.markBlockForUpdate(getWorld(), getPos());
					}
		    	}
		    } else {
		    	isRunning = false;
	    		EventHandler.removeFiniteEngine(getWorld().provider.getDimension(), getPos());
	    		if(lastIsRunning !=isRunning){
		    		BlockUtil.markBlockForUpdate(getWorld(), getPos());
				}
		    }
		}
	}
	
	@Override
	public void invalidate(){
		super.invalidate();
		if(getWorld() !=null && getWorld().provider !=null)EventHandler.removeFiniteEngine(getWorld().provider.getDimension(), getPos());		
	}
	
	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
		if(messageId.equalsIgnoreCase("UpdatePower")){
			int newPower = messageData.getInteger("Power");
			energyStorage.setEnergyStored(newPower);
		}
		if(messageId.equalsIgnoreCase("UpdateRunning")){
			boolean newRunning = messageData.getBoolean("Running");
			lastIsRunning = isRunning;
			isRunning = newRunning;
			if(lastIsRunning !=isRunning){
	    		BlockUtil.markBlockForUpdate(getWorld(), getPos());
			}
		}
	}
	
	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing)
    {
		return capability == CapabilityEnergy.ENERGY || capability == CapabilityCrystalEnergy.CENERGY || super.hasCapability(capability, facing);
	}
	
	@SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing)
    {
        if (facing != null && capability == CapabilityEnergy.ENERGY){
            return (T) energyStorage;
        }
        if(capability == CapabilityCrystalEnergy.CENERGY){
            return (T) new ICEnergyStorage(){

				@Override
				public int fillCEnergy(int maxReceive, boolean simulate) {
					final int cuDemand = (int) Math.floor(getPowerNeeded( PowerUnits.CU, maxReceive ));
					final int usedCU = Math.min( maxReceive, cuDemand );

					if( !simulate )
					{
						injectExternalPower( PowerUnits.CU, usedCU );
					}

					return usedCU;
				}

				@Override
				public int drainCEnergy(int maxExtract, boolean simulate) {
					return 0;
				}

				@Override
				public int getCEnergyStored() {
					return (int) Math.floor( PowerUnits.RF.convertTo( PowerUnits.CU, energyStorage.getEnergyStored() ) );					
				}

				@Override
				public int getMaxCEnergyStored() {
					return (int) Math.floor( PowerUnits.RF.convertTo( PowerUnits.CU, energyStorage.getMaxEnergyStored() ) );
				}

				@Override
				public boolean canExtract() {
					return false;
				}

				@Override
				public boolean canReceive() {
					return true;
				}
            	
            };
        }
        return super.getCapability(capability, facing);
    }
	
	protected final int getPowerNeeded( final PowerUnits externalUnit, final int maxPowerRequired )
	{
		return PowerUnits.RF.convertTo( externalUnit, Math.max( 0, getFunnelPowerNeeded( externalUnit.convertTo( PowerUnits.RF, maxPowerRequired ) ) ) );
	}
	
	protected int getFunnelPowerNeeded( final double maxRequired )
	{
		return energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
	}
	
	public final int injectExternalPower( final PowerUnits input, final int amt )
	{
		return PowerUnits.RF.convertTo( input, this.funnelPowerIntoStorage( input.convertTo( PowerUnits.RF, amt ), false ) );
	}

	protected int funnelPowerIntoStorage( int amt, final boolean sim )
	{
		if( sim )
		{
			final int fakeBattery = energyStorage.getEnergyStored() + amt;

			if( fakeBattery > energyStorage.getMaxEnergyStored() )
			{
				return fakeBattery - energyStorage.getMaxEnergyStored();
			}

			return 0;
		}
		else
		{
			final int old = energyStorage.getEnergyStored();
			energyStorage.setEnergyStored( energyStorage.getEnergyStored() + amt );
			if( old+amt > energyStorage.getMaxEnergyStored() )
			{
				amt = old - energyStorage.getMaxEnergyStored();
				return amt;
			}

			return 0;
		}
	}

}
