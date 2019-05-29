package alec_wam.CrystalMod.tiles.pipes.energy;

import alec_wam.CrystalMod.tiles.pipes.NetworkPos;
import net.minecraft.util.EnumFacing;

public class EnergyNode {

	public static class EnergyNodeEntry {

		public TileEntityPipeEnergy pipe;
		public NetworkPos pos;
		public EnumFacing direction;

		public IEnergyNode energyNode;

		public EnergyNodeEntry(IEnergyNode energyNode, NetworkPos pos, TileEntityPipeEnergy pipe, EnumFacing direction) {
			this.energyNode = energyNode;
			this.pos = pos;
			this.pipe = pipe;
			this.direction = direction;
		}

	}

	public static class EnergyNodeKey {
		NetworkPos pos;
		EnumFacing direction;

		EnergyNodeKey(NetworkPos pos, EnumFacing direction) {
			this.pos = pos;
			this.direction = direction;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((pos == null) ? 0 : pos.hashCode());
			result = prime * result + ((direction == null) ? 0 : direction.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if(this == obj) {
				return true;
			}
			if(obj == null) {
				return false;
			}
			if(getClass() != obj.getClass()) {
				return false;
			}
			EnergyNodeKey other = (EnergyNodeKey) obj;
			if(pos == null) {
				if(other.pos != null) {
					return false;
				}
			} else if(!pos.equals(other.pos)) {
				return false;
			}
			if(direction != other.direction) {
				return false;
			}
			return true;
		}

	}
	
}
