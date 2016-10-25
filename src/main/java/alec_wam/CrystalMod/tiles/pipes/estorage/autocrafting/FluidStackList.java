package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting;

import alec_wam.CrystalMod.util.FluidUtil;
import com.google.common.collect.ArrayListMultimap;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Collection;

public class FluidStackList{
    private ArrayListMultimap<Fluid, FluidStack> stacks = ArrayListMultimap.create();

    public void add(FluidStack stack) {
        for (FluidStack otherStack : stacks.get(stack.getFluid())) {
            if (FluidUtil.canCombine(otherStack, stack)) {
                otherStack.amount += stack.amount;

                return;
            }
        }

        stacks.put(stack.getFluid(), stack.copy());
    }

    public boolean remove(@Nonnull FluidStack stack, boolean removeIfReachedZero) {
    	return remove(stack, stack.amount, removeIfReachedZero);
    }
    
    public boolean remove(@Nonnull FluidStack stack, int size, boolean removeIfReachedZero) {
        for (FluidStack otherStack : stacks.get(stack.getFluid())) {
            if (FluidUtil.canCombine(otherStack, stack)) {
                otherStack.amount -= size;

                if (otherStack.amount <= 0 && removeIfReachedZero) {
                    stacks.remove(otherStack.getFluid(), otherStack);
                }

                return true;
            }
        }

        return false;
    }

    @Nullable
    public FluidStack get(@Nonnull FluidStack stack) {
        for (FluidStack otherStack : stacks.get(stack.getFluid())) {
            if (FluidUtil.canCombine(stack, otherStack)) {
                return otherStack;
            }
        }

        return null;
    }

    public void clear() {
        stacks.clear();
    }

    public void clean() {
        /*List<FluidStack> toRemove = stacks.values().stream()
            .filter(stack -> stack.stackSize <= 0)
            .collect(Collectors.toList());
        toRemove.forEach(stack -> stacks.remove(stack.getFluid(), stack));*/
    }

    public boolean isEmpty() {
        return stacks.isEmpty();
    }

    @Nonnull
    public Collection<FluidStack> getStacks() {
        return stacks.values();
    }

    @Nonnull
    public FluidStackList copy() {
        FluidStackList list = new FluidStackList();

        for (FluidStack stack : stacks.values()) {
            list.add(stack.copy());
        }

        return list;
    }

    @Override
    public String toString() {
        return stacks.toString();
    }
}
