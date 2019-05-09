package alec_wam.CrystalMod.network;

import net.minecraft.nbt.NBTTagCompound;

public interface IMessageHandler {

	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client);
	
}
