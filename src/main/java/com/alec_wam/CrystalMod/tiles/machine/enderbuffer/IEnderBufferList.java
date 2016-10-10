package com.alec_wam.CrystalMod.tiles.machine.enderbuffer;

import com.alec_wam.CrystalMod.tiles.machine.enderbuffer.EnderBufferManager.EnderBuffer;

public interface IEnderBufferList {

	public void setDirty();
	
	public EnderBuffer getBuffer(int id);
}
