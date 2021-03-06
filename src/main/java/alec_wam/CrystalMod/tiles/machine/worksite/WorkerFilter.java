package alec_wam.CrystalMod.tiles.machine.worksite;

import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.util.ItemStackTools;

public abstract class WorkerFilter{
	public abstract boolean matches(EntityMinionWorker worker);
	
	public static WorkerFilter anyFilter = new WorkerFilter() {
		@Override
		public boolean matches(EntityMinionWorker worker){
			return true;
		}
	};
	
	public static WorkerFilter idleFilter = new WorkerFilter() {
		@Override
		public boolean matches(EntityMinionWorker worker){
			return worker.getHeldItemMainhand().isEmpty();
		}
	};
	
	public static class ToolFilter extends WorkerFilter {
		@Override
		public boolean matches(EntityMinionWorker worker){
			return true;
		}
	}
}
