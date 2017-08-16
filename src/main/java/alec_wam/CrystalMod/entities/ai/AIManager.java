package alec_wam.CrystalMod.entities.ai;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Manages the execution of AI objects attached to an minion.
 */
@SuppressWarnings("rawtypes") 
public class AIManager 
{
	private EntityLivingBase entity;
	private List<AIBase> AIList;

	public AIManager(EntityLivingBase entity)
	{
		this.entity = entity;
		this.AIList = new ArrayList<AIBase>();
	}

	public void addAI(AIBase AI)
	{
		AIList.add(AI);
	}

	@SuppressWarnings("unchecked")
	public void onUpdate()
	{
		for (final AIBase AI : AIList)
		{
			boolean doRun = AI instanceof ToggleAIBase ? ((ToggleAIBase)AI).getIsActive() : true;

			if (doRun)
			{
				AI.onUpdateCommon(entity);

				if (entity.getEntityWorld().isRemote)
				{
					AI.onUpdateClient(entity);
				}

				else
				{
					AI.onUpdateServer(entity);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void writeToNBT(NBTTagCompound nbt)
	{
		for (final AIBase AI : AIList)
		{
			AI.writeToNBT(entity, nbt);
		}
	}

	@SuppressWarnings("unchecked")
	public void readFromNBT(NBTTagCompound nbt)
	{
		for (final AIBase AI : AIList)
		{
			AI.readFromNBT(entity, nbt);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends AIBase> T getAI(Class<T> clazz)
	{
		for (final AIBase AI : AIList)
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
		for (final AIBase AI : AIList)
		{
			if (AI instanceof ToggleAIBase)
			{
				ToggleAIBase TAI = (ToggleAIBase) AI;
				
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
		for (final AIBase AI : AIList)
		{
			if (AI instanceof ToggleAIBase)
			{
				ToggleAIBase TAI = (ToggleAIBase) AI;
				
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
		for (final AIBase AI : AIList)
		{
			if (AI instanceof ToggleAIBase)
			{
				ToggleAIBase TAI = (ToggleAIBase) AI;
				TAI.setIsActive(false);
			}
		}
	}
}
