package alec_wam.CrystalMod.util.inventory;

import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class WrapperBaubleInventory extends WrapperInventory implements IBaublesItemHandler {

	private final IBaublesItemHandler masterBaubles;
	public WrapperBaubleInventory(IBaublesItemHandler inv) {
		super(inv);
		this.masterBaubles = inv;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack, EntityLivingBase player) {
		return masterBaubles.isItemValidForSlot(slot, stack, player);
	}

	@Override
	public boolean isEventBlocked() {
		return masterBaubles.isEventBlocked();
	}

	@Override
	public void setEventBlock(boolean blockEvents) {
		masterBaubles.setEventBlock(blockEvents);
	}

	@Override
	public boolean isChanged(int slot) {
		return masterBaubles.isChanged(slot);
	}

	@Override
	public void setChanged(int slot, boolean changed) {
		masterBaubles.setChanged(slot, changed);
	}

}
