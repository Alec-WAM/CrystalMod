package alec_wam.CrystalMod.tiles.chests.wireless;

import java.util.UUID;

import alec_wam.CrystalMod.tiles.chests.wireless.WirelessChestManager.WirelessInventory;

public interface IWirelessChestSource {

	public int getCode();
	
	public WirelessInventory getInventory();
	
	public boolean isPrivate();
	
	public UUID getOwner();
	
	public boolean isOwner(UUID uuid);
	
}
