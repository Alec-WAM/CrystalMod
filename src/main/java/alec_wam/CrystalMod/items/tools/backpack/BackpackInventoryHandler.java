package alec_wam.CrystalMod.items.tools.backpack;

import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class BackpackInventoryHandler implements ICapabilityProvider {

	private final InventoryBackpack inv;
	private final ItemStack backpack;
	public BackpackInventoryHandler(ItemStack backpack, IBackpackInventory type){
		this.backpack = backpack;
		inv = type.getInventory(backpack);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return facing == null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) new InvWrapper(inv) : null;
	}

}
