package alec_wam.CrystalMod.tiles.machine.worksite.gui;

import alec_wam.CrystalMod.client.container.ContainerMessageBase;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided;
import alec_wam.CrystalMod.tiles.machine.worksite.ItemSlotFilter;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBoundedInventory;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerWorksite extends ContainerMessageBase {

	public final TileWorksiteBoundedInventory worksite;
	public final InventorySided inventory;
	public int guiHeight, topLabel, frontLabel, bottomLabel, rearLabel,
			leftLabel, rightLabel, playerLabel;

	public ContainerWorksite(EntityPlayer player, TileWorksiteBoundedInventory worksite) {
		super(player);
		this.worksite = worksite;
		inventory = worksite.inventory;
	}

	protected int addSlots(int xPosStart, int yPosStart, int firstSlotIndex, int numberOfSlots) {
		ItemSlotFilter filter;
		SlotFiltered slot;
		int x1, y1, xPos, yPos;
		int maxY = 0;
		for (int i = 0, slotNum = firstSlotIndex; i < numberOfSlots; i++, slotNum++) {
			filter = inventory.getFilterForSlot(slotNum);
			x1 = i % 9;
			y1 = i / 9;
			xPos = xPosStart + x1 * 18;
			yPos = yPosStart + y1 * 18;
			if (yPos + 18 > maxY) {
				maxY = yPos + 18;
			}
			slot = new SlotFiltered(inventory, slotNum, xPos, yPos, filter);
			addSlotToContainer(slot);
		}
		return maxY;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex) {
		int slots = worksite.inventory.getSizeInventory();
		Slot slot = (Slot) this.inventorySlots.get(slotClickedIndex);
		if (slot == null || !slot.getHasStack()) {
			return ItemStackTools.getEmptyStack();
		}
		ItemStack stackFromSlot = slot.getStack();
		if (slotClickedIndex < slots) {
			this.mergeItemStack(stackFromSlot, slots, slots + 36, false);
		} else {
			this.mergeItemStack(stackFromSlot, 0, slots, false);
		}
		if (ItemStackTools.isEmpty(stackFromSlot)) {
			slot.putStack(ItemStackTools.getEmptyStack());
		} else {
			slot.onSlotChanged();
		}
		return ItemStackTools.getEmptyStack();
	}

}
