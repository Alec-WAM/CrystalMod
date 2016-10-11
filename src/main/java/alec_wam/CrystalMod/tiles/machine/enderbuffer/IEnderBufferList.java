package alec_wam.CrystalMod.tiles.machine.enderbuffer;

import alec_wam.CrystalMod.tiles.machine.enderbuffer.EnderBufferManager.EnderBuffer;

public interface IEnderBufferList {

	public void setDirty();
	
	public EnderBuffer getBuffer(int id);
}
