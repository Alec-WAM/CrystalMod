package alec_wam.CrystalMod.tiles.machine.power.converter;

import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.api.energy.ICEnergyProvider;
import alec_wam.CrystalMod.api.energy.ICEnergyReceiver;
import alec_wam.CrystalMod.tiles.machine.power.CustomEnergyStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

public class TileEnergyConverterRFtoCU extends TileEnergyConveterBase implements ICEnergyProvider{
	CEnergyStorage energyStorage;
	CustomEnergyStorage rfEnergyStorage;
	
	public TileEnergyConverterRFtoCU()
	{
	    this.energyStorage = new CEnergyStorage(5000);
	    this.rfEnergyStorage = new CustomEnergyStorage(5000){
	    	@Override
	    	public boolean canExtract(){
	    		return false;
	    	}
	    	
	    	@Override
	        public int receiveEnergy(int maxReceive, boolean simulate){
	            if(!this.canReceive()){
	                return 0;
	            }
	            final int rfDemand = (int) Math.floor( TileEnergyConverterRFtoCU.this.getPowerNeeded( PowerUnits.RF, maxReceive ) );
	    		final int usedRF = Math.min( maxReceive, rfDemand );

	    		if( !simulate )
	    		{
	    			injectExternalPower( PowerUnits.RF, usedRF );
	    		}

	    		return usedRF;
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
	public boolean canConnectCEnergy(EnumFacing from) {
		return true;
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
