package alec_wam.CrystalMod.crafting;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.brewing.AbstractBrewingRecipe;

public class CustomBrewingRecipe extends AbstractBrewingRecipe<ItemStack> {

    public CustomBrewingRecipe(@Nonnull ItemStack input, @Nonnull ItemStack ingredient, @Nonnull ItemStack output)
    {
        super(input, ingredient, output);
    }

    @Override
    public boolean isInput(@Nonnull ItemStack stack)
    {
        return ItemUtil.canCombine(getInput(), stack);
    }

    @Override
    public boolean isIngredient(@Nonnull ItemStack stack)
    {
        return ItemUtil.canCombine(getIngredient(), stack);
    }
}
