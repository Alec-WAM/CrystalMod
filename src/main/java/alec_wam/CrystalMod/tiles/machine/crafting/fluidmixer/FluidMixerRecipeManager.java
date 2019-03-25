package alec_wam.CrystalMod.tiles.machine.crafting.fluidmixer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.BlockStone;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidMixerRecipeManager {

	public static class FluidMixRecipe {

		final FluidStack inputLeft;
		final int leftConsumption;
		final FluidStack inputRight;
		final int rightConsumption;
	    final ItemStack output;
	    final int energy;
	    
	    public FluidMixRecipe(FluidStack inputLeft, int leftConsumption, FluidStack inputRight, int rightConsumption, ItemStack output, int energy)
	    {
	      this.inputLeft = inputLeft;
	      this.leftConsumption = leftConsumption;
	      this.inputRight = inputRight;
	      this.rightConsumption = rightConsumption;
	      this.output = output;
	      this.energy = energy;
	    }
	    
	    public FluidStack getLeftFluidInput()
	    {
	      return inputLeft == null ? null : this.inputLeft.copy();
	    }
	    
	    public int getLeftConsumption(){
	    	return leftConsumption;
	    }
	    
	    public FluidStack getRightFluidInput()
	    {
	      return inputRight == null ? null : this.inputRight.copy();
	    }
	    
	    public int getRightConsumption(){
	    	return rightConsumption;
	    }
	    
	    public ItemStack getOutput()
	    {
	      return this.output.copy();
	    }
	    
	    public int getEnergy(){
	    	return energy;
	    }

	    public List<FluidStack> getInputs()
	    {
	    	List<FluidStack> list = Lists.newArrayList();
	    	list.add(inputLeft);
	    	list.add(inputRight);
	    	return list;
	    }
		
	}

	//Keeps order
	private static LinkedHashMap<String, FluidMixRecipe> recipes = new LinkedHashMap<String, FluidMixRecipe>();

	public static FluidMixRecipe getRecipe(FluidStack inputLeft, FluidStack inputRight){
		if (inputLeft == null || inputRight == null) {
			return null;
		}
		for(FluidMixRecipe recipe : recipes.values()){
			FluidStack leftRecipe = recipe.getLeftFluidInput();
			FluidStack rightRecipe = recipe.getRightFluidInput();
			if(leftRecipe.isFluidEqual(inputLeft) && leftRecipe.amount <= inputLeft.amount){
				if(rightRecipe.isFluidEqual(inputRight) && rightRecipe.amount <= inputRight.amount){
					return recipe;
				}
			}
		}
		
		return null;
	}
	
	public static FluidMixRecipe getRecipe(String name){
		return recipes.get(name);
	}
	
	public static String getNextRecipe(String current, boolean rev){
		ArrayList<String> list = new ArrayList<String>(recipes.keySet());
		int index = list.indexOf(current);
		if(index > -1){
			if(rev){
				int newIndex = index-1;
				if(newIndex < 0){
					newIndex = list.size()-1;
				}
				return list.get(newIndex);
			} else {
				int newIndex = index+1;
				if(newIndex >= list.size()){
					newIndex = 0;
				}
				return list.get(newIndex);
			}
		}
		return "cobblestone";
	}

	public static void initRecipes() {
		FluidStack waterBucket = new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
		FluidStack lavaBucket = new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME);
		addRecipe("cobblestone", lavaBucket, waterBucket, 0, 0, new ItemStack(Blocks.COBBLESTONE), 400);
		addRecipe("stone", lavaBucket, waterBucket, 0, 0, new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.STONE.getMetadata()), 600);
		addRecipe("granite", lavaBucket, waterBucket, 0, 0, new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.GRANITE.getMetadata()), 400);
		addRecipe("granite_smooth", lavaBucket, waterBucket, 0, 0, new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.GRANITE_SMOOTH.getMetadata()), 600);
		addRecipe("diorite", lavaBucket, waterBucket, 0, 0, new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.DIORITE.getMetadata()), 400);
		addRecipe("diorite_smooth", lavaBucket, waterBucket, 0, 0, new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.DIORITE_SMOOTH.getMetadata()), 600);
		addRecipe("andesite", lavaBucket, waterBucket, 0, 0, new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.ANDESITE.getMetadata()), 400);
		addRecipe("andesite_smooth", lavaBucket, waterBucket, 0, 0, new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.ANDESITE_SMOOTH.getMetadata()), 600);
		addRecipe("obsidian", lavaBucket, waterBucket, Fluid.BUCKET_VOLUME, 0, new ItemStack(Blocks.OBSIDIAN), 1600);
	}
	
	public static void addRecipe(String name, FluidStack fluidStackLeft, FluidStack fluidStackRight, int leftC, int rightC, ItemStack output, int power){
		recipes.put(name, new FluidMixRecipe(fluidStackLeft, leftC, fluidStackRight, rightC, output, power));
	}

	public static Collection<FluidMixRecipe> getRecipes() {
		return recipes.values();
	}
	
}
