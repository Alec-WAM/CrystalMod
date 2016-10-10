package com.alec_wam.CrystalMod.entities.minions.ai;

import java.util.ArrayList;
import java.util.List;

import com.alec_wam.CrystalMod.entities.minions.EntityMinionBase;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Manages the execution of AI objects attached to an minion.
 */
@SuppressWarnings("rawtypes") 
public class AIManager 
{
	private EntityMinionBase minion;
	private List<MinionAIBase> AIList;

	public AIManager(EntityMinionBase minion)
	{
		this.minion = minion;
		this.AIList = new ArrayList<MinionAIBase>();
	}

	public void addAI(MinionAIBase AI)
	{
		AIList.add(AI);
	}

	@SuppressWarnings("unchecked")
	public void onUpdate()
	{
		for (final MinionAIBase AI : AIList)
		{
			boolean doRun = AI instanceof MinionToggleAIBase ? ((MinionToggleAIBase)AI).getIsActive() : true;

			if (doRun)
			{
				AI.onUpdateCommon(minion);

				if (minion.worldObj.isRemote)
				{
					AI.onUpdateClient(minion);
				}

				else
				{
					AI.onUpdateServer(minion);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void writeToNBT(NBTTagCompound nbt)
	{
		for (final MinionAIBase AI : AIList)
		{
			AI.writeToNBT(minion, nbt);
		}
	}

	@SuppressWarnings("unchecked")
	public void readFromNBT(NBTTagCompound nbt)
	{
		for (final MinionAIBase AI : AIList)
		{
			AI.readFromNBT(minion, nbt);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends MinionAIBase> T getAI(Class<T> clazz)
	{
		for (final MinionAIBase AI : AIList)
		{
			if (AI.getClass() == clazz)
			{
				return (T) AI;
			}
		}

		return null;
	}
	
	public boolean isToggleAIActive()
	{
		for (final MinionAIBase AI : AIList)
		{
			if (AI instanceof MinionToggleAIBase)
			{
				MinionToggleAIBase TAI = (MinionToggleAIBase) AI;
				
				if (TAI.getIsActive())
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public String getNameOfActiveAI()
	{
		for (final MinionAIBase AI : AIList)
		{
			if (AI instanceof MinionToggleAIBase)
			{
				MinionToggleAIBase TAI = (MinionToggleAIBase) AI;
				
				if (TAI.getIsActive())
				{
					return TAI.getName();
				}
			}
		}
		
		return "";
	}
	
	public void disableAllToggleAIs()
	{
		for (final MinionAIBase AI : AIList)
		{
			if (AI instanceof MinionToggleAIBase)
			{
				MinionToggleAIBase TAI = (MinionToggleAIBase) AI;
				TAI.setIsActive(false);
			}
		}
	}
}
