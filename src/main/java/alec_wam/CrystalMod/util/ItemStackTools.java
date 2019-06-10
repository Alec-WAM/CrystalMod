package alec_wam.CrystalMod.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ItemStackTools {

    /**
     * Add (or decrease) a number of items from a stack.
     * If the number of items drops below 0 then null will be returned on 1.10 and the
     * 'null' itemstack on 1.11. Otherwise the same modified stack is returned.
     */
    @Nullable
    public static ItemStack incStackSize(@Nonnull ItemStack stack, int amount) {
    	stack.grow(amount);
        if (stack.getCount() <= 0) {
            return getEmptyStack();
        }
        return stack;
    }

    /**
     * Make a safe copy of an itemstack
     */
    @Nullable
    public static ItemStack safeCopy(@Nullable ItemStack stack) {
        if (stack.isEmpty()) {
            return getEmptyStack();
        }
        stack = stack.copy();
        // Safety
        if (stack.getCount() == 0) {
            stack.setCount(1);
        }
        return stack;
    }


    /**
     * Get the stacksize from a stack
     */
    public static int getStackSize(@Nullable ItemStack stack) {
        if (isEmpty(stack)) {
            return 0;
        }
        return stack.getCount();
    }

    /**
     * Set the stacksize on a stack. Returns the same stack or null if the new
     * amount was 0. On 1.11 it will return the 'null' itemstack
     */
    @Nullable
    public static ItemStack setStackSize(@Nonnull ItemStack stack, int amount) {
        if (amount <= 0) {
            return getEmptyStack();
        }
        stack.setCount(amount);
        return stack;
    }
    /**
     * Check if this is a valid stack. Tests for null on 1.10.
     */
    public static boolean isValid(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return stack.getCount() > 0;
    }
    
    
    public static boolean isNullStack(@Nonnull ItemStack stack){
    	if(stack == null){
    		//This is not supposed to happen
    		return true;
    	}
        return stack.isEmpty();
    }
    

    /**
     * Check if this is an empty stack. Tests for null on 1.10.
     */
    public static boolean isEmpty(ItemStack stack) {
        return isNullStack(stack);
    }

    public static void makeEmpty(@Nonnull ItemStack stack) {
        stack.setCount(0);
    }

    /**
     * Load an ItemStack from NBT.
     */
    @Nullable
    public static ItemStack loadFromNBT(@Nonnull CompoundNBT nbt) {
    	if(nbt.isEmpty()) return getEmptyStack();
        return ItemStack.read(nbt);
    }

    @Nullable
    public static ItemStack getEmptyStack() {
        return ItemStack.EMPTY;
    }

}
