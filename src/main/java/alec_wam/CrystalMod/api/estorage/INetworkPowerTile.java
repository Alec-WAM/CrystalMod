package alec_wam.CrystalMod.api.estorage;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.item.ItemStack;

public interface INetworkPowerTile extends INetworkTile {

	public int getEnergyUsage();

	public default ItemStack getDisplayStack() {
		return ItemStackTools.getEmptyStack();
	}
	
}
