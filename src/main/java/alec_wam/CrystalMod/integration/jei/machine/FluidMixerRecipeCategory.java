package alec_wam.CrystalMod.integration.jei.machine;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.integration.jei.RecipeHandler;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCrystalMachine.MachineType;
import alec_wam.CrystalMod.tiles.machine.crafting.fluidmixer.FluidMixerRecipeManager;
import alec_wam.CrystalMod.tiles.machine.crafting.fluidmixer.FluidMixerRecipeManager.FluidMixRecipe;
import alec_wam.CrystalMod.tiles.machine.crafting.fluidmixer.GuiFluidMixer;
import alec_wam.CrystalMod.util.Lang;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FluidMixerRecipeCategory extends BlankRecipeCategory<FluidMixerRecipeCategory.FluidMixJEIRecipe>  {

	public static final @Nonnull String UID = CrystalMod.MODID+".FluidMixer";
	
	public static class FluidMixJEIRecipe extends BlankRecipeWrapper {
		public FluidMixRecipe recipe;
		
		public FluidMixJEIRecipe(FluidMixRecipe recipe){
			this.recipe = recipe;
		}
		
		@Override
	    public List<String> getTooltipStrings(int mouseX, int mouseY) {
		  if(mouseX > 49-xOff && mouseX < 49+24-xOff && mouseY > 33-yOff && mouseY < 33+17-yOff){
			  String leftUsage = "";
			  if(recipe.getLeftConsumption() <= 0){
				  leftUsage = Lang.localize("gui.free");
			  } else {
				  leftUsage = recipe.getLeftConsumption() + " mB";
			  }
			  return Lists.newArrayList("Cost: "+leftUsage);
		  }
		  if(mouseX > 103-xOff && mouseX < 103+24-xOff && mouseY > 33-yOff && mouseY < 33+17-yOff){
			  String rightUsage = "";
			  if(recipe.getRightConsumption() <= 0){
				  rightUsage = Lang.localize("gui.free");
			  } else {
				  rightUsage = recipe.getRightConsumption() + " mB";
			  }
			  return Lists.newArrayList("Cost: "+rightUsage);
		  }
	      return super.getTooltipStrings(mouseX, mouseY);
	    }
		
		@Override
		public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
			String energyString = getEnergyRequired()+" "+Lang.localize("power.cu");
		    int width = minecraft.fontRendererObj.getStringWidth(energyString);
			minecraft.fontRendererObj.drawString(energyString, 89 - (width / 2) - xOff, 20 - yOff, 0x808080, false);  
			GlStateManager.pushMatrix();
	        RenderHelper.enableGUIStandardItemLighting();
			minecraft.getRenderItem().renderItemAndEffectIntoGUI(recipe.getOutput(), 80 - xOff, 61 - yOff);
			GlStateManager.popMatrix();
		    GlStateManager.color(1,1,1,1);
		}
		
		public int getEnergyRequired(){
			return recipe.getEnergy();
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			List<List<FluidStack>> fluids = Lists.newArrayList();
			if(recipe.getInputs() !=null){
				fluids.add(Lists.newArrayList(recipe.getLeftFluidInput()));
				fluids.add(Lists.newArrayList(recipe.getRightFluidInput()));
			} 
			ingredients.setInputLists(FluidStack.class, fluids);

			ItemStack recipeOutput = recipe.getOutput();
			if (recipeOutput != null) {
				ingredients.setOutput(ItemStack.class, recipeOutput);
			}
		}
		
	}

	public static void register(IJeiHelpers jeiHelpers, IModRegistry registry, IGuiHelper guiHelper) {
	    
	    registry.addRecipeCategories(new FluidMixerRecipeCategory(guiHelper));
	    registry.addRecipeHandlers(new RecipeHandler<FluidMixJEIRecipe>(FluidMixJEIRecipe.class, FluidMixerRecipeCategory.UID));
	    registry.addRecipeClickArea(GuiFluidMixer.class, 103, 34, 24, 17, FluidMixerRecipeCategory.UID);
	    registry.addRecipeClickArea(GuiFluidMixer.class, 53, 34, 24, 17, FluidMixerRecipeCategory.UID);

	    registry.addRecipeCategoryCraftingItem(displayStack, FluidMixerRecipeCategory.UID);

	    List<FluidMixJEIRecipe> result = new ArrayList<FluidMixJEIRecipe>();    
	    for (FluidMixRecipe rec : FluidMixerRecipeManager.getRecipes()) {
	      result.add(new FluidMixJEIRecipe(rec));
	    }    
	    registry.addRecipes(result);
	}
	
	@Nonnull
	private final IDrawable background;

	private static int xOff = 24;
	private static int yOff = 20;
	
	@Nonnull
	protected final IDrawableAnimated arrowLeft;
	@Nonnull
	protected final IDrawableAnimated arrowRight;
	  
	public FluidMixerRecipeCategory(IGuiHelper guiHelper) {
	    ResourceLocation backgroundLocation = GuiFluidMixer.TEXTURE;
	    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, 125, 60);

	    IDrawableStatic arrowDrawable = guiHelper.createDrawable(backgroundLocation, 176, 0, 24, 17);
		this.arrowLeft = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
		IDrawableStatic arrowDrawable2 = guiHelper.createDrawable(backgroundLocation, 176, 17, 24, 34);
		this.arrowRight = guiHelper.createAnimatedDrawable(arrowDrawable2, 200, IDrawableAnimated.StartDirection.RIGHT, false);
  	}
	
	@Override
	public IDrawable getBackground() {
		return background;
	}

	protected final static ItemStack displayStack = new ItemStack(ModBlocks.crystalMachine, 1, MachineType.FLUID_MIXER.getMeta());
	
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
		arrowLeft.draw(minecraft, 49-xOff, 33-yOff);
		arrowRight.draw(minecraft, 103-xOff, 33-yOff);
	}	

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FluidMixJEIRecipe recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(0, false, 80 - xOff-1, 34 - yOff-1);
		guiItemStacks.set(ingredients);
		
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
		guiFluidStacks.init(0, true, 33 - xOff-1, 24 - yOff-1, 12, 40, Fluid.BUCKET_VOLUME * 8, true, null);
		guiFluidStacks.init(1, true, 133 - xOff-1, 24 - yOff-1, 12, 40, Fluid.BUCKET_VOLUME * 8, true, null);
		guiFluidStacks.set(ingredients);
	}

}
