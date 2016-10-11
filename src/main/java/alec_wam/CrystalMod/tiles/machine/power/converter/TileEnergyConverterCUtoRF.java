package alec_wam.CrystalMod.tiles.machine.power.converter;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import alec_wam.CrystalMod.api.energy.ICEnergyReceiver;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;

public class TileEnergyConverterCUtoRF extends TileEnergyConveterBase implements IEnergyProvider, ICEnergyReceiver {
	EnergyStorage energyStorage;
	
	public TileEnergyConverterCUtoRF()
	{
	    this.energyStorage = new EnergyStorage(5000);
	}
	
	public void update(){
		if(this.worldObj.isRemote)return;
		
		if (this.energyStorage !=null)
	    {
	      for (EnumFacing face : EnumFacing.VALUES) {
	        if(this.energyStorage.getEnergyStored() > 0)transferEnergy(face);
	      }
	    }
	}
	
	protected void transferEnergy(EnumFacing face)
	{
	    TileEntity tile= getWorld().getTileEntity(getPos().offset(face));
	    if(tile !=null && tile instanceof IEnergyReceiver){
	      IEnergyReceiver handler = (IEnergyReceiver) tile;
	      if(!handler.canConnectEnergy(face))return;
	      int rec = handler.receiveEnergy(face, Math.min(this.energyStorage.getMaxExtract(), this.energyStorage.getEnergyStored()), false);
	      this.energyStorage.modifyEnergyStored(-(rec));
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
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return this.energyStorage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return this.energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return this.energyStorage.getMaxEnergyStored();
	}

	@Override
	public int fillCEnergy(EnumFacing from, int maxReceive, boolean simulate) {	
		final int cuDemand = (int) Math.floor( this.getPowerNeeded( PowerUnits.CU, maxReceive ) );
		final int usedCU = Math.min( maxReceive, cuDemand );

		if( !simulate )
		{
			injectExternalPower( PowerUnits.CU, usedCU );
		}

		return usedCU;
	}

	@Override
	public int getCEnergyStored(EnumFacing from) {
		return (int) Math.floor( getUnitType().convertTo( PowerUnits.CU, this.energyStorage.getEnergyStored() ) );
	}

	@Override
	public int getMaxCEnergyStored(EnumFacing from) {
		return (int) Math.floor( getUnitType().convertTo( PowerUnits.CU, this.energyStorage.getMaxEnergyStored() ) );
	}

	@Override
	public PowerUnits getUnitType() {
		return PowerUnits.RF;
	}

	@Override
	protected void setEnergyStored(int power) {
		energyStorage.setEnergyStored(power);
	}

	@Override
	protected int getEnergyStored() {
		return energyStorage.getEnergyStored();
	}

	@Override
	protected int getMaxEnergyStored() {
		return energyStorage.getMaxEnergyStored();
	}

}
