package alec_wam.CrystalMod.tiles.chests.wireless;

import alec_wam.CrystalMod.tiles.chests.wireless.WirelessChestManager.WirelessInventory;

public interface IWirelessChestList {
	public void setDirty();
	
	public WirelessInventory getInventory(int code);
}
