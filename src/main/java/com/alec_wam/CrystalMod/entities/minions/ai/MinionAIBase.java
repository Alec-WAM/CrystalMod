package com.alec_wam.CrystalMod.entities.minions.ai;

import com.alec_wam.CrystalMod.entities.minions.EntityMinionBase;

import net.minecraft.nbt.NBTTagCompound;

public abstract class MinionAIBase<T extends EntityMinionBase>
{
	/** Update code that runs on both the client and the server. */
	public abstract void onUpdateCommon(T minion);
	
	/** Update code that will only run on the client. */
	public abstract void onUpdateClient(T minion);
	
	/** Update code that will only run on the server. */
	public abstract void onUpdateServer(T minion);
	
	/** */
	public abstract void reset(T minion);
	
	public abstract void writeToNBT(T minion, NBTTagCompound nbt);
	
	public abstract void readFromNBT(T minion, NBTTagCompound nbt);
}
