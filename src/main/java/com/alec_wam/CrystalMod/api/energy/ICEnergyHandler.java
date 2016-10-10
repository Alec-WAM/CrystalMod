package com.alec_wam.CrystalMod.api.energy;

import net.minecraft.util.EnumFacing;

public interface ICEnergyHandler extends ICEnergyConnection {

	int getCEnergyStored(EnumFacing from);

	int getMaxCEnergyStored(EnumFacing from);

}

