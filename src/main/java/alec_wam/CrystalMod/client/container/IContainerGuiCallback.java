package alec_wam.CrystalMod.client.container;

import net.minecraft.nbt.NBTTagCompound;

public interface IContainerGuiCallback
{

	public void refreshGui();
	public void handlePacketData(NBTTagCompound data);

}
