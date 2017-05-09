package alec_wam.CrystalMod.integration.jei.crystalworkbench;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.crafting.ShapedCrystalRecipe;
import alec_wam.CrystalMod.integration.jei.CrystalModRecipeUids;
import alec_wam.CrystalMod.util.ItemStackTools;

public class ShapedCrystalRecipeHandler implements IRecipeHandler<ShapedCrystalRecipe> {
	@Nonnull
	private final IJeiHelpers jeiHelpers;

	public ShapedCrystalRecipeHandler(@Nonnull IJeiHelpers jeiHelpers) {
		this.jeiHelpers = jeiHelpers;
	}

	@Nonnull
	@Override
	public Class<ShapedCrystalRecipe> getRecipeClass() {
		return ShapedCrystalRecipe.class;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull ShapedCrystalRecipe recipe) {
		return new ShapedCrystalRecipeWrapper(jeiHelpers, recipe);
	}

	@Override
	public boolean isRecipeValid(@Nonnull ShapedCrystalRecipe recipe) {
		if (ItemStackTools.isEmpty(recipe.getRecipeOutput())) {
			String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, this);
			Log.error("Recipe has no outputs. {}", recipeInfo);
			return false;
		}
		int inputCount = 0;
		for (ItemStack input : recipe.recipeItems) {
			if (ItemStackTools.isValid(input)) {
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

	@Override
	public String getRecipeCategoryUid(ShapedCrystalRecipe arg0) {
		return CrystalModRecipeUids.WORKBENCH;
	}
}
