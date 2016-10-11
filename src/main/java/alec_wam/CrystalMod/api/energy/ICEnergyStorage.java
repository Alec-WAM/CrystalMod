package alec_wam.CrystalMod.api.energy;

public interface ICEnergyStorage {

	int fillCEnergy(int maxReceive, boolean simulate);

	int drainCEnergy(int maxExtract, boolean simulate);

	int getCEnergyStored();

	int getMaxCEnergyStored();

}
