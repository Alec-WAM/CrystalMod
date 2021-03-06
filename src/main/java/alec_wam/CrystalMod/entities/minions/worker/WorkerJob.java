package alec_wam.CrystalMod.entities.minions.worker;

import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;

public abstract class WorkerJob {

	public abstract boolean run(EntityMinionWorker worker, TileWorksiteBase worksite);
	
	public void onCompleted(EntityMinionWorker worker, TileWorksiteBase worksite){
		
	}
	
	public abstract boolean isSame(WorkerJob job);
	
}
