package alec_wam.CrystalMod.tiles.pipes.power.rf;

import alec_wam.CrystalMod.tiles.pipes.power.IPowerInterface;
import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.util.EnumFacing;

public class PowerInterfaceRF implements IPowerInterface {

  

  private final IEnergyConnection con;
  private IEnergyHandler eh;
  private IEnergyReceiver er;

  public PowerInterfaceRF(IEnergyConnection con) {       
    this.con = con;
    if(con instanceof IEnergyHandler) {
      eh = (IEnergyHandler)con;
    }
    if(con instanceof IEnergyReceiver) {
      er = (IEnergyReceiver)con;
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
    return con.canConnectEnergy(from);
  }

  @Override
  public int getEnergyStored(EnumFacing dir) {
    if (eh == null) {
    	return 0;
    }
    return eh.getEnergyStored(dir);
  }

  @Override
  public int getMaxEnergyStored(EnumFacing dir) {
    if (eh == null) {
      return 0;
    }
    return eh.getMaxEnergyStored(dir);

  }

  @Override
  public int getPowerRequest(EnumFacing dir) {
    if(er == null) {
    	return 0;
    }
    return er.receiveEnergy(dir, 9999999, true);
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
    return er.receiveEnergy(dir, canOffer, false);
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
