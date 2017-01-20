package alec_wam.CrystalMod.tiles.machine.power.converter;

import alec_wam.CrystalMod.api.energy.ICEnergyReceiver;
import alec_wam.CrystalMod.tiles.machine.power.CustomEnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

public class TileEnergyConverterCUtoRF extends TileEnergyConveterBase implements ICEnergyReceiver {
	CustomEnergyStorage energyStorage;
	
	public TileEnergyConverterCUtoRF()
	{
	    this.energyStorage = new CustomEnergyStorage(5000){
	    	@Override
	    	public boolean canReceive(){
	    		return false;
	    	}
	    };
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
	      handler.receiveEnergy(face, energyStorage.extractEnergy(80, false), false);
	    }
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		energyStorage.writeToNBT(nbt);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		energyStorage.readFromNBT(nbt);
	}
	
	@Override
	public boolean canConnectCEnergy(EnumFacing from) {
		return true;
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
	
	@Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing){
		 return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }
	
	@SuppressWarnings("unchecked")
	@Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing){
        if(capability == CapabilityEnergy.ENERGY){
            if(energyStorage != null){
                return (T)energyStorage;
            }
        }
        return super.getCapability(capability, facing);
	}

}
