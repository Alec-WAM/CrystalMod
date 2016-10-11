package alec_wam.CrystalMod.integration.jei.crystalworkbench;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import alec_wam.CrystalMod.crafting.ShapelessCrystalRecipe;

public class ShapelessCrystalRecipeWrapper extends BlankRecipeWrapper implements ICraftingRecipeWrapper {

	protected final ShapelessCrystalRecipe recipe;
	
	public ShapelessCrystalRecipeWrapper(@Nonnull IJeiHelpers jeiHelpers, @Nonnull ShapelessCrystalRecipe recipe2) {
		this.recipe = recipe2;
		for (ItemStack itemStack : this.recipe.recipeItems) {
			if (itemStack != null && itemStack.stackSize != 1) {
				itemStack.stackSize = 1;
			}
		}
	}

	@Override
	@Nonnull
	public List<ItemStack> getOutputs() {
		return Collections.singletonList(recipe.getRecipeOutput());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@Nonnull
	public List getInputs() {
		return Arrays.asList(recipe.recipeItems);
	}
}
