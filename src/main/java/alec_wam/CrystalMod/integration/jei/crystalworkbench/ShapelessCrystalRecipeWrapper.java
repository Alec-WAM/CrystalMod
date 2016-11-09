package alec_wam.CrystalMod.integration.jei.crystalworkbench;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import alec_wam.CrystalMod.crafting.ShapelessCrystalRecipe;

public class ShapelessCrystalRecipeWrapper extends BlankRecipeWrapper implements ICraftingRecipeWrapper {
	private final IJeiHelpers jeiHelpers;
	protected final ShapelessCrystalRecipe recipe;
	
	public ShapelessCrystalRecipeWrapper(@Nonnull IJeiHelpers jeiHelpers, @Nonnull ShapelessCrystalRecipe recipe2) {
		this.jeiHelpers = jeiHelpers;
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

	@Override
	public void getIngredients(IIngredients ingredients) {
		IStackHelper stackHelper = jeiHelpers.getStackHelper();

		List<List<ItemStack>> inputs = stackHelper.expandRecipeItemStackInputs(getInputs());
		ingredients.setInputLists(ItemStack.class, inputs);

		ItemStack recipeOutput = recipe.getRecipeOutput();
		if (recipeOutput != null) {
			ingredients.setOutput(ItemStack.class, recipeOutput);
		}
	}
}
