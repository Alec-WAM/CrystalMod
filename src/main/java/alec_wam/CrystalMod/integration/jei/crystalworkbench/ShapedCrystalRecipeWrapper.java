package alec_wam.CrystalMod.integration.jei.crystalworkbench;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.crafting.ShapedCrystalRecipe;
import alec_wam.CrystalMod.util.ItemStackTools;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.item.ItemStack;

public class ShapedCrystalRecipeWrapper extends BlankRecipeWrapper implements IShapedCraftingRecipeWrapper {
	protected final ShapedCrystalRecipe recipe;
	
	public ShapedCrystalRecipeWrapper(@Nonnull IJeiHelpers jeiHelpers, @Nonnull ShapedCrystalRecipe recipe) {
		this.recipe = recipe;
		for (ItemStack itemStack : this.recipe.recipeItems) {
			if (ItemStackTools.isValid(itemStack) && ItemStackTools.getStackSize(itemStack) !=1) {
				ItemStackTools.setStackSize(itemStack, 1);
			}
		}
	}
	
	@Override
	public int getWidth() {
		return recipe.recipeWidth;
	}

	@Override
	public int getHeight() {
		return recipe.recipeHeight;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ShapedCrystalRecipeWrapper)) {
			return false;
		}
		return super.equals(obj);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		List<ItemStack> recipeItems = Arrays.asList(recipe.recipeItems);
		ingredients.setInputs(ItemStack.class, recipeItems);		
		
		ItemStack recipeOutput = recipe.getRecipeOutput();
		if (ItemStackTools.isValid(recipeOutput)) {
			ingredients.setOutput(ItemStack.class, recipeOutput);
		}
	}
}
