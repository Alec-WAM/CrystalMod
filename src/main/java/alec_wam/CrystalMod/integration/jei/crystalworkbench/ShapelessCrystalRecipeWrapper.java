package alec_wam.CrystalMod.integration.jei.crystalworkbench;

import java.util.List;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.crafting.ShapelessCrystalRecipe;
import alec_wam.CrystalMod.util.ItemStackTools;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;

public class ShapelessCrystalRecipeWrapper extends BlankRecipeWrapper implements IRecipeWrapper {
	private final IJeiHelpers jeiHelpers;
	protected final ShapelessCrystalRecipe recipe;
	
	public ShapelessCrystalRecipeWrapper(@Nonnull IJeiHelpers jeiHelpers, @Nonnull ShapelessCrystalRecipe recipe2) {
		this.jeiHelpers = jeiHelpers;
		this.recipe = recipe2;
		for (ItemStack itemStack : this.recipe.recipeItems) {
			if (ItemStackTools.isValid(itemStack) && ItemStackTools.getStackSize(itemStack) !=1) {
				ItemStackTools.setStackSize(itemStack, 1);
			}
		}
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		IStackHelper stackHelper = jeiHelpers.getStackHelper();

		List<List<ItemStack>> inputs = stackHelper.expandRecipeItemStackInputs(recipe.recipeItems);
		ingredients.setInputLists(ItemStack.class, inputs);

		ItemStack recipeOutput = recipe.getRecipeOutput();
		if (recipeOutput != null) {
			ingredients.setOutput(ItemStack.class, recipeOutput);
		}
	}
}
