package alec_wam.CrystalMod.integration.jei.machine;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.integration.jei.RecipeHandler;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCrystalMachine.MachineType;
import alec_wam.CrystalMod.tiles.machine.crafting.infuser.ContainerCrystalInfuser;
import alec_wam.CrystalMod.tiles.machine.crafting.infuser.CrystalInfusionManager;
import alec_wam.CrystalMod.tiles.machine.crafting.infuser.GuiCrystalInfuser;
import alec_wam.CrystalMod.tiles.machine.crafting.infuser.CrystalInfusionManager.InfusionMachineRecipe;
import alec_wam.CrystalMod.tiles.machine.crafting.liquidizer.ContainerLiquidizer;
import alec_wam.CrystalMod.tiles.machine.crafting.liquidizer.GuiLiquidizer;
import alec_wam.CrystalMod.tiles.machine.crafting.liquidizer.LiquidizerRecipeManager;
import alec_wam.CrystalMod.tiles.machine.crafting.liquidizer.TileEntityLiquidizer;
import alec_wam.CrystalMod.tiles.machine.crafting.liquidizer.LiquidizerRecipeManager.LiquidizerRecipe;
import alec_wam.CrystalMod.tiles.machine.crafting.press.GuiPress;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.client.RenderUtil;

import com.google.common.collect.Lists;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;

public class InfuserRecipeCategory extends BlankRecipeCategory<InfuserRecipeCategory.InfuserJEIRecipe>  {

	public static final @Nonnull String UID = CrystalMod.MODID+".Infuser";
	
	public static class InfuserJEIRecipe extends BlankRecipeWrapper {

		public InfusionMachineRecipe recipe;
		
		public InfuserJEIRecipe(InfusionMachineRecipe recipe){
			this.recipe = recipe;
		}
		
		@Override
		public @Nonnull List<?> getInputs() {
		    List<ItemStack> inputStacks = recipe.getInputs();
		    return inputStacks != null ? inputStacks : new ArrayList<ItemStack>();
		}
		
		@Override
		public @Nonnull List<?> getOutputs() {
		    ItemStack out = recipe.getOutput();
		    return out != null ? Lists.newArrayList(out) : new ArrayList<ItemStack>();
		}
		
		@Override
		public @Nonnull List<FluidStack> getFluidInputs() {
			List<FluidStack> list = new ArrayList<FluidStack>();
			list.add(recipe.getFluidInput());
			return list;
		}
		
		@Override
	    public List<String> getTooltipStrings(int mouseX, int mouseY) {
	      /*List<String> res = new ArrayList<String>(1);
	      FluidStack output = recipe.getFluidInput();
	      if(output !=null){
	    	Rectangle outTankBounds = new Rectangle(112 - xOff, 23 - yOff, 12, 40);
	    	if (outTankBounds.contains(mouseX, mouseY)) {
	    		res.add(output.getLocalizedName()+" "+output.amount+" mB");
	    	} 
	      }*/
	      return super.getTooltipStrings(mouseX, mouseY);
	    }
		
		@Override
		public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
			/*FluidStack output = recipe.getFluidInput();
		    if(output !=null){
		    	Rectangle outTankBounds = new Rectangle(112 - xOff, 23 - yOff, 12, 40);
		    	RenderUtil.renderGuiTank(output, TileEntityLiquidizer.CAPACITY, output.amount, outTankBounds.x, outTankBounds.y, 0, outTankBounds.width, outTankBounds.height);
		    }*/
		    
		    String energyString = getEnergyRequired()+" "+Lang.localize("power.cu");
		    minecraft.fontRendererObj.drawString(energyString, 108 - xOff, 62 - yOff, 0x808080, false);    
		    GlStateManager.color(1,1,1,1);
		}
		
		public int getEnergyRequired(){
			return recipe.getEnergy();
		}
		
	}

	public static void register(IModRegistry registry, IGuiHelper guiHelper) {
	    
	    registry.addRecipeCategories(new InfuserRecipeCategory(guiHelper));
	    registry.addRecipeHandlers(new RecipeHandler<InfuserJEIRecipe>(InfuserJEIRecipe.class, InfuserRecipeCategory.UID));
	    registry.addRecipeClickArea(GuiCrystalInfuser.class, 79, 34, 24, 17, InfuserRecipeCategory.UID);
	    registry.addRecipeCategoryCraftingItem(displayStack, InfuserRecipeCategory.UID);

	    List<InfuserJEIRecipe> result = new ArrayList<InfuserJEIRecipe>();    
	    for (InfusionMachineRecipe rec : CrystalInfusionManager.getRecipes()) {
	      result.add(new InfuserJEIRecipe(rec));
	    }    
	    registry.addRecipes(result);

	    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerCrystalInfuser.class, InfuserRecipeCategory.UID, 36,
	        1, 0, 35);
	}
	
	@Nonnull
	private final IDrawable background;

	private static int xOff = 24;
	private static int yOff = 20;
	
	@Nonnull
	protected final IDrawableAnimated arrow;
	@Nonnull
	protected final IDrawableAnimated arrow2;
	  
	private InfuserJEIRecipe currentRecipe;

  	public InfuserRecipeCategory(IGuiHelper guiHelper) {
	    ResourceLocation backgroundLocation = new ResourceLocation("crystalmod", "textures/gui/machine/infuser.png");
	    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, 125, 50);

	    IDrawableStatic arrowDrawable = guiHelper.createDrawable(backgroundLocation, 176, 0, 24, 17);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
		
		this.arrow2 = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.RIGHT, false);
  	}
	
	@Override
	public IDrawable getBackground() {
		return background;
	}

	protected final static ItemStack displayStack = new ItemStack(ModBlocks.crystalMachine, 1, MachineType.INFUSER.getMeta());
	
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
		arrow.draw(minecraft, 90-xOff, 34-yOff);
		arrow2.draw(minecraft, 110-xOff, 34-yOff);
	}
	
	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {
	    if(currentRecipe == null) {
	      return;
	    }
	    
  	}
	
	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull InfuserRecipeCategory.InfuserJEIRecipe recipeWrapper) {
		currentRecipe = recipeWrapper;
		
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(0, true, 132 - xOff-1, 34 - yOff-1);
		guiItemStacks.init(1, true, 80 - xOff-1, 34 - yOff-1);
		guiItemStacks.setFromRecipe(0, recipeWrapper.getInputs());
		guiItemStacks.setFromRecipe(1, recipeWrapper.recipe.getOutput());
		
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
		guiFluidStacks.init(0, false, 33 - xOff-1, 24 - yOff-1, 12, 40, Fluid.BUCKET_VOLUME * 4, true, null);
		guiFluidStacks.set(0, recipeWrapper.getFluidInputs());
	}

}
