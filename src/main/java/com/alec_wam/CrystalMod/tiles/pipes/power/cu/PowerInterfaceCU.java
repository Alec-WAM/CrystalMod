package com.alec_wam.CrystalMod.tiles.pipes.power.cu;

import com.alec_wam.CrystalMod.api.energy.ICEnergyConnection;
import com.alec_wam.CrystalMod.api.energy.ICEnergyHandler;
import com.alec_wam.CrystalMod.api.energy.ICEnergyReceiver;
import com.alec_wam.CrystalMod.tiles.pipes.power.IPowerInterface;

import net.minecraft.util.EnumFacing;

public class PowerInterfaceCU implements IPowerInterface {

  

  private final ICEnergyConnection con;
  private ICEnergyHandler eh;
  private ICEnergyReceiver er;

  public PowerInterfaceCU(ICEnergyConnection con) {       
    this.con = con;
    if(con instanceof ICEnergyHandler) {
      eh = (ICEnergyHandler)con;
    }
    if(con instanceof ICEnergyReceiver) {
      er = (ICEnergyReceiver)con;
    }       
  }

  @Override
  public Object getDelegate() {
    return con;
  }

  @Override
  public boolean canPipeConnect(EnumFacing from) {
    if(from != null) {
      from = from.getOpposite();
    }
    return con.canConnectCEnergy(from);
  }

  @Override
  public int getEnergyStored(EnumFacing dir) {
    if (eh == null) {
    	return 0;
    }
    return eh.getCEnergyStored(dir);
  }

  @Override
  public int getMaxEnergyStored(EnumFacing dir) {
    if (eh == null) {
      return 0;
    }
    return eh.getMaxCEnergyStored(dir);

  }

  @Override
  public int getPowerRequest(EnumFacing dir) {
    if(er == null) {
    	return 0;
    }
    return er.fillCEnergy(dir, 9999999, true);
  }

  @Override
  public int getMinEnergyReceived(EnumFacing dir) {
    return 0;
  }

  @Override
  public int fillEnergy(EnumFacing dir, int canOffer) {
    if(er == null) {
    	return 0;
    }
    return er.fillCEnergy(dir, canOffer, false);
  }

  @Override
  public boolean isOutputOnly() {
    return er == null;
  }

  @Override
  public boolean isInputOnly() {
    return false;
  }

}
