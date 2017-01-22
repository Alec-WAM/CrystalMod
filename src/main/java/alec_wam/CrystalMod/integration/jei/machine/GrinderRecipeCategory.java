package alec_wam.CrystalMod.integration.jei.machine;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.integration.jei.RecipeHandler;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCrystalMachine.MachineType;
import alec_wam.CrystalMod.tiles.machine.crafting.grinder.ContainerGrinder;
import alec_wam.CrystalMod.tiles.machine.crafting.grinder.GrinderManager;
import alec_wam.CrystalMod.tiles.machine.crafting.grinder.GrinderManager.GrinderRecipe;
import alec_wam.CrystalMod.tiles.machine.crafting.grinder.GuiGrinder;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.Lang;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GrinderRecipeCategory extends BlankRecipeCategory<GrinderRecipeCategory.GrinderJEIRecipe>  {

	public static final @Nonnull String UID = CrystalMod.MODID+".Grinder";
	
	public static class GrinderJEIRecipe extends BlankRecipeWrapper {
		private final IJeiHelpers jeiHelpers;
		public GrinderRecipe recipe;
		
		public GrinderJEIRecipe(IJeiHelpers jeiHelpers, GrinderRecipe recipe){
			this.jeiHelpers = jeiHelpers;
			this.recipe = recipe;
		}
		
		@Override
		public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
			String energyString = getEnergyRequired()+" "+Lang.localize("power.cu");
		    minecraft.fontRendererObj.drawString(energyString, 108 - xOff, 62 - yOff, 0x808080, false);
		    
		    if(ItemStackTools.isValid(recipe.getSecondaryOutput())){
		    	minecraft.fontRendererObj.drawString(recipe.getSecondaryChance()+"%", 163 - xOff, 40 - yOff, 0x808080, false);
		    }
		    GlStateManager.color(1,1,1,1);
		}
		
		public int getEnergyRequired(){
			return recipe.getEnergy();
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			IStackHelper stackHelper = jeiHelpers.getStackHelper();

			List<List<ItemStack>> inputs = stackHelper.expandRecipeItemStackInputs(recipe.getInputs());
			ingredients.setInputLists(ItemStack.class, inputs);

			List<ItemStack> outputStacks = Lists.newArrayList();
			if(ItemStackTools.isValid(recipe.getMainOutput())){
				outputStacks.add(recipe.getMainOutput());
			}
			if(ItemStackTools.isValid(recipe.getSecondaryOutput())){
				outputStacks.add(recipe.getSecondaryOutput());
			}
			ingredients.setOutputs(ItemStack.class, outputStacks);
		}
		
	}

	public static void register(IJeiHelpers jeiHelpers, IModRegistry registry, IGuiHelper guiHelper) {
	    
	    registry.addRecipeCategories(new GrinderRecipeCategory(guiHelper));
	    registry.addRecipeHandlers(new RecipeHandler<GrinderJEIRecipe>(GrinderJEIRecipe.class, GrinderRecipeCategory.UID));
	    registry.addRecipeClickArea(GuiGrinder.class, 79, 34, 24, 17, GrinderRecipeCategory.UID);
	    registry.addRecipeCategoryCraftingItem(displayStack, GrinderRecipeCategory.UID);

	    List<GrinderJEIRecipe> result = new ArrayList<GrinderJEIRecipe>();    
	    for (GrinderRecipe rec : GrinderManager.getRecipes()) {
	      result.add(new GrinderJEIRecipe(jeiHelpers, rec));
	    }    
	    registry.addRecipes(result);

	    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerGrinder.class, GrinderRecipeCategory.UID, 36,
	        1, 0, 35);
	}
	
	@Nonnull
	private final IDrawable background;

	private static int xOff = 44;
	private static int yOff = 30;
	
	@Nonnull
	protected final IDrawableAnimated arrow;
	  
	private GrinderJEIRecipe currentRecipe;

  	public GrinderRecipeCategory(IGuiHelper guiHelper) {
	    ResourceLocation backgroundLocation = new ResourceLocation("crystalmod", "textures/gui/machine/grinder.png");
	    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, 125, 50);

	    IDrawableStatic arrowDrawable = guiHelper.createDrawable(backgroundLocation, 176, 0, 24, 17);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
  	}
	
	@Override
	public IDrawable getBackground() {
		return background;
	}

	protected final static ItemStack displayStack = new ItemStack(ModBlocks.crystalMachine, 1, MachineType.GRINDER.getMeta());
	
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
		arrow.draw(minecraft, 80-xOff, 34-yOff);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, GrinderJEIRecipe recipeWrapper, IIngredients arg2) {
		currentRecipe = recipeWrapper;
		
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(0, true, 56 - xOff-1, 35 - yOff-1);
		guiItemStacks.init(1, false, 116 - xOff-1, 35 - yOff-1);
		guiItemStacks.init(2, false, 142 - xOff-1, 35 - yOff-1);
		guiItemStacks.set(arg2);
	}

}
