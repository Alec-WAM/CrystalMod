package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task;

import net.minecraft.item.ItemStack;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import alec_wam.CrystalMod.util.ItemUtil;

public class CraftingProcess{
    private CraftingPattern pattern;
    private int pos;
    private boolean satisfied[];

    public CraftingProcess(CraftingPattern pattern) {
        this.pattern = pattern;
        this.satisfied = new boolean[pattern.getOutputs().size()];
    }

    public CraftingPattern getPattern() {
        return pattern;
    }

    public void nextStack() {
        ++pos;
    }

    public ItemStack getStackToInsert() {
        if (pos > pattern.getInputs().size() - 1) {
            return null;
        }

        return pattern.getInputs().get(pos);
    }

    public boolean hasReceivedOutputs() {
        for (boolean item : satisfied) {
            if (!item) {
                return false;
            }
        }

        return true;
    }

    public boolean hasReceivedOutput(int i) {
        return satisfied[i];
    }

    public boolean onReceiveOutput(ItemStack stack) {
        for (int i = 0; i < pattern.getOutputs().size(); ++i) {
            if (!satisfied[i]) {
                ItemStack item = pattern.getOutputs().get(i);

                if (ItemUtil.canCombine(stack, item)) {
                    satisfied[i] = true;

                    return true;
                }
            }
        }

        return false;
    }
}
