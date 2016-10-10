package com.alec_wam.CrystalMod.tiles.machine.inventory.charger;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileEntityInventoryChargerRF extends TileEntityInventoryCharger implements IEnergyReceiver {

	public EnergyStorage energyStorage;
	
	public TileEntityInventoryChargerRF(){
		energyStorage = new EnergyStorage(20000);
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
		return stack !=null && stack.getItem() !=null && stack.getItem() instanceof cofh.api.energy.IEnergyContainerItem && stack.stackSize == 1;
	}

	@Override
	public void chargeItem(ItemStack stack) {
		if(!canChargeItem(stack))return;
		IEnergyContainerItem chargable = (IEnergyContainerItem)stack.getItem();
		int max = chargable.getMaxEnergyStored(stack);
        int cur = chargable.getEnergyStored(stack);
        int canUse = Math.min(energyStorage.getEnergyStored(), max - cur);
        if(cur < max) {
          int used = chargable.receiveEnergy(stack, canUse, true);
          if(used > 0) {
        	  if(energyStorage.extractEnergy(used, false) >=used){
        		  chargable.receiveEnergy(stack, used, false);
        	  }
          }
        }
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return energyStorage.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return from.ordinal() !=facing;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		int fill = energyStorage.receiveEnergy(maxReceive, simulate);
		return fill;
	}

	@Override
	protected int getEnergyStored() {
		return energyStorage.getEnergyStored();
	}

	@Override
	protected void setEnergyStored(int energy) {
		energyStorage.setEnergyStored(energy);
	}

}
