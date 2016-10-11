package alec_wam.CrystalMod.tiles.pipes.power.rf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import alec_wam.CrystalMod.tiles.pipes.power.IPowerInterface;
import alec_wam.CrystalMod.tiles.pipes.power.rf.RFPowerPipeNetwork.ReceptorEntry;
import alec_wam.CrystalMod.util.ModLogger;

public class NetworkRFPowerManager {

  private final RFPowerPipeNetwork network;

  int maxEnergyStored;
  int energyStored;

  private final List<ReceptorEntry> receptors = new ArrayList<RFPowerPipeNetwork.ReceptorEntry>();
  private ListIterator<ReceptorEntry> receptorIterator = receptors.listIterator();

  private final List<ReceptorEntry> storageReceptors = new ArrayList<ReceptorEntry>();

  private boolean receptorsDirty = true;

  public NetworkRFPowerManager(RFPowerPipeNetwork netowrk, World world) {
    network = netowrk;
    maxEnergyStored = 64;
  }

  public int getPowerInConduits() {
    return energyStored;
  }

  public int getMaxPowerInConduits() {
    return maxEnergyStored;
  }

  public long getPowerInReceptors() {
    long result = 0;
    Set<Object> done = new HashSet<Object>();
    for (ReceptorEntry re : receptors) {
      if(!re.emmiter.getConnectionsDirty()) {
        IPowerInterface powerReceptor = re.powerInterface;
        if(!done.contains(powerReceptor.getDelegate())) {
          done.add(powerReceptor.getDelegate());
          result += powerReceptor.getEnergyStored(re.direction);
        }
      }
    }
    return result;
  }

  public long getMaxPowerInReceptors() {
    long result = 0;
    Set<Object> done = new HashSet<Object>();
    for (ReceptorEntry re : receptors) {
      if(!re.emmiter.getConnectionsDirty()) {
        IPowerInterface powerReceptor = re.powerInterface;
        if(!done.contains(powerReceptor.getDelegate())) {
          done.add(powerReceptor.getDelegate());
          result += powerReceptor.getMaxEnergyStored(re.direction);
        }
      }
    }
    return result;
  }

  private int errorSupressionA = 0;
  private int errorSupressionB = 0;

  public void applyRecievedPower() {
    try {
      doApplyRecievedPower();
    } catch (Exception e) {
      if (errorSupressionA-- <= 0) {
        ModLogger.warning("NetworkPowerManager: Exception thrown when updating power network " + e);
        e.printStackTrace();
        errorSupressionA = 200;
        errorSupressionB = 20;
      } else if (errorSupressionB-- <= 0) {
        ModLogger.warning("NetworkPowerManager: Exception thrown when updating power network " + e);
        errorSupressionB = 20;
      }
    }
  }

  public void doApplyRecievedPower() {

    checkReceptors();

    // Update our energy stored based on what's in our conduits
    updateNetorkStorage();

    int appliedCount = 0;
    int numReceptors = receptors.size();
    int available = energyStored;
    int wasAvailable = available;

    if(available <= 0 || (receptors.isEmpty() && storageReceptors.isEmpty())) {
      return;
    }
    while (available > 0 && appliedCount < numReceptors) {

      if(!receptors.isEmpty() && !receptorIterator.hasNext()) {
        receptorIterator = receptors.listIterator();
      }
      ReceptorEntry r = receptorIterator.next();
      IPowerInterface pp = r.powerInterface;
      if(pp != null) {
        int canOffer = Math.min(r.emmiter.getMaxEnergyExtracted(r.direction), available);
        int used = pp.fillEnergy(r.direction.getOpposite(), canOffer);
        used = Math.max(0, used);
        available -= used;
        if(available <= 0) {
          break;
        }
      }
      appliedCount++;
    }

    int used = wasAvailable - available;
    // use all the capacator storage first
    energyStored -= used;

    distributeStorageToPipes();
  }

  private void distributeStorageToPipes() {
    if(maxEnergyStored <= 0 || energyStored <= 0) {
      for (TileEntityPipe pip : network.getPipes()) {
        if(!(pip instanceof TileEntityPipePowerRF))continue;
       	TileEntityPipePowerRF con = (TileEntityPipePowerRF)pip;
        con.setEnergyStored(0);
      }
      return;
    }
    energyStored = MathHelper.clamp_int(energyStored, 0, maxEnergyStored);

    float filledRatio = (float) energyStored / maxEnergyStored;
    int energyLeft = energyStored;
    
    for (TileEntityPipe pip : network.getPipes()) {
    	if(!(pip instanceof TileEntityPipePowerRF))continue;
    	TileEntityPipePowerRF con = (TileEntityPipePowerRF)pip;
      if(energyLeft > 0) {
        // NB: use ceil to ensure we dont through away any energy due to
        // rounding
        // errors
        int give = (int) Math.ceil(con.getMaxEnergyStored() * filledRatio);
        give = Math.min(give, con.getMaxEnergyStored());
        give = Math.min(give, energyLeft);
        con.setEnergyStored(give);    
        energyLeft -= give;
      } else {
        con.setEnergyStored(0);
      }
    }
  }

  boolean isActive() {
    return energyStored > 0;
  }

  private void updateNetorkStorage() {
    maxEnergyStored = 0;
    energyStored = 0;
    for (TileEntityPipe pip : network.getPipes()) {
      if(!(pip instanceof TileEntityPipePowerRF))continue;
	  TileEntityPipePowerRF con = (TileEntityPipePowerRF)pip;
      maxEnergyStored += con.getMaxEnergyStored();
      energyStored += con.getEnergyStored();
    }
    energyStored = MathHelper.clamp_int(energyStored, 0, maxEnergyStored);
  }

  public void receptorsChanged() {
    receptorsDirty = true;
  }

  private void checkReceptors() {
    if(!receptorsDirty) {
      return;
    }
    receptors.clear();
    storageReceptors.clear();
    for (ReceptorEntry rec : network.getPowerReceptors()) {
      /*if(rec.powerInterface.getDelegate() != null &&
          rec.powerInterface.getDelegate() instanceof alec_wam.CrystalMod.api.energy.ICEnergyConnection) {
        storageReceptors.add(rec);
      } else {*/
        receptors.add(rec);
      //}
    }
    receptorIterator = receptors.listIterator();

    receptorsDirty = false;
  }

  void onNetworkDestroyed() {
  }

  @SuppressWarnings("unused")
  private int minAbs(int amount, int limit) {
    if(amount < 0) {
      return Math.max(amount, -limit);
    } else {
      return Math.min(amount, limit);
    }
  }
  
}