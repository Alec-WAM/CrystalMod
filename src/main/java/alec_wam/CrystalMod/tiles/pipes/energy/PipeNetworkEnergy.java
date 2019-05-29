package alec_wam.CrystalMod.tiles.pipes.energy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import alec_wam.CrystalMod.tiles.pipes.NetworkPos;
import alec_wam.CrystalMod.tiles.pipes.PipeNetworkBase;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipeBase;
import alec_wam.CrystalMod.tiles.pipes.energy.EnergyNode.EnergyNodeEntry;
import alec_wam.CrystalMod.tiles.pipes.energy.EnergyNode.EnergyNodeKey;
import net.minecraft.util.EnumFacing;

public class PipeNetworkEnergy extends PipeNetworkBase<TileEntityPipeEnergy> {

	private final Map<EnergyNodeKey, EnergyNodeEntry> energyNodes = new HashMap<EnergyNodeKey, EnergyNodeEntry>();

	PipePowerDistributor powerDistributor;
	public PipeNetworkEnergy(){
		super();
		powerDistributor = new PipePowerDistributor(this);
		powerDistributor.nodesChanged();
	}

	public PipePowerDistributor getPowerDistributor() {
		return powerDistributor;
	}

	@Override
	public boolean addPipe(TileEntityPipeBase p) {
		if(!(p instanceof TileEntityPipeEnergy)) return false;
		if(super.addPipe(p)){
			TileEntityPipeEnergy pipe = (TileEntityPipeEnergy)p;
	
			Set<EnumFacing> externalDirs = pipe.getExternalConnections();
			for (EnumFacing dir : externalDirs) {
				IEnergyNode pr = pipe.getExternalEnergyNode(dir);
				if(pr != null) {
					powerNodeAdded(pipe, dir, pr);
				}
			}
			return true;
		}
		return false;
	}

	public void powerNodeAdded(TileEntityPipeEnergy energyPipe, EnumFacing direction, IEnergyNode energyNode) {
		if(energyNode == null) {
			return;
		}
		NetworkPos pos = energyPipe.getNetworkPos().offset(direction);
		EnergyNodeKey key = new EnergyNodeKey(pos, direction);
		EnergyNodeEntry re = energyNodes.get(key);
		if(re == null) {
			re = new EnergyNodeEntry(energyNode, pos, energyPipe, direction);
			energyNodes.put(key, re);
		}
		if(powerDistributor != null) {
			powerDistributor.nodesChanged();
		}
	}

	public void powerNodeRemoved(NetworkPos pos) {
		List<EnergyNodeKey> remove = new ArrayList<EnergyNodeKey>();
		for (EnergyNodeKey key : energyNodes.keySet()) {
			if(key != null && key.pos.equals(pos)) {
				remove.add(key);
			}
		}
		for (EnergyNodeKey key : remove) {
			energyNodes.remove(key);
		}
		powerDistributor.nodesChanged();
	}

	public Collection<EnergyNodeEntry> getEnergyNodes() {
		return energyNodes.values();
	}

	@Override
	public void tick() {
		super.tick();
		powerDistributor.applyRecievedPower();
	}

}
