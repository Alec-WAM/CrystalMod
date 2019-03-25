package alec_wam.CrystalMod.integration.jei.machine;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.integration.jei.RecipeHandler;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCrystalMachine.MachineType;
import alec_wam.CrystalMod.tiles.machine.crafting.infuser.ContainerCrystalInfuser;
import alec_wam.CrystalMod.tiles.machine.crafting.infuser.CrystalInfusionManager;
import alec_wam.CrystalMod.tiles.machine.crafting.infuser.CrystalInfusionManager.InfusionMachineRecipe;
import alec_wam.CrystalMod.tiles.machine.crafting.infuser.GuiCrystalInfuser;
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
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class InfuserRecipeCategory extends BlankRecipeCategory<InfuserRecipeCategory.InfuserJEIRecipe>  {

	public static final @Nonnull String UID = CrystalMod.MODID+".Infuser";
	
	public static class InfuserJEIRecipe extends BlankRecipeWrapper {
		private final IJeiHelpers jeiHelpers;
		public InfusionMachineRecipe recipe;
		
		public InfuserJEIRecipe(IJeiHelpers jeiHelpers, InfusionMachineRecipe recipe){
			this.jeiHelpers = jeiHelpers;
			this.recipe = recipe;
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

		@Override
		public void getIngredients(IIngredients ingredients) {
			IStackHelper stackHelper = jeiHelpers.getStackHelper();

			List<List<ItemStack>> inputs = stackHelper.expandRecipeItemStackInputs(recipe.getInputs());
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

			ItemStack recipeOutput = recipe.getOutput();
			if (recipeOutput != null) {
				ingredients.setOutput(ItemStack.class, recipeOutput);
			}
		}
		
	}

	public static void register(IJeiHelpers jeiHelpers, IModRegistry registry, IGuiHelper guiHelper) {
	    
	    registry.addRecipeCategories(new InfuserRecipeCategory(guiHelper));
	    registry.addRecipeHandlers(new RecipeHandler<InfuserJEIRecipe>(InfuserJEIRecipe.class, InfuserRecipeCategory.UID));
	    registry.addRecipeClickArea(GuiCrystalInfuser.class, 103, 34, 24, 17, InfuserRecipeCategory.UID);
	    registry.addRecipeClickArea(GuiCrystalInfuser.class, 53, 34, 24, 17, InfuserRecipeCategory.UID);

	    registry.addRecipeCategoryCraftingItem(displayStack, InfuserRecipeCategory.UID);

	    List<InfuserJEIRecipe> result = new ArrayList<InfuserJEIRecipe>();    
	    for (InfusionMachineRecipe rec : CrystalInfusionManager.getRecipes()) {
	      result.add(new InfuserJEIRecipe(jeiHelpers, rec));
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
	  
	public InfuserRecipeCategory(IGuiHelper guiHelper) {
	    ResourceLocation backgroundLocation = new ResourceLocation("crystalmod", "textures/gui/machine/infuser.png");
	    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, 125, 50);

	    IDrawableStatic arrowDrawable = guiHelper.createDrawable(backgroundLocation, 176, 0, 24, 17);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
		IDrawableStatic arrowDrawable2 = guiHelper.createDrawable(backgroundLocation, 176, 17, 24, 34);
		this.arrow2 = guiHelper.createAnimatedDrawable(arrowDrawable2, 200, IDrawableAnimated.StartDirection.RIGHT, false);
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
	public void drawExtras(@Nonnull Minecraft minecraft) {
		arrow.draw(minecraft, 49-xOff, 33-yOff);
		arrow2.draw(minecraft, 103-xOff, 33-yOff);		
	}
	

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, InfuserJEIRecipe recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 132 - xOff-1, 34 - yOff-1);
		guiItemStacks.init(1, false, 80 - xOff-1, 34 - yOff-1);
		guiItemStacks.set(ingredients);
		
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
		guiFluidStacks.init(0, true, 33 - xOff-1, 24 - yOff-1, 12, 40, Fluid.BUCKET_VOLUME * 8, true, null);
		guiFluidStacks.set(ingredients);
	}

}
