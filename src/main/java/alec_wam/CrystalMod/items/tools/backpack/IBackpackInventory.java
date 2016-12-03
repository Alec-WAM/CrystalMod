package alec_wam.CrystalMod.items.tools.backpack;

import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IBackpackInventory extends IBackpack {

	public InventoryBackpack getInventory(EntityPlayer player, ItemStack backpack);
	
}
