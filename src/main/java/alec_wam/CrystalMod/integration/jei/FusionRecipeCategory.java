package alec_wam.CrystalMod.integration.jei;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.api.recipe.IFusionRecipe;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.util.ItemStackTools;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FusionRecipeCategory extends BlankRecipeCategory<FusionRecipeCategory.FusionJEIRecipe>  {

	public static final @Nonnull String UID = CrystalMod.MODID+".Fusion";
	
	public static class FusionJEIRecipe extends BlankRecipeWrapper {
		private final IJeiHelpers jeiHelpers;
		public IFusionRecipe recipe;
		
		public FusionJEIRecipe(IJeiHelpers jeiHelpers, IFusionRecipe recipe){
			this.jeiHelpers = jeiHelpers;
			this.recipe = recipe;
		}
		
		@Override
		public @Nonnull List<?> getInputs() {
			List<Object> inputs = Lists.newArrayList();
			inputs.add(recipe.getMainInput());
			inputs.addAll(recipe.getInputs());
		    return inputs;
		}
		
		@Override
		public @Nonnull List<?> getOutputs() {
			List<ItemStack> outputStacks = Lists.newArrayList();
			if(ItemStackTools.isValid(recipe.getOutput())){
				outputStacks.add(recipe.getOutput());
			}
			return outputStacks;
		}
		
		@Override
		public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
			 super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			IStackHelper stackHelper = jeiHelpers.getStackHelper();

			List<List<ItemStack>> inputs = stackHelper.expandRecipeItemStackInputs(getInputs());
			ingredients.setInputLists(ItemStack.class, inputs);

			List<ItemStack> outputStacks = Lists.newArrayList();
			if(ItemStackTools.isValid(recipe.getOutput())){
				outputStacks.add(recipe.getOutput());
			}
			ingredients.setOutputs(ItemStack.class, outputStacks);
		}
		
	}

	public static void register(IJeiHelpers jeiHelpers, IModRegistry registry, IGuiHelper guiHelper) {
	    
	    registry.addRecipeCategories(new FusionRecipeCategory(guiHelper));
	    registry.addRecipeHandlers(new RecipeHandler<FusionJEIRecipe>(FusionJEIRecipe.class, FusionRecipeCategory.UID));
	    registry.addRecipeCategoryCraftingItem(displayStack, FusionRecipeCategory.UID);

	    List<FusionJEIRecipe> result = new ArrayList<FusionJEIRecipe>();    
	    for (IFusionRecipe rec : CrystalModAPI.getFusionRecipes()) {
	      result.add(new FusionJEIRecipe(jeiHelpers, rec));
	    }    
	    registry.addRecipes(result);
	}
	
	@Nonnull
	private final IDrawable background;

	private static int xOff = 44;
	private static int yOff = 30;

  	public FusionRecipeCategory(IGuiHelper guiHelper) {
	    ResourceLocation backgroundLocation = new ResourceLocation("crystalmod", "textures/gui/machine/grinder.png");
	    background = guiHelper.createBlankDrawable(100, 100);
  	}
	
	@Override
	public IDrawable getBackground() {
		return background;
	}

	protected final static ItemStack displayStack = new ItemStack(ModBlocks.fusionPedistal);
	
	@Override
	public String getTitle() {
		return displayStack.getDisplayName();
	}

	@Override
	public String getUid() {
		return UID;
	}

	@Override
	public void drawAnimations(@Nonnull Minecraft minecraft) {
	}
	
	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {
  	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FusionJEIRecipe recipeWrapper, IIngredients arg2) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		int x = 36;
		int y = 36;
		
		guiItemStacks.init(0, false, x+55, y);
		
		guiItemStacks.init(1, true, x, y);
		int inputSize = recipeWrapper.getInputs().size()-1;
		double theta = ((Math.PI*2)/inputSize);
		for(int i = 0; i < inputSize; i++){
			double angle = theta * i;
			guiItemStacks.init(2+i, true, x + (int)(30.0D * Math.cos(angle)), y - 1 + (int)(30.0D * Math.sin(angle)));
		}	
		
		guiItemStacks.set(arg2);
	}

}
