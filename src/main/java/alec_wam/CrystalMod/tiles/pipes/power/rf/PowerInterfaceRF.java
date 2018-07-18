package alec_wam.CrystalMod.tiles.pipes.power.rf;

import alec_wam.CrystalMod.tiles.pipes.power.IPowerInterface;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;

public class PowerInterfaceRF implements IPowerInterface {

  private IEnergyStorage storage;	
	
  public PowerInterfaceRF(IEnergyStorage con) {       
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
    return storage.getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored(EnumFacing dir) {
    if (storage == null) {
      return 0;
    }
    return storage.getMaxEnergyStored();

  }

  @Override
  public int getPowerRequest(EnumFacing dir) {
    if(storage == null) {
    	return 0;
    }
    return storage.receiveEnergy(9999999, true);
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
    return storage.receiveEnergy(canOffer, simulate);
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
