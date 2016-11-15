package alec_wam.CrystalMod.tiles.chest.wireless;

import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestManager.WirelessInventory;

public interface IWirelessChestList {
	public void setDirty();
	
	public WirelessInventory getInventory(int code);
}
