package alec_wam.CrystalMod.tiles.crate;

import alec_wam.CrystalMod.ModConfig;
import alec_wam.CrystalMod.tiles.EnumCrystalColorSpecialWithCreative;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemHandlerCrate implements IItemHandler
{
	public static final boolean LEAVE_ONE_ITEM = ModConfig.BLOCKS.Crate_LeaveItem.get();
    public final TileEntityCrate crate;
    
    public ItemHandlerCrate(TileEntityCrate crate)
    {
        this.crate = crate;
    }

    public boolean isCreative() {
    	return crate.tier == EnumCrystalColorSpecialWithCreative.CREATIVE.ordinal();
    }
    
    public boolean isVoid() {
    	return crate.hasVoidUpgrade;
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
    		if(isCreative()){
    			return ItemUtil.copy(crate.getStack(), 64);
        	}            
    		if(LEAVE_ONE_ITEM){
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
    	
    	if(isCreative()){
    		return stack;
    	}

        ItemStack existing = crate.getStack();

        int limit = getStackLimit(slot, stack);

        if (ItemStackTools.isValid(existing))
        {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= ItemStackTools.getStackSize(existing);
        }

        if (limit <= 0){
        	if(isVoid()){
        		//Void Items
        		return ItemStackTools.getEmptyStack();
        	}
            return stack;
        }

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

    @Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
    	if (amount == 0)
            return ItemStackTools.getEmptyStack();

        ItemStack existing = crate.getStack();
        
        if(isCreative()){
        	if(ItemStackTools.isValid(existing)){
        		return ItemUtil.copy(existing, amount);
        	}
        	return ItemStackTools.getEmptyStack();
    	}
        
        if (ItemStackTools.isEmpty(existing) || ItemStackTools.getStackSize(existing) == 1 && LEAVE_ONE_ITEM)
            return ItemStackTools.getEmptyStack();

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        int offset = LEAVE_ONE_ITEM ? 1 : 0;
        
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

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return slot == 0 && (ItemStackTools.isEmpty(getStackInSlot(0)) || (!isCreative() && ItemUtil.canCombine(stack, getStackInSlot(0))));
	}
}