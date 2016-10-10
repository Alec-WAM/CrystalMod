package com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting;

import net.minecraft.item.ItemStack;

import com.alec_wam.CrystalMod.items.ModItems;
import com.alec_wam.CrystalMod.tiles.BasicItemHandler;
import com.alec_wam.CrystalMod.tiles.BasicItemValidator;

public class TileProcessingPatternEncoder extends TilePatternEncoder {

	private BasicItemHandler patterns = new BasicItemHandler(2, this, new BasicItemValidator(ModItems.craftingPattern));
    private BasicItemHandler configuration = new BasicItemHandler(9 * 2, this);
    
    public void onCreatePattern() {
        if (mayCreatePattern()) {
            ItemStack pattern = new ItemStack(ModItems.craftingPattern);

            ItemPattern.setProcessing(pattern, true);

            for (int i = 0; i < 18; ++i) {
                if (configuration.getStackInSlot(i) != null) {
                    if (i >= 9) {
                        ItemPattern.addOutput(pattern, configuration.getStackInSlot(i));
                    } else {
                        ItemPattern.addInput(pattern, configuration.getStackInSlot(i));
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
            if (configuration.getStackInSlot(i) != null) {
                inputsFilled++;
            }
        }

        for (int i = 9; i < 18; ++i) {
            if (configuration.getStackInSlot(i) != null) {
                outputsFilled++;
            }
        }

        return inputsFilled > 0 && outputsFilled > 0 && patterns.getStackInSlot(0) != null && patterns.getStackInSlot(1) == null;
    }
    
    public BasicItemHandler getPatterns() {
        return patterns;
    }

    public BasicItemHandler getConfiguration() {
        return configuration;
    }
}
