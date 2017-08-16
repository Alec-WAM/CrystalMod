package alec_wam.CrystalMod.entities.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

public abstract class AIBase<T extends EntityLivingBase>
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
