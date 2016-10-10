package com.alec_wam.CrystalMod.entities.minions;

import com.alec_wam.CrystalMod.entities.minions.warrior.EntityMinionWarrior;
import com.alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;

public enum MinionType {

	BASIC(EntityMinionBase.class), WORKER(EntityMinionWorker.class), WARRIOR(EntityMinionWarrior.class); 
	
	private final Class<? extends EntityMinionBase> clazz;
	
	MinionType(Class<? extends EntityMinionBase> clazz){
		this.clazz = clazz;
	}
	
	public Class<? extends EntityMinionBase> getEntityClass(){
		return clazz;
	}
	
}
