package com.alec_wam.CrystalMod.integration.jei.crystalworkbench;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.item.ItemStack;

import com.alec_wam.CrystalMod.crafting.ShapedCrystalRecipe;

public class ShapedCrystalRecipeWrapper extends BlankRecipeWrapper implements IShapedCraftingRecipeWrapper, ICraftingRecipeWrapper {

	protected final ShapedCrystalRecipe recipe;
	
	public ShapedCrystalRecipeWrapper(@Nonnull IJeiHelpers jeiHelpers, @Nonnull ShapedCrystalRecipe recipe) {
		this.recipe = recipe;
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
	
	@Override
	public int getWidth() {
		return recipe.recipeWidth;
	}

	@Override
	public int getHeight() {
		return recipe.recipeHeight;
	}

	@Override
	@Nonnull
	public List<ItemStack> getInputs() {
		return Arrays.asList(recipe.recipeItems);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ShapedCrystalRecipeWrapper)) {
			return false;
		}
		return super.equals(obj);
	}
}
