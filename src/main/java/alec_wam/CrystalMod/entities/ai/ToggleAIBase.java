package alec_wam.CrystalMod.entities.ai;

import alec_wam.CrystalMod.entities.minions.EntityMinionBase;

public abstract class ToggleAIBase<T extends EntityMinionBase> extends AIBase<T> {

	/** Sets this AI as active and begins calling the update methods. */
	public abstract void setIsActive(boolean value);
	
	/** @returns True if this AI is currently running. */
	public abstract boolean getIsActive();
	
	/** @returns The user-friendly name of this AI. Displays above the actor's head. */
	protected abstract String getName();
	
}
