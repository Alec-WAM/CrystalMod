package alec_wam.CrystalMod.tiles.chest.wireless;

import java.util.UUID;

import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestManager.WirelessInventory;

public interface IWirelessChestSource {

	public int getCode();
	
	public WirelessInventory getInventory();
	
	public boolean isPrivate();
	
	public UUID getOwner();
	
	public boolean isOwner(UUID uuid);
	
}
