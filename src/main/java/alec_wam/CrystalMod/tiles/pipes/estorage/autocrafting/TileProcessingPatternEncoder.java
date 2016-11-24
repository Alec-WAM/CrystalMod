package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.BasicItemHandler;
import alec_wam.CrystalMod.tiles.BasicItemValidator;
import alec_wam.CrystalMod.util.ItemStackTools;

public class TileProcessingPatternEncoder extends TilePatternEncoder {

	private BasicItemHandler patterns = new BasicItemHandler(2, this, new BasicItemValidator(ModItems.craftingPattern));
    private BasicItemHandler configuration = new BasicItemHandler(9 * 2, this);
    
    public void onCreatePattern() {
        if (mayCreatePattern()) {
            ItemStack pattern = new ItemStack(ModItems.craftingPattern);

            ItemPattern.setProcessing(pattern, true);
            ItemPattern.setOredict(pattern, this.isOreDict);

            for (int i = 0; i < 18; ++i) {
                if (!ItemStackTools.isNullStack(configuration.getStackInSlot(i))) {
                	if (i >= 9) {
                        for (int j = 0; j < ItemStackTools.getStackSize(configuration.getStackInSlot(i)); ++j) {
                            ItemPattern.addOutput(pattern, ItemHandlerHelper.copyStackWithSize(configuration.getStackInSlot(i), 1));
                        }
                    } else {
                        ItemPattern.setInput(pattern, i, configuration.getStackInSlot(i));
                    }
                }
            }

            patterns.extractItem(0, 1, false);
            patterns.setStackInSlot(1, pattern);
        }
    }
    
    public boolean mayCreatePattern() {
        int inputsFilled = 0, outputsFilled = 0;

        for (int i = 0; i < 9; ++i) {
            if (!ItemStackTools.isNullStack(configuration.getStackInSlot(i))) {
                inputsFilled++;
            }
        }

        for (int i = 9; i < 18; ++i) {
            if (!ItemStackTools.isNullStack(configuration.getStackInSlot(i))) {
                outputsFilled++;
            }
        }

        return inputsFilled > 0 && outputsFilled > 0 && !ItemStackTools.isNullStack(patterns.getStackInSlot(0)) && ItemStackTools.isNullStack(patterns.getStackInSlot(1));
    }
    
    public void fillInputs(ItemStack[] stacks){
    	for (int i = 0; i < 9; ++i) {
    		ItemStack stack = i >= stacks.length ? ItemStackTools.getEmptyStack() : stacks[i];
    		configuration.setStackInSlot(i, stack);
        }
    }
    
    public void fillOutputs(ItemStack[] stacks){
    	for (int i = 0; i < 9; ++i) {
    		ItemStack stack = i >= stacks.length ? ItemStackTools.getEmptyStack() : stacks[i];
    		configuration.setStackInSlot(9+i, stack);
        }
    }
    
    public BasicItemHandler getPatterns() {
        return patterns;
    }

    public BasicItemHandler getConfiguration() {
        return configuration;
    }
}
