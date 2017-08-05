package alec_wam.CrystalMod.api;

import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;

import com.google.common.collect.ArrayListMultimap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ItemStackList{
    private ArrayListMultimap<Item, ItemStack> stacks = ArrayListMultimap.create();

    public void add(ItemStack stack) {
    	if(ItemStackTools.isNullStack(stack)){
    		return;
    	}
    	
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (ItemUtil.canCombine(stack, otherStack)) {
            	ItemStackTools.incStackSize(otherStack, ItemStackTools.getStackSize(stack));
                return;
            }
        }

        stacks.put(stack.getItem(), ItemStackTools.safeCopy(stack));
    }

    public boolean remove(@Nonnull ItemStack stack, boolean removeIfReachedZero) {
    	if(ItemStackTools.isNullStack(stack))return false;
    	return remove(stack, ItemStackTools.getStackSize(stack), removeIfReachedZero);
    }
    
    public boolean remove(@Nonnull ItemStack stack, int size, boolean removeIfReachedZero) {
    	for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (ItemUtil.canCombine(stack, otherStack)) {
            	ItemStackTools.incStackSize(otherStack, -size);

                if ((ItemStackTools.isEmpty(otherStack) || !ItemStackTools.isValid(otherStack)) && removeIfReachedZero) {
                    stacks.remove(otherStack.getItem(), otherStack);
                }

                return true;
            }
        }

        return false;
    }

    @Nullable
    public ItemStack get(@Nonnull ItemStack stack, boolean ore) {
        // When the oreDict flag is set all stacks need to be checked not just the ones matching the Item
        for (ItemStack otherStack : ore ? stacks.values() : stacks.get(stack.getItem())) {
            if (ore ? ItemUtil.stackMatchUseOre(stack, otherStack) : ItemUtil.canCombine(stack, otherStack)) {
                return otherStack;
            }
        }

        return ItemStackTools.getEmptyStack();
    }

    public void clear() {
        stacks.clear();
    }

    public void clean() {
    	
    	Iterator<ItemStack> ii = stacks.values().iterator();
    	while(ii.hasNext()){
    		ItemStack stack = ii.next();
    		if(!ItemStackTools.isValid(stack) || ItemStackTools.isEmpty(stack)){
    			stacks.remove(stack.getItem(), stack);
    		}
    	}
    }

    public boolean isEmpty() {
        return stacks.isEmpty();
    }

    @Nonnull
    public Collection<ItemStack> getStacks() {
        return stacks.values();
    }

    @Nonnull
    public ItemStackList copy() {
        ItemStackList list = new ItemStackList();

        for (ItemStack stack : stacks.values()) {
            list.add(ItemStackTools.safeCopy(stack));
        }

        return list;
    }

    @Override
    public String toString() {
        return stacks.toString();
    }
}
