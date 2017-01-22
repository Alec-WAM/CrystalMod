package alec_wam.CrystalMod.integration.jei.machine;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.integration.jei.RecipeHandler;
import alec_wam.CrystalMod.tiles.machine.BasicMachineRecipe;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCrystalMachine.MachineType;
import alec_wam.CrystalMod.tiles.machine.crafting.liquidizer.TileEntityLiquidizer;
import alec_wam.CrystalMod.tiles.machine.crafting.press.ContainerPress;
import alec_wam.CrystalMod.tiles.machine.crafting.press.GuiPress;
import alec_wam.CrystalMod.tiles.machine.crafting.press.PressRecipeManager;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.client.RenderUtil;

import com.google.common.collect.Lists;

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

public class PressRecipeCategory extends BlankRecipeCategory<PressRecipeCategory.PressRecipe>  {

	public static final @Nonnull String UID = CrystalMod.MODID+".Press";
	
	public static class PressRecipe extends BlankRecipeWrapper {
		private final IJeiHelpers jeiHelpers;
		public BasicMachineRecipe recipe;
		
		public PressRecipe(IJeiHelpers jeiHelpers, BasicMachineRecipe recipe){
			this.jeiHelpers = jeiHelpers;
			this.recipe = recipe;
		}
		
		@Override
		public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
			String energyString = getEnergyRequired()+" "+Lang.localize("power.cu");
		    minecraft.fontRendererObj.drawString(energyString, 108 - xOff, 62 - yOff, 0x808080, false);    
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

			ItemStack recipeOutput = recipe.getOutput();
			if (recipeOutput != null) {
				ingredients.setOutput(ItemStack.class, recipeOutput);
			}
		}
		
	}

	public static void register(IJeiHelpers jeiHelpers, IModRegistry registry, IGuiHelper guiHelper) {
	    
	    registry.addRecipeCategories(new PressRecipeCategory(guiHelper));
	    registry.addRecipeHandlers(new RecipeHandler<PressRecipe>(PressRecipe.class, PressRecipeCategory.UID));
	    registry.addRecipeClickArea(GuiPress.class, 79, 34, 24, 17, PressRecipeCategory.UID);
	    registry.addRecipeCategoryCraftingItem(displayStack, PressRecipeCategory.UID);

	    List<PressRecipe> result = new ArrayList<PressRecipe>();    
	    for (BasicMachineRecipe rec : PressRecipeManager.getRecipes()) {
	      result.add(new PressRecipe(jeiHelpers, rec));
	    }    
	    registry.addRecipes(result);

	    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerPress.class, PressRecipeCategory.UID, 36,
	        1, 0, 35);
	}
	
	@Nonnull
	private final IDrawable background;

	private static int xOff = 34;
	private static int yOff = 30;
	
	@Nonnull
	protected final IDrawableAnimated arrow;
	  
	private PressRecipe currentRecipe;

  	public PressRecipeCategory(IGuiHelper guiHelper) {
	    ResourceLocation backgroundLocation = new ResourceLocation("crystalmod", "textures/gui/machine/press.png");
	    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, 125, 50);

	    IDrawableStatic arrowDrawable = guiHelper.createDrawable(backgroundLocation, 176, 0, 24, 17);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
  	}
	
	@Override
	public IDrawable getBackground() {
		return background;
	}

	protected final static ItemStack displayStack = new ItemStack(ModBlocks.crystalMachine, 1, MachineType.PRESS.getMeta());
	
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
	public void setRecipe(IRecipeLayout recipeLayout, PressRecipe recipeWrapper, IIngredients arg2) {
		currentRecipe = recipeWrapper;
		
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(0, true, 56 - xOff-1, 35 - yOff-1);
		guiItemStacks.init(1, false, 116 - xOff-1, 35 - yOff-1);
		guiItemStacks.set(arg2);
	}

}
