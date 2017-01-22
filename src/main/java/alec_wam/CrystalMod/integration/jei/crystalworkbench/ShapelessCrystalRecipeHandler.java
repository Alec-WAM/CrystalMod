package alec_wam.CrystalMod.integration.jei.crystalworkbench;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.crafting.ShapelessCrystalRecipe;
import alec_wam.CrystalMod.integration.jei.CrystalModRecipeUids;

public class ShapelessCrystalRecipeHandler implements IRecipeHandler<ShapelessCrystalRecipe> {
	@Nonnull
	private final IJeiHelpers jeiHelpers;

	public ShapelessCrystalRecipeHandler(@Nonnull IJeiHelpers jeiHelpers) {
		this.jeiHelpers = jeiHelpers;
	}

	@Nonnull
	@Override
	public Class<ShapelessCrystalRecipe> getRecipeClass() {
		return ShapelessCrystalRecipe.class;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull ShapelessCrystalRecipe recipe) {
		return new ShapelessCrystalRecipeWrapper(jeiHelpers, recipe);
	}

	@Override
	public boolean isRecipeValid(@Nonnull ShapelessCrystalRecipe recipe) {
		if (recipe.getRecipeOutput() == null) {
			String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, this);
			Log.error("Recipe has no output. {}", recipeInfo);
			return false;
		}
		int inputCount = 0;
		for (Object input : recipe.recipeItems) {
			if (input instanceof ItemStack) {
				inputCount++;
			} else {
				String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, this);
				Log.error("Recipe has an input that is not an ItemStack. {}", recipeInfo);
				return false;
			}
		}
		if (inputCount > 9) {
			String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, this);
			Log.error("Recipe has too many inputs. {}", recipeInfo);
			return false;
		}
		return inputCount > 0;
	}

	@Override
	public String getRecipeCategoryUid(ShapelessCrystalRecipe arg0) {
		return CrystalModRecipeUids.WORKBENCH;
	}
}
