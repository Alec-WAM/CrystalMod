package alec_wam.CrystalMod.items.tools.backpack;

import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.items.tools.backpack.gui.OpenType;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class BackpackUtil {

	public static ItemStack getItemStack(InventoryPlayer player, OpenType type){
		if(type == OpenType.BACK){
			ExtendedPlayer ePlayer = ExtendedPlayerProvider.getExtendedPlayer(player.player);
			return ePlayer.getInventory().getStackInSlot(0);
		}		
		return type == OpenType.MAIN_HAND ? player.getCurrentItem() : player.offHandInventory[0];
	}
	
}
