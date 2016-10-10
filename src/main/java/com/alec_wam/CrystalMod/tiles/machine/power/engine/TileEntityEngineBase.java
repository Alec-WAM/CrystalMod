package com.alec_wam.CrystalMod.tiles.machine.power.engine;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import com.alec_wam.CrystalMod.api.energy.CEnergyStorage;
import com.alec_wam.CrystalMod.api.energy.ICEnergyProvider;
import com.alec_wam.CrystalMod.api.energy.ICEnergyReceiver;
import com.alec_wam.CrystalMod.network.CrystalModNetwork;
import com.alec_wam.CrystalMod.network.IMessageHandler;
import com.alec_wam.CrystalMod.network.packets.PacketTileMessage;
import com.alec_wam.CrystalMod.tiles.TileEntityMod;
import com.alec_wam.CrystalMod.tiles.machine.IMachineTile;
import com.alec_wam.CrystalMod.util.data.watchable.WatchableInteger;

public abstract class TileEntityEngineBase extends TileEntityMod implements ICEnergyProvider, IMessageHandler, IMachineTile {

	public CEnergyStorage energyStorage;
	public WatchableInteger fuel = new WatchableInteger();
	public WatchableInteger maxFuel = new WatchableInteger();
	public int multi;
	public int facing;
	
	public TileEntityEngineBase(){
		multi = 1;
		energyStorage = createStorage(multi);
	}
	
	public TileEntityEngineBase(int multi){
		this.multi = multi;
		energyStorage = createStorage(this.multi);
	}
	
	public void updateMulti(int multi){
		this.multi = multi;
		final int energy = this.energyStorage.getCEnergyStored();
		energyStorage = createStorage(this.multi);
		energyStorage.setEnergyStored(energy);
	}
	
	public abstract CEnergyStorage createStorage(int multi);
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
	    nbt.setInteger("Fuel", fuel.getValue());
	    nbt.setInteger("MaxFuel", maxFuel.getValue());
	    nbt.setInteger("Multi", this.multi);
	    nbt.setInteger("Facing", facing);
	    this.energyStorage.writeToNBT(nbt);
	}
	
	public void readCustomNBT(NBTTagCompound nbt)
	{
	    super.readCustomNBT(nbt);
	    this.fuel.setValue(nbt.getInteger("Fuel"));
	    this.maxFuel.setValue(nbt.getInteger("MaxFuel"));
	    this.multi = nbt.getInteger("Multi");
	    this.facing = nbt.getInteger("Facing");
	    this.energyStorage.readFromNBT(nbt);
	}
	
	protected float lastSyncPowerStored = -1;
	
	public void update(){
		super.update();
		if(!getWorld().isRemote){
			if(this.energyStorage.getCEnergyStored() < this.energyStorage.getMaxCEnergyStored()){
				if(canRefuel()){
					refuel();
				}else{
					for(int m = 0; (m < this.multi && fuel.getValue() > 0 && this.energyStorage.getCEnergyStored() < this.energyStorage.getMaxCEnergyStored()); m++){
				  		final int calc = getFuelValue();
				    	this.energyStorage.modifyEnergyStored(calc);
				    	fuel.setValue(fuel.getValue()-1);
				    	if(this.fuel.getValue() < 0){
				    		fuel.setValue(0);
				    	}
					}
				}
			}
			
			boolean powerChanged = (lastSyncPowerStored != energyStorage.getCEnergyStored() && shouldDoWorkThisTick(5));
		    if(powerChanged) {
		      lastSyncPowerStored = energyStorage.getCEnergyStored();
		      NBTTagCompound nbt = new NBTTagCompound();
		      nbt.setInteger("Power", energyStorage.getCEnergyStored());
		      CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdatePower", nbt), this);
		    }
		    
		    boolean fuelChanged = (fuel.getLastValue() != fuel.getValue() && shouldDoWorkThisTick(5));
		    if(fuelChanged) {
		      fuel.setLastValue(fuel.getValue());
		      NBTTagCompound nbt = new NBTTagCompound();
		      nbt.setInteger("Fuel", fuel.getValue());
		      CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateFuel", nbt), this);
		    }
		    
		    boolean maxChanged = (maxFuel.getLastValue() != maxFuel.getValue() && shouldDoWorkThisTick(5));
		    if(maxChanged) {
		      maxFuel.setLastValue(maxFuel.getValue());
		      NBTTagCompound nbt = new NBTTagCompound();
		      nbt.setInteger("Max", maxFuel.getValue());
		      CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateFuel", nbt), this);
		    }
			
			for(EnumFacing face : EnumFacing.VALUES){
				//if(face !=EnumFacing.getFront(facing)){
					TileEntity tile = this.getWorld().getTileEntity(getPos().offset(face));
					if(tile !=null && tile instanceof ICEnergyReceiver){
						ICEnergyReceiver rec = (ICEnergyReceiver)tile;
						if(!rec.canConnectCEnergy(face))continue;
						int fill = rec.fillCEnergy(face.getOpposite(), Math.min(energyStorage.getMaxExtract(), energyStorage.getCEnergyStored()), false);
						if(fill > 0){
							this.energyStorage.modifyEnergyStored(-fill);
						}
					}
				//}
			}
		}
		
	}
	
	public boolean canRefuel(){
		return this.energyStorage.getCEnergyStored() < this.energyStorage.getMaxCEnergyStored() && fuel.getValue() <= 0;
	}
	
	public abstract void refuel();
	
	public abstract int getFuelValue();
	
	@Override
	public boolean canConnectCEnergy(EnumFacing from) {
		return from !=EnumFacing.getFront(facing).getOpposite();
	}

	@Override
	public int getCEnergyStored(EnumFacing from) {
		return energyStorage.getCEnergyStored();
	}

	@Override
	public int getMaxCEnergyStored(EnumFacing from) {
		return energyStorage.getMaxCEnergyStored();
	}



	public int getScaledFuel(int scale) {
		if ((this.maxFuel.getValue() <= 0) || (this.fuel.getValue() <= 0)) {
	        return 0;
	    }
		return scale * (this.maxFuel.getValue() - this.fuel.getValue()) / this.maxFuel.getValue();
	}
	
	public void setFacing(int facing){
		this.facing = facing;
	}
	
	public int getFacing(){
		return facing;
	}
	
	public boolean isActive(){
		return fuel.getValue() > 0;
	}
	
	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
		if(messageId.equalsIgnoreCase("UpdatePower")){
			int newPower = messageData.getInteger("Power");
			this.energyStorage.setEnergyStored(newPower);
		}
		
		if(messageId.equalsIgnoreCase("UpdateFuel")){
			int newFuel = messageData.hasKey("Fuel") ? messageData.getInteger("Fuel") : fuel.getValue();
			int newMax = messageData.hasKey("Max") ? messageData.getInteger("Max") : maxFuel.getValue();
			this.fuel.setValue(newFuel);
			this.maxFuel.setValue(newMax);
		}
	}
}
