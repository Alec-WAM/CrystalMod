package alec_wam.CrystalMod.integration.jei;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.BlankRecipeWrapper;

public class RecipeHandler<T extends BlankRecipeWrapper> extends  BaseRecipeHandler<T> {

  public RecipeHandler(@Nonnull Class<T> clazz, @Nonnull String uid) {
    super(clazz, uid);    
  }

  @Override
  public boolean isRecipeValid(@Nonnull T recipe) {
    return super.isRecipeValid(recipe);
  }

}