package alec_wam.CrystalMod.integration.jei.crystalworkbench;

import java.util.List;

import alec_wam.CrystalMod.crafting.ShapedOreCrystalRecipe;
import alec_wam.CrystalMod.integration.jei.CrystalModRecipeUids;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;

public class ShapedCrystalOreRecipeHandler implements IRecipeHandler<ShapedOreCrystalRecipe> {
	private final IJeiHelpers jeiHelpers;

	public ShapedCrystalOreRecipeHandler(IJeiHelpers jeiHelpers) {
		this.jeiHelpers = jeiHelpers;
	}

	@Override
	public Class<ShapedOreCrystalRecipe> getRecipeClass() {
		return ShapedOreCrystalRecipe.class;
	}

	@Override
	public String getRecipeCategoryUid(ShapedOreCrystalRecipe recipe) {
		return CrystalModRecipeUids.WORKBENCH;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(ShapedOreCrystalRecipe recipe) {
		return new ShapedOreCrystalRecipeWrapper(jeiHelpers, recipe);
	}

	@Override
	public boolean isRecipeValid(ShapedOreCrystalRecipe recipe) {
		if (recipe.getRecipeOutput() == null) {
			String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, this);
			Log.error("Recipe has no output. {}", recipeInfo);
			return false;
		}
		int inputCount = 0;
		for (Object input : recipe.getInput()) {
			if (input instanceof List && ((List<?>) input).isEmpty()) {
				// missing items for an oreDict name. This is normal behavior, but the recipe is invalid.
				return false;
			}
			if (input != null) {
				inputCount++;
			}
		}
		if (inputCount > 9) {
			String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, this);
			Log.error("Recipe has too many inputs. {}", recipeInfo);
			return false;
		}
		if (inputCount == 0) {
			String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, this);
			Log.error("Recipe has no inputs. {}", recipeInfo);
			return false;
		}
		return true;
	}
}