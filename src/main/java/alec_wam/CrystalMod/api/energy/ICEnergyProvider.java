package alec_wam.CrystalMod.api.energy;

import net.minecraft.util.EnumFacing;

public interface ICEnergyProvider extends ICEnergyHandler {

	int drainCEnergy(EnumFacing from, int maxExtract, boolean simulate);

}
