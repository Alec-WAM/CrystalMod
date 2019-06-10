package alec_wam.CrystalMod.tiles.pipes.energy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import alec_wam.CrystalMod.tiles.pipes.energy.EnergyNode.EnergyNodeEntry;
import net.minecraft.util.math.MathHelper;

public class PipePowerDistributor {

	private final PipeNetworkEnergy network;

	int maxEnergyStored;
	int energyStored;

	private final List<EnergyNodeEntry> energyNodes = new ArrayList<EnergyNodeEntry>();
	private ListIterator<EnergyNodeEntry> nodeIterator = energyNodes.listIterator();

	private boolean nodesDirty = true;

	public PipePowerDistributor(PipeNetworkEnergy network) {
		this.network = network;
		maxEnergyStored = 64;
	}

	public int getPowerInPipes() {
		return energyStored;
	}

	public int getMaxPowerInPipes() {
		return maxEnergyStored;
	}

	public long getPowerInNodes() {
		long result = 0;
		Set<Object> done = new HashSet<Object>();
		for (EnergyNodeEntry re : energyNodes) {
			if(!re.pipe.isRebuildingConnections()) {
				IEnergyNode powerNode = re.energyNode;
				if(!done.contains(powerNode.getHost())) {
					done.add(powerNode.getHost());
					result += powerNode.getEnergy();
				}
			}
		}
		return result;
	}

	public long getMaxPowerInNodes() {
		long result = 0;
		Set<Object> done = new HashSet<Object>();
		for (EnergyNodeEntry re : energyNodes) {
			if(!re.pipe.isRebuildingConnections()) {
				IEnergyNode powerNode = re.energyNode;
				if(!done.contains(powerNode.getHost())) {
					done.add(powerNode.getHost());
					result += powerNode.getMaxEnergy();
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
				e.printStackTrace();
				errorSupressionA = 200;
				errorSupressionB = 20;
			} else if (errorSupressionB-- <= 0) {
				errorSupressionB = 20;
			}
		}
	}

	public void doApplyRecievedPower() {

		checkNodes();

		// Update our energy stored based on what's in our pipes
		updateNetworkStorage();

		int appliedCount = 0;
		int numNodes = energyNodes.size();
		int available = energyStored;
		int wasAvailable = available;

		if(available <= 0 || (energyNodes.isEmpty())) {
			return;
		}
		
		while (available > 0 && appliedCount < numNodes) {

			if(!energyNodes.isEmpty() && !nodeIterator.hasNext()) {
				nodeIterator = energyNodes.listIterator();
			}
			EnergyNodeEntry r = nodeIterator.next();
			IEnergyNode node = r.energyNode;
			if(node != null) {
				int canOffer = Math.min(r.pipe.getMaxEnergyExtracted(r.direction), available);
				int used = node.fillEnergy(canOffer, false);
				used = Math.max(0, used);
				available -= used;
				if(available <= 0) {
					break;
				}
			}
			appliedCount++;
		}

		int used = wasAvailable - available;
		energyStored -= used;

		distributeStorageToPipes();
	}

	public int freeSend(final int power, boolean sim){
		if(sim){
			int appliedCount = 0;
			int numNodes = energyNodes.size();
			int available = power;
			int wasAvailable = available;

			if(available <= 0 || (energyNodes.isEmpty())) {
				return 0;
			}
			while (available > 0 && appliedCount < numNodes) {

				if(!energyNodes.isEmpty() && !nodeIterator.hasNext()) {
					nodeIterator = energyNodes.listIterator();
				}
				
				EnergyNodeEntry r = nodeIterator.next();
				IEnergyNode node = r.energyNode;
				if(node != null) {
					int canOffer = Math.min(r.pipe.getMaxEnergyExtracted(r.direction), available);
					int used = node.fillEnergy(canOffer, true);
					used = Math.max(0, used);
					available -= used;
					if(available <= 0) {
						break;
					}
				}
				appliedCount++;
			}

			int used = wasAvailable - available;
			return used;
		} else {
			checkNodes();

			int appliedCount = 0;
			int numNodes = energyNodes.size();
			int available = power;
			int wasAvailable = available;

			if(available <= 0 || (energyNodes.isEmpty())) {
				return 0;
			}
			
			while (available > 0 && appliedCount < numNodes) {

				if(!energyNodes.isEmpty() && !nodeIterator.hasNext()) {
					nodeIterator = energyNodes.listIterator();
				}
				EnergyNodeEntry r = nodeIterator.next();
				IEnergyNode node = r.energyNode;
				if(node != null) {
					int canOffer = Math.min(r.pipe.getMaxEnergyExtracted(r.direction), available);
					int used = node.fillEnergy(canOffer, false);
					used = Math.max(0, used);
					available -= used;
					if(available <= 0) {
						break;
					}
				}
				appliedCount++;
			}

			int used = wasAvailable - available;
			return used;
		}
	}

	private void distributeStorageToPipes() {
		if(maxEnergyStored <= 0 || energyStored <= 0) {
			for (TileEntityPipeEnergy pipe : network.getPipes()) {
				pipe.setEnergyStored(0);
			}
			return;
		}
		energyStored = MathHelper.clamp(energyStored, 0, maxEnergyStored);

		float filledRatio = (float) energyStored / maxEnergyStored;
		int energyLeft = energyStored;

		for (TileEntityPipeEnergy pipe : network.getPipes()) {
			if(energyLeft > 0) {
				int give = (int) Math.ceil(pipe.getMaxEnergyStored() * filledRatio);
				give = Math.min(give, pipe.getMaxEnergyStored());
				give = Math.min(give, energyLeft);
				pipe.setEnergyStored(give);    
				energyLeft -= give;
			} else {
				pipe.setEnergyStored(0);
			}
		}
	}

	boolean isActive() {
		return energyStored > 0;
	}

	private void updateNetworkStorage() {
		maxEnergyStored = 0;
		energyStored = 0;
		for (TileEntityPipeEnergy pipe : network.getPipes()) {
			maxEnergyStored += pipe.getMaxEnergyStored();
			energyStored += pipe.getEnergyStored();
		}
		energyStored = MathHelper.clamp(energyStored, 0, maxEnergyStored);
	}

	public void nodesChanged() {
		nodesDirty = true;
	}

	private void checkNodes() {
		if(!nodesDirty) {
			return;
		}
		energyNodes.clear();
		for (EnergyNodeEntry rec : network.getEnergyNodes()) {
			energyNodes.add(rec);
		}
		nodeIterator = energyNodes.listIterator();

		nodesDirty = false;
	}

}