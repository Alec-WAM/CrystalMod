package com.alec_wam.CrystalMod.tiles.pipes.power;

import net.minecraft.util.EnumFacing;

public interface IPowerInterface {

  Object getDelegate();

  boolean canPipeConnect(EnumFacing direction);

  int getEnergyStored(EnumFacing dir);

  int getMaxEnergyStored(EnumFacing dir);

  int getPowerRequest(EnumFacing dir);

  int getMinEnergyReceived(EnumFacing dir);

  int fillEnergy(EnumFacing opposite, int canOffer);

  boolean isOutputOnly();

  boolean isInputOnly();

}
