package com.alec_wam.CrystalMod.integration.jei.crystalworkbench;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

import com.alec_wam.CrystalMod.crafting.ShapedCrystalRecipe;
import com.alec_wam.CrystalMod.integration.jei.CrystalModRecipeUids;

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
	public String getRecipeCategoryUid() {
		return CrystalModRecipeUids.WORKBENCH;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull ShapedCrystalRecipe recipe) {
		return new ShapedCrystalRecipeWrapper(jeiHelpers, recipe);
	}

	@Override
	public boolean isRecipeValid(@Nonnull ShapedCrystalRecipe recipe) {
		if (recipe.getRecipeOutput() == null) {
			String recipeInfo = ErrorUtil.getInfoFromBrokenRecipe(recipe, this);
			Log.error("Recipe has no outputs. {}", recipeInfo);
			return false;
		}
		int inputCount = 0;
		for (ItemStack input : recipe.recipeItems) {
			if (input != null) {
				inputCount++;
			}
		}
		if (inputCount > 9) {
			String recipeInfo = ErrorUtil.getInfoFromBrokenRecipe(recipe, this);
			Log.error("Recipe has too many inputs. {}", recipeInfo);
			return false;
		}
		if (inputCount == 0) {
			String recipeInfo = ErrorUtil.getInfoFromBrokenRecipe(recipe, this);
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
