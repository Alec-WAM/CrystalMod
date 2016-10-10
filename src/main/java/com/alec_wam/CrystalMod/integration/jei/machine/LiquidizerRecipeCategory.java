package com.alec_wam.CrystalMod.integration.jei.machine;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.blocks.ModBlocks;
import com.alec_wam.CrystalMod.integration.jei.RecipeHandler;
import com.alec_wam.CrystalMod.tiles.machine.crafting.BlockCrystalMachine.MachineType;
import com.alec_wam.CrystalMod.tiles.machine.crafting.liquidizer.ContainerLiquidizer;
import com.alec_wam.CrystalMod.tiles.machine.crafting.liquidizer.GuiLiquidizer;
import com.alec_wam.CrystalMod.tiles.machine.crafting.liquidizer.LiquidizerRecipeManager;
import com.alec_wam.CrystalMod.tiles.machine.crafting.liquidizer.LiquidizerRecipeManager.LiquidizerRecipe;
import com.alec_wam.CrystalMod.tiles.machine.crafting.liquidizer.TileEntityLiquidizer;
import com.alec_wam.CrystalMod.tiles.machine.crafting.press.GuiPress;
import com.alec_wam.CrystalMod.util.Lang;
import com.alec_wam.CrystalMod.util.client.RenderUtil;
import com.google.common.collect.Lists;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;

public class LiquidizerRecipeCategory extends BlankRecipeCategory<LiquidizerRecipeCategory.LiquidizerJEIRecipe>  {

	public static final @Nonnull String UID = CrystalMod.MODID+".Liquidizer";
	
	public static class LiquidizerJEIRecipe extends BlankRecipeWrapper {

		public LiquidizerRecipe recipe;
		
		public LiquidizerJEIRecipe(LiquidizerRecipe recipe){
			this.recipe = recipe;
		}
		
		@Override
		public @Nonnull List<?> getInputs() {
		    List<ItemStack> inputStacks = Lists.newArrayList(recipe.getInput());
		    return inputStacks != null ? inputStacks : new ArrayList<ItemStack>();
		}
		
		@Override
		public @Nonnull List<FluidStack> getFluidOutputs() {
			List<FluidStack> list = new ArrayList<FluidStack>();
			list.add(recipe.getOutput());
			return list;
		}
		
		@Override
	    public List<String> getTooltipStrings(int mouseX, int mouseY) {
	      List<String> res = new ArrayList<String>(1);
	      FluidStack output = recipe.getOutput();
	      if(output !=null){
	    	Rectangle outTankBounds = new Rectangle(112 - xOff, 23 - yOff, 12, 40);
	    	if (outTankBounds.contains(mouseX, mouseY)) {
	    		res.add(output.getLocalizedName()+" "+output.amount+" mB");
	    	} 
	      }
	      return res;
	    }
		
		@Override
		public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
			FluidStack output = recipe.getOutput();
		    if(output !=null){
		    	Rectangle outTankBounds = new Rectangle(112 - xOff, 23 - yOff, 12, 40);
		    	RenderUtil.renderGuiTank(output, TileEntityLiquidizer.CAPACITY, output.amount, outTankBounds.x, outTankBounds.y, 0, outTankBounds.width, outTankBounds.height);
		    }
		    
		    String energyString = getEnergyRequired()+" "+Lang.localize("power.cu");
		    minecraft.fontRendererObj.drawString(energyString, 108 - xOff, 62 - yOff, 0x808080, false);    
		    GlStateManager.color(1,1,1,1);
		}
		
		public int getEnergyRequired(){
			return recipe.getEnergy();
		}
		
	}

	public static void register(IModRegistry registry, IGuiHelper guiHelper) {
	    
	    registry.addRecipeCategories(new LiquidizerRecipeCategory(guiHelper));
	    registry.addRecipeHandlers(new RecipeHandler<LiquidizerJEIRecipe>(LiquidizerJEIRecipe.class, LiquidizerRecipeCategory.UID));
	    registry.addRecipeClickArea(GuiLiquidizer.class, 79, 34, 24, 17, LiquidizerRecipeCategory.UID);
	    registry.addRecipeCategoryCraftingItem(displayStack, LiquidizerRecipeCategory.UID);

	    List<LiquidizerJEIRecipe> result = new ArrayList<LiquidizerJEIRecipe>();    
	    for (LiquidizerRecipe rec : LiquidizerRecipeManager.getRecipes()) {
	      result.add(new LiquidizerJEIRecipe(rec));
	    }    
	    registry.addRecipes(result);

	    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerLiquidizer.class, LiquidizerRecipeCategory.UID, 36,
	        1, 0, 35);
	}
	
	@Nonnull
	private final IDrawable background;

	private static int xOff = 34;
	private static int yOff = 20;
	
	@Nonnull
	protected final IDrawableAnimated arrow;
	  
	private LiquidizerJEIRecipe currentRecipe;

  	public LiquidizerRecipeCategory(IGuiHelper guiHelper) {
	    ResourceLocation backgroundLocation = new ResourceLocation("crystalmod", "textures/gui/machine/liquidizer.png");
	    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, 125, 50);

	    IDrawableStatic arrowDrawable = guiHelper.createDrawable(backgroundLocation, 176, 0, 24, 17);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
  	}
	
	@Override
	public IDrawable getBackground() {
		return background;
	}

	protected final static ItemStack displayStack = new ItemStack(ModBlocks.crystalMachine, 1, MachineType.LIQUIDIZER.getMeta());
	
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
		arrow.draw(minecraft, 80-xOff, 34-yOff);
	}
	
	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {
	    if(currentRecipe == null) {
	      return;
	    }
	    
  	}
	
	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull LiquidizerRecipeCategory.LiquidizerJEIRecipe recipeWrapper) {
		currentRecipe = recipeWrapper;
		
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(0, true, 56 - xOff-1, 35 - yOff-1);
		guiItemStacks.setFromRecipe(0, recipeWrapper.getInputs());
	}

}
