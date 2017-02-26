package alec_wam.CrystalMod.tiles.machine.power.converter;

import alec_wam.CrystalMod.api.energy.CapabilityCrystalEnergy;
import alec_wam.CrystalMod.api.energy.ICEnergyStorage;
import alec_wam.CrystalMod.tiles.machine.power.CustomEnergyStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEnergyConverterCUtoRF extends TileEnergyConveterBase {
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
	
	@Override
	public void update(){
		super.update();
		if(this.getWorld().isRemote)return;
		
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
	    if(tile !=null && tile.hasCapability(CapabilityEnergy.ENERGY, face)){
	    	IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, face);
	    	if(storage !=null)storage.receiveEnergy(energyStorage.extractEnergy(80, false), false);
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
		 return capability == CapabilityEnergy.ENERGY || capability == CapabilityCrystalEnergy.CENERGY ||super.hasCapability(capability, facing);
    }
	
	@SuppressWarnings("unchecked")
	@Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing){
        if(capability == CapabilityEnergy.ENERGY){
            if(energyStorage != null){
                return (T)energyStorage;
            }
        }
        if(capability == CapabilityCrystalEnergy.CENERGY){
            return (T) new ICEnergyStorage(){

				@Override
				public int fillCEnergy(int maxReceive, boolean simulate) {
					final int cuDemand = (int) Math.floor( TileEnergyConverterCUtoRF.this.getPowerNeeded( PowerUnits.CU, maxReceive ) );
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
					return (int) Math.floor( getUnitType().convertTo( PowerUnits.CU, TileEnergyConverterCUtoRF.this.energyStorage.getEnergyStored() ) );
				}

				@Override
				public int getMaxCEnergyStored() {
					return (int) Math.floor( getUnitType().convertTo( PowerUnits.CU, TileEnergyConverterCUtoRF.this.energyStorage.getMaxEnergyStored() ) );
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

}
