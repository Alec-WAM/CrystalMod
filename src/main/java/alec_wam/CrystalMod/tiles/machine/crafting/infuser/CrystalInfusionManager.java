package alec_wam.CrystalMod.tiles.machine.crafting.infuser;

import java.util.ArrayList;
import java.util.List;

import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.ItemMachineFrame.FrameType;
import alec_wam.CrystalMod.tiles.cauldron.CauldronRecipeManager;
import alec_wam.CrystalMod.tiles.cauldron.CauldronRecipeManager.InfusionRecipe;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;

import com.google.common.collect.Lists;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class CrystalInfusionManager {

	public static class InfusionMachineRecipe {

		final Object input;
		final FluidStack finput;
	    final ItemStack output;
	    final int energy;
	    
	    public InfusionMachineRecipe(Object input, FluidStack finput, ItemStack output, int energy)
	    {
	      this.input = input;
	      this.finput = finput;
	      this.output = output;
	      this.energy = energy;
	    }
	    
	    public Object getInput()
	    {
	    	if(input instanceof ItemStack){
	    		return ((ItemStack)input).copy();
	    	}
	    	return this.input;
	    }
	    
	    public ItemStack getOutput()
	    {
	      return this.output.copy();
	    }
	    
	    public FluidStack getFluidInput()
	    {
	      return this.finput.copy();
	    }
	    
	    public int getEnergy(){
	    	return energy;
	    }

	    public List<ItemStack> getInputs()
	    {
	    	if(this.input instanceof String){
	    		return OreDictionary.getOres((String)input);
	    	}
	    	if(input instanceof ItemStack){
	    		return Lists.newArrayList((ItemStack)input);
	    	}
	    	return Lists.newArrayList();
	    }
		
	}


	private static List<InfusionMachineRecipe> recipes = new ArrayList<InfusionMachineRecipe>();

	public static boolean isInput(ItemStack input){
		return CauldronRecipeManager.isInput(input);
	}
	
	public static InfusionMachineRecipe getRecipe(ItemStack input, FluidStack cinput){
		if (ItemStackTools.isNullStack(input) || cinput == null) {
			return null;
		}
		for(InfusionMachineRecipe recipe : recipes){
			boolean passInput = false;
			if(recipe.input instanceof String){
				passInput = ItemUtil.itemStackMatchesOredict(input, (String)recipe.getInput());
			}
			else if(recipe.input instanceof ItemStack){
				passInput = ItemUtil.canCombine(input, (ItemStack)recipe.getInput());
			}
			if(passInput){
				if(recipe.getFluidInput().isFluidEqual(cinput) && recipe.getFluidInput().amount <=cinput.amount){
					return recipe;
				}
			}
		}
		
		return null;
	}

	public static void initRecipes() {
		for(InfusionRecipe recipe : CauldronRecipeManager.getRecipes()){
			recipes.add(new InfusionMachineRecipe(recipe.getInput(), recipe.getFluidInput(), recipe.getOutput().copy(), 1600));
		}
		
		recipes.add(new InfusionMachineRecipe(new ItemStack(ModItems.machineFrame, 1, FrameType.BASIC.getMetadata()), new FluidStack(ModFluids.fluidEnder, 1000), new ItemStack(ModItems.machineFrame, 1, FrameType.ENDER.getMetadata()), 1600));

		recipes.add(new InfusionMachineRecipe(new ItemStack(Blocks.COBBLESTONE), new FluidStack(FluidRegistry.WATER, 125), new ItemStack(Blocks.MOSSY_COBBLESTONE), 1600));
		recipes.add(new InfusionMachineRecipe(new ItemStack(Blocks.COBBLESTONE), new FluidStack(FluidRegistry.LAVA, 125), new ItemStack(Blocks.NETHERRACK), 1600));
		//Molten Block
		recipes.add(new InfusionMachineRecipe(new ItemStack(Blocks.NETHERRACK), new FluidStack(FluidRegistry.LAVA, 125), new ItemStack(Blocks.MAGMA), 1600));
		recipes.add(new InfusionMachineRecipe(new ItemStack(Blocks.NETHERRACK), new FluidStack(ModFluids.fluidEnder, 125), new ItemStack(Blocks.END_STONE), 1600));
	}
	
	public static void addRecipe(ItemStack stack, FluidStack fluidStack, ItemStack output, int power){
		recipes.add(new InfusionMachineRecipe(stack, fluidStack, output, power));
	}

	public static List<InfusionMachineRecipe> getRecipes() {
		return recipes;
	}
	
}
