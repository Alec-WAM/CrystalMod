package alec_wam.CrystalMod.items.tools.backpack;

import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IBackpackInventory extends IBackpack {

	public InventoryBackpack getInventory(EntityPlayer player, ItemStack backpack);
	public InventoryBackpack getInventory(ItemStack backpack);
	
	@Override
	public default ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return ItemStackTools.isValid(stack) ? new BackpackInventoryHandler(stack, this) : null;
	}
}
