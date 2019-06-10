package alec_wam.CrystalMod.api.energy;

import net.minecraft.item.ItemStack;

public interface ICEnergyContainerItem {

	int fillCEnergy(ItemStack container, int maxReceive, boolean simulate);
	
	int drainCEnergy(ItemStack container, int maxExtract, boolean simulate);
	
	int getCEnergyStored(ItemStack container);
	
	int getMaxCEnergyStored(ItemStack container);
	
}
