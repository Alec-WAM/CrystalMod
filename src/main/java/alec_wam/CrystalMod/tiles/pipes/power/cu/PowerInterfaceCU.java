package alec_wam.CrystalMod.tiles.pipes.power.cu;

import alec_wam.CrystalMod.api.energy.ICEnergyStorage;
import alec_wam.CrystalMod.tiles.pipes.power.IPowerInterface;
import net.minecraft.util.EnumFacing;

public class PowerInterfaceCU implements IPowerInterface {

  private ICEnergyStorage storage;	
	
  public PowerInterfaceCU(ICEnergyStorage con) {       
    this.storage = con;
  }

  @Override
  public Object getDelegate() {
    return storage;
  }

  @Override
  public boolean canPipeConnect(EnumFacing from) {
    if(from != null) {
      from = from.getOpposite();
    }
    return true;
  }

  @Override
  public int getEnergyStored(EnumFacing dir) {
    if (storage == null) {
    	return 0;
    }
    return storage.getCEnergyStored();
  }

  @Override
  public int getMaxEnergyStored(EnumFacing dir) {
    if (storage == null) {
      return 0;
    }
    return storage.getMaxCEnergyStored();

  }

  @Override
  public int getPowerRequest(EnumFacing dir) {
    if(storage == null) {
    	return 0;
    }
    return storage.fillCEnergy(9999999, true);
  }

  @Override
  public int getMinEnergyReceived(EnumFacing dir) {
    return 0;
  }

  @Override
  public int fillEnergy(EnumFacing dir, int canOffer, boolean simulate) {
    if(storage == null) {
    	return 0;
    }
    return storage.fillCEnergy(canOffer, simulate);
  }

  @Override
  public boolean isOutputOnly() {
    return !storage.canReceive();
  }

  @Override
  public boolean isInputOnly() {
    return false;
  }

}
