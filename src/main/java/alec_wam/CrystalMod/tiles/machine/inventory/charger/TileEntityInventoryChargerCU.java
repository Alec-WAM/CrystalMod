package alec_wam.CrystalMod.tiles.machine.inventory.charger;

import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.api.energy.CapabilityCrystalEnergy;
import alec_wam.CrystalMod.api.energy.ICEnergyContainerItem;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class TileEntityInventoryChargerCU extends TileEntityInventoryCharger {

	public CEnergyStorage energyStorage;
	
	public TileEntityInventoryChargerCU(){
		energyStorage = new CEnergyStorage(20000) {
			@Override
			public boolean canExtract(){
				return false;
			}
		};
	}
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		NBTTagCompound eNBT = new NBTTagCompound();
		this.energyStorage.writeToNBT(eNBT);
		nbt.setTag("EnergyStorage", eNBT);
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		if(nbt.hasKey("EnergyStorage"))this.energyStorage.readFromNBT(nbt.getCompoundTag("EnergyStorage"));
		updateAfterLoad();
	}
	
	@Override
	public boolean canChargeItem(ItemStack stack) {
		return ItemStackTools.isValid(stack) && stack.getItem() !=null && stack.getItem() instanceof alec_wam.CrystalMod.api.energy.ICEnergyContainerItem && ItemStackTools.getStackSize(stack) == 1;
	}

	@Override
	public void chargeItem(ItemStack stack) {
		if(!canChargeItem(stack))return;
		ICEnergyContainerItem chargable = (ICEnergyContainerItem)stack.getItem();
		int max = chargable.getMaxCEnergyStored(stack);
        int cur = chargable.getCEnergyStored(stack);
        int canUse = Math.min(energyStorage.getCEnergyStored(), max - cur);
        if(cur < max) {
          int used = chargable.fillCEnergy(stack, canUse, true);
          if(used > 0) {
        	  if(energyStorage.drainCEnergy(used, false) >=used){
        		  chargable.fillCEnergy(stack, used, false);
        	  }
          }
        }
	}

	@Override
	protected int getEnergyStored() {
		return energyStorage.getCEnergyStored();
	}

	@Override
	protected void setEnergyStored(int energy) {
		energyStorage.setEnergyStored(energy);
	}
	
	@Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
	  if(capability == CapabilityCrystalEnergy.CENERGY){
		  return facingIn.ordinal() !=facing;
	  }
      return super.hasCapability(capability, facingIn);
    }

	@SuppressWarnings("unchecked")
	@Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityCrystalEnergy.CENERGY) {
            return facing.ordinal() !=this.facing ? (T) energyStorage : super.getCapability(capability, facing);
        }
        return super.getCapability(capability, facing);
    }

}
