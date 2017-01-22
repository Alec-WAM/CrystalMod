package alec_wam.CrystalMod.util.inventory;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class WrapperInventory implements IItemHandlerModifiable {

	private final NonNullList<ItemStack> items;
	private IItemHandler master;
	
	public WrapperInventory(IItemHandler inv){
		master = inv;
		items = NonNullList.withSize(master.getSlots(), ItemStackTools.getEmptyStack());
		for(int s = 0; s < master.getSlots(); s++){
			items.set(s, master.getStackInSlot(s));
		}
	}

	@Override
	public int getSlots() {
		return master.getSlots();
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (!ItemStackTools.isValid(stack))
            return ItemStackTools.getEmptyStack();

        validateSlotIndex(slot);

        ItemStack existing = this.items.get(slot);

        int limit = getStackLimit(slot, stack);

        if (!ItemStackTools.isNullStack(existing))
        {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= ItemStackTools.getStackSize(existing);
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = ItemStackTools.getStackSize(stack) > limit;

        if (!simulate)
        {
            if (ItemStackTools.isNullStack(existing))
            {
                this.items.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            }
            else
            {
            	ItemStackTools.incStackSize(existing, reachedLimit ? limit : ItemStackTools.getStackSize(stack));
            }
            onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, ItemStackTools.getStackSize(stack) - limit) : null;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0)
            return ItemStackTools.getEmptyStack();

        validateSlotIndex(slot);

        ItemStack existing = this.items.get(slot);

        if (ItemStackTools.isNullStack(existing))
            return ItemStackTools.getEmptyStack();

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (ItemStackTools.getStackSize(existing) <= toExtract)
        {
            if (!simulate)
            {
                this.items.set(slot, ItemStackTools.getEmptyStack());
                onContentsChanged(slot);
            }
            return existing;
        }
        else
        {
            if (!simulate)
            {
                this.items.set(slot, ItemHandlerHelper.copyStackWithSize(existing, ItemStackTools.getStackSize(existing) - toExtract));
                onContentsChanged(slot);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
	}
	
	protected int getStackLimit(int slot, ItemStack stack)
    {
        return stack.getMaxStackSize();
    }

	protected void validateSlotIndex(int slot)
	{
	        if (slot < 0 || slot >= items.size())
	            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + items.size() + ")");
    }

    protected void onContentsChanged(int slot)
    {

    }
	
	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		items.set(slot, stack);
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return items.get(slot);
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

}
