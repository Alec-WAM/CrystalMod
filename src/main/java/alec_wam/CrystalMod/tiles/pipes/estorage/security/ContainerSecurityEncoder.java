package alec_wam.CrystalMod.tiles.pipes.estorage.security;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSecurityEncoder extends Container {

	public TileSecurityEncoder encoder;

	public ContainerSecurityEncoder(EntityPlayer player, TileSecurityEncoder encoder){
		this.encoder = encoder;
		
		addSlotToContainer(new Slot(encoder, 0, 152, 18));
		addPlayerInventory(player, 8, 90);
	}
	
	protected void addPlayerInventory(EntityPlayer player, int xInventory, int yInventory) {
        int id = 0;

        for (int i = 0; i < 9; i++) {
            Slot slot = new Slot(player.inventory, id, xInventory + i * 18, yInventory + 4 + (3 * 18));

            //playerInventorySlots.add(slot);

            addSlotToContainer(slot);

            id++;
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot slot = new Slot(player.inventory, id, xInventory + x * 18, yInventory + y * 18);

                //playerInventorySlots.add(slot);

                addSlotToContainer(slot);

                id++;
            }
        }
    }
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot){
		ItemStack stack = ItemStackTools.getEmptyStack();

        Slot s = getSlot(slot);

        if (s != null && s.getHasStack()) {
            stack = s.getStack().copy();

            if (slot < 1) {
                if (!mergeItemStack(stack, 1, inventorySlots.size(), false)) {
                    return ItemStackTools.getEmptyStack();
                }
            } else if (!mergeItemStack(stack, 0, 1, false)) {
                return ItemStackTools.getEmptyStack();
            }

            if (ItemStackTools.isEmpty(stack)) {
                s.putStack(ItemStackTools.getEmptyStack());
            } else {
                s.onSlotChanged();
            }
        }
        return stack;
	}
}
