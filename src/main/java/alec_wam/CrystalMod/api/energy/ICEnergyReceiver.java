package alec_wam.CrystalMod.api.energy;

import net.minecraft.util.EnumFacing;


public interface ICEnergyReceiver extends ICEnergyHandler {

	int fillCEnergy(EnumFacing from, int maxReceive, boolean simulate);

}

