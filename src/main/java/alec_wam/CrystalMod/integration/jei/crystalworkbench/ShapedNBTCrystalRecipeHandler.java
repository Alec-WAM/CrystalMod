package alec_wam.CrystalMod.integration.jei.crystalworkbench;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.crafting.ShapedCrystalRecipe;
import alec_wam.CrystalMod.crafting.ShapedNBTRecipe;
import alec_wam.CrystalMod.integration.jei.CrystalModRecipeUids;
import alec_wam.CrystalMod.util.ItemStackTools;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;
import net.minecraft.item.ItemStack;

public class ShapedNBTCrystalRecipeHandler implements IRecipeHandler<ShapedNBTRecipe> {
	@Nonnull
	private final IJeiHelpers jeiHelpers;

	public ShapedNBTCrystalRecipeHandler(@Nonnull IJeiHelpers jeiHelpers) {
		this.jeiHelpers = jeiHelpers;
	}

	@Nonnull
	@Override
	public Class<ShapedNBTRecipe> getRecipeClass() {
		return ShapedNBTRecipe.class;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull ShapedNBTRecipe recipe) {
		return new ShapedCrystalRecipeWrapper(jeiHelpers, recipe);
	}

	@Override
	public boolean isRecipeValid(@Nonnull ShapedNBTRecipe recipe) {
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
	public String getRecipeCategoryUid(ShapedNBTRecipe arg0) {
		return CrystalModRecipeUids.WORKBENCH;
	}
}
