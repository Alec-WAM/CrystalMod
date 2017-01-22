package alec_wam.CrystalMod.tiles.crate;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemHandlerCrate implements IItemHandler
{
    public final TileCrate crate;

    public ItemHandlerCrate(TileCrate crate)
    {
        this.crate = crate;
    }

    @Override
    public int getSlots()
    {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
    	ItemStack fixed = ItemStackTools.getEmptyStack();
    	if(ItemStackTools.isValid(crate.getStack())){
    		if(Config.crates_leaveOneItem){
    			if(ItemStackTools.getStackSize(crate.getStack()) == 1)return ItemStackTools.getEmptyStack();
    			fixed = ItemUtil.copy(crate.getStack(), ItemStackTools.getStackSize(crate.getStack())-1);
    			if(ItemStackTools.isEmpty(fixed)){
    				fixed = ItemStackTools.getEmptyStack();
    			}
    		} else {
    			fixed = crate.getStack();
    		}
    	}
    	return fixed;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        if (ItemStackTools.isEmpty(stack))
            return ItemStackTools.getEmptyStack();

        ItemStack existing = crate.getStack();

        int limit = getStackLimit(slot, stack);

        if (ItemStackTools.isValid(existing))
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
            if (ItemStackTools.isEmpty(existing))
            {
                crate.setStack(reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            }
            else
            {
            	ItemStackTools.incStackSize(existing, reachedLimit ? limit : ItemStackTools.getStackSize(stack));
            	crate.setStack(existing);
            }
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, ItemStackTools.getStackSize(stack) - limit) : ItemStackTools.getEmptyStack();
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (amount == 0)
            return ItemStackTools.getEmptyStack();

        ItemStack existing = crate.getStack();
        
        if (ItemStackTools.isEmpty(existing) || ItemStackTools.getStackSize(existing) == 1 && Config.crates_leaveOneItem)
            return ItemStackTools.getEmptyStack();

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        int offset = Config.crates_leaveOneItem ? 1 : 0;
        
        if (ItemStackTools.getStackSize(existing) <= toExtract-offset)
        {
            if (!simulate)
            {
                crate.setStack(ItemStackTools.getEmptyStack());
            }
            return existing;
        }
        else
        {
            if (!simulate)
            {
            	crate.setStack(ItemHandlerHelper.copyStackWithSize(existing, ItemStackTools.getStackSize(existing) - toExtract));
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    protected int getStackLimit(int slot, ItemStack stack)
    {
        return stack.getMaxStackSize()*crate.getCrateSize();
    }

	@Override
	public int getSlotLimit(int slot) {
		ItemStack existing = crate.getStack();
		if(ItemStackTools.isEmpty(existing))return 64;
        return getStackLimit(slot, existing);
	}
}