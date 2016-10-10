package com.alec_wam.CrystalMod.tiles.machine.worksite.gui;

import net.minecraft.nbt.NBTTagCompound;

public interface IContainerGuiCallback
{

	public void refreshGui();
	public void handlePacketData(NBTTagCompound data);

}
