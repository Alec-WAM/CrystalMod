package alec_wam.CrystalMod.items.tools.backpack;

import alec_wam.CrystalMod.items.tools.backpack.gui.OpenType;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class BackpackUtil {

	public static ItemStack getItemStack(InventoryPlayer player, OpenType type){
		return type == OpenType.MAIN_HAND ? player.getCurrentItem() : player.offHandInventory[0];
	}
	
}
