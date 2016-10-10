package com.alec_wam.CrystalMod.tiles.machine.power.converter;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import cofh.api.energy.IEnergyReceiver;

import com.alec_wam.CrystalMod.api.energy.CEnergyStorage;
import com.alec_wam.CrystalMod.api.energy.ICEnergyProvider;
import com.alec_wam.CrystalMod.api.energy.ICEnergyReceiver;

public class TileEnergyConverterRFtoCU extends TileEnergyConveterBase implements IEnergyReceiver, ICEnergyProvider{
	CEnergyStorage energyStorage;
	
	public TileEnergyConverterRFtoCU()
	{
	    this.energyStorage = new CEnergyStorage(5000);
	}
	
	public void update(){
		if(this.worldObj.isRemote)return;
		
		if (this.energyStorage !=null)
	    {
	      for (EnumFacing face : EnumFacing.VALUES) {
	        if(energyStorage.getCEnergyStored() > 0)transferEnergy(face);
	        else break;
	      }
	    }
	}
	
	protected void transferEnergy(EnumFacing face)
	{
	    TileEntity tile = getWorld().getTileEntity(getPos().offset(face));
	    if(tile !=null && tile instanceof ICEnergyReceiver){
	      ICEnergyReceiver handler = (ICEnergyReceiver) tile;
	      if(handler.canConnectCEnergy(face))this.energyStorage.modifyEnergyStored(-handler.fillCEnergy(face, Math.min(this.energyStorage.getMaxExtract(), this.energyStorage.getCEnergyStored()), false));
	    }
	}
	
	public void writeCustomNBT(NBTTagCompound nbt){
		this.energyStorage.writeToNBT(nbt);
	}
	public void readCustomNBT(NBTTagCompound nbt){
		this.energyStorage.readFromNBT(nbt);
	}
	
	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}
	@Override
	public boolean canConnectCEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		final int rfDemand = (int) Math.floor( this.getPowerNeeded( PowerUnits.RF, maxReceive ) );
		final int usedRF = Math.min( maxReceive, rfDemand );

		if( !simulate )
		{
			injectExternalPower( PowerUnits.RF, usedRF );
		}

		return usedRF;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return (int) Math.floor( getUnitType().convertTo( PowerUnits.RF, this.energyStorage.getCEnergyStored() ) );
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return (int) Math.floor( getUnitType().convertTo( PowerUnits.RF, this.energyStorage.getMaxCEnergyStored() ) );
	}
	
	@Override
	public int drainCEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return this.energyStorage.drainCEnergy(maxExtract, simulate);
	}

	@Override
	public int getCEnergyStored(EnumFacing from) {
		return this.energyStorage.getCEnergyStored();
	}

	@Override
	public int getMaxCEnergyStored(EnumFacing from) {
		return this.energyStorage.getMaxCEnergyStored();
	}

	@Override
	public PowerUnits getUnitType() {
		return PowerUnits.CU;
	}

	@Override
	protected void setEnergyStored(int power) {
		energyStorage.setEnergyStored(power);
	}

	@Override
	protected int getEnergyStored() {
		return energyStorage.getCEnergyStored();
	}

	@Override
	protected int getMaxEnergyStored() {
		return energyStorage.getMaxCEnergyStored();
	}

}
