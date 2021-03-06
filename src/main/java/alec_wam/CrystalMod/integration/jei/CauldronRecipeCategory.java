package alec_wam.CrystalMod.integration.jei;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.tiles.cauldron.CauldronRecipeManager;
import alec_wam.CrystalMod.tiles.cauldron.CauldronRecipeManager.InfusionRecipe;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.client.RenderUtil;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class CauldronRecipeCategory extends BlankRecipeCategory<CauldronRecipeCategory.CauldronJEIRecipe>  {

	public static final @Nonnull String UID = CrystalMod.MODID+".Cauldron";
	
	public static class CauldronJEIRecipe extends BlankRecipeWrapper {
		private final IJeiHelpers jeiHelpers;
		public InfusionRecipe recipe;
		
		public CauldronJEIRecipe(IJeiHelpers jeiHelpers, InfusionRecipe recipe){
			this.jeiHelpers = jeiHelpers;
			this.recipe = recipe;
		}
		
		@Override
		public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
			 super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			IStackHelper stackHelper = jeiHelpers.getStackHelper();

			List<Object> inputList = Lists.newArrayList();
			inputList.add(recipe.getInput());
			List<List<ItemStack>> inputs = stackHelper.expandRecipeItemStackInputs(inputList);
			ingredients.setInputLists(ItemStack.class, inputs);

			List<List<FluidStack>> fluids = Lists.newArrayList();
			if(recipe.getFluidInput() !=null){
				fluids.add(Lists.newArrayList(recipe.getFluidInput()));
			} else {
				List<FluidStack> stacks = Lists.newArrayList();
				stacks.add(new FluidStack(ModFluids.fluidBlueCrystal, 1));
				stacks.add(new FluidStack(ModFluids.fluidRedCrystal, 1));
				stacks.add(new FluidStack(ModFluids.fluidGreenCrystal, 1));
				stacks.add(new FluidStack(ModFluids.fluidDarkCrystal, 1));
				fluids.add(stacks);
			}
			ingredients.setInputLists(FluidStack.class, fluids);
			
			List<ItemStack> outputStacks = Lists.newArrayList();
			if(ItemStackTools.isValid(recipe.getOutput())){
				outputStacks.add(recipe.getOutput());
			}
			ingredients.setOutputs(ItemStack.class, outputStacks);
		}
		
	}

	public static void register(IJeiHelpers jeiHelpers, IModRegistry registry, IGuiHelper guiHelper) {
	    
	    registry.addRecipeCategories(new CauldronRecipeCategory(guiHelper));
	    registry.addRecipeHandlers(new RecipeHandler<CauldronJEIRecipe>(CauldronJEIRecipe.class, CauldronRecipeCategory.UID));
	    registry.addRecipeCategoryCraftingItem(displayStack, CauldronRecipeCategory.UID);

	    List<CauldronJEIRecipe> result = new ArrayList<CauldronJEIRecipe>();    
	    for (InfusionRecipe rec : CauldronRecipeManager.getRecipes()) {
	      result.add(new CauldronJEIRecipe(jeiHelpers, rec));
	    }    
	    registry.addRecipes(result);
	}
	
	@Nonnull
	private final IDrawable background;

	public CauldronRecipeCategory(IGuiHelper guiHelper) {
	    background = guiHelper.createBlankDrawable(100, 100);
  	}
	
	@Override
	public IDrawable getBackground() {
		return background;
	}

	protected final static ItemStack displayStack = new ItemStack(ModBlocks.cauldron);
	
	@Override
	public String getTitle() {
		return displayStack.getDisplayName();
	}

	@Override
	public String getUid() {
		return UID;
	}
	
	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {
		TextureAtlasSprite sprite = RenderUtil.getSprite("crystalmod:blocks/cauldron/cauldron_side");
		minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		RenderUtil.renderTexturedQuad(36-8, 36, 36+32-8, 36+32, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV());
  	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CauldronJEIRecipe recipeWrapper, IIngredients arg2) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		int x = 36;
		int y = 36;
		
		guiItemStacks.init(0, false, x+30, y);
		
		guiItemStacks.init(1, true, x, y-20);
		guiItemStacks.set(arg2);
		
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
		guiFluidStacks.init(0, true, x, y+5, 16, 16, Fluid.BUCKET_VOLUME, true, null);
		guiFluidStacks.set(arg2);
	}

}
