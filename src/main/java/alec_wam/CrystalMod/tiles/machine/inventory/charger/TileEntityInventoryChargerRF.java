package alec_wam.CrystalMod.tiles.machine.inventory.charger;

import alec_wam.CrystalMod.tiles.machine.power.CustomEnergyStorage;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityInventoryChargerRF extends TileEntityInventoryCharger {

	public CustomEnergyStorage energyStorage;
	
	public TileEntityInventoryChargerRF(){
		energyStorage = new CustomEnergyStorage(20000) {
			@Override
			public boolean canExtract(){
				return false;
			}
		};
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		NBTTagCompound eNBT = new NBTTagCompound();
		this.energyStorage.writeToNBT(eNBT);
		nbt.setTag("EnergyStorage", eNBT);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		if(nbt.hasKey("EnergyStorage"))this.energyStorage.readFromNBT(nbt.getCompoundTag("EnergyStorage"));
		updateAfterLoad();
	}
	
	@Override
	public boolean canChargeItem(ItemStack stack) {
		return ItemStackTools.isValid(stack) && stack.getItem() !=null && stack.hasCapability(CapabilityEnergy.ENERGY, null) && ItemStackTools.getStackSize(stack) == 1;
	}

	@Override
	public void chargeItem(ItemStack stack) {
		if(!canChargeItem(stack))return;
		IEnergyStorage cap = stack.getCapability(CapabilityEnergy.ENERGY, null);
		if(cap == null)return;
		int max = cap.getMaxEnergyStored();
        int cur = cap.getEnergyStored();
        int canUse = Math.min(energyStorage.getEnergyStored(), max - cur);
        if(cur < max) {
          int used = cap.receiveEnergy(canUse, true);
          if(used > 0) {
        	  if(energyStorage.extractEnergy(used, false) >=used){
        		  cap.receiveEnergy(used, false);
        	  }
          }
        }
	}

	@Override
	protected int getEnergyStored() {
		return energyStorage.getEnergyStored();
	}

	@Override
	protected void setEnergyStored(int energy) {
		energyStorage.setEnergyStored(energy);
	}
	
	@Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing){
        return this.getCapability(capability, facing) != null;
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
