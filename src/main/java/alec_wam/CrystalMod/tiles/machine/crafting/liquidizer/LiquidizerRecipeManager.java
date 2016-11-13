package alec_wam.CrystalMod.tiles.machine.crafting.liquidizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ItemIngot.IngotType;
import alec_wam.CrystalMod.items.ItemMetalPlate.PlateType;
import alec_wam.CrystalMod.tiles.machine.BasicMachineRecipe;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class LiquidizerRecipeManager {

	public static class LiquidizerRecipe
	{
	    final ItemStack input;
	    final FluidStack output;
	    final int energy;
	    
	    public LiquidizerRecipe(ItemStack input, FluidStack output, int energy)
	    {
	      this.input = input;
	      this.output = output;
	      this.energy = energy;
	    }
	    
	    public ItemStack getInput()
	    {
	      return this.input.copy();
	    }
	    
	    public boolean matchesInput(ItemStack stack){
	    	return ItemUtil.canCombine(stack, input);
	    }
	    
	    public FluidStack getOutput()
	    {
	      return this.output.copy();
	    }
	    
	    public int getEnergy()
	    {
	      return this.energy;
	    }
	}

	
	private static List<LiquidizerRecipe> recipes = new ArrayList<LiquidizerRecipe>();
	
	public static LiquidizerRecipe getRecipe(ItemStack input){
		if (input == null) {
			return null;
		}
		
		for(LiquidizerRecipe recipe : recipes){
			if(recipe.matchesInput(input) && recipe.getInput().stackSize <=input.stackSize){
				return recipe;
			}
		}
		return null;
	}
	
	public static List<LiquidizerRecipe> getRecipes(){
		return recipes;
	}
	
	public static LiquidizerRecipe addRecipe(ItemStack input, FluidStack output, int energy){
		LiquidizerRecipe recipe = new LiquidizerRecipe(input, output, energy);
		return addRecipe(recipe);
	}
	
	public static LiquidizerRecipe addRecipe(LiquidizerRecipe recipe){
		recipes.add(recipe);
		return recipe;
	}
	
	public static void initRecipes(){
		final int defaultPower = 1600;
		int shardValue = 100;
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.BLUE_SHARD.getMetadata()), new FluidStack(ModFluids.fluidBlueCrystal, shardValue), defaultPower);
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.RED_SHARD.getMetadata()), new FluidStack(ModFluids.fluidRedCrystal, shardValue), defaultPower);
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.GREEN_SHARD.getMetadata()), new FluidStack(ModFluids.fluidGreenCrystal, shardValue), defaultPower);
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.DARK_SHARD.getMetadata()), new FluidStack(ModFluids.fluidDarkCrystal, shardValue), defaultPower);
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.PURE_SHARD.getMetadata()), new FluidStack(ModFluids.fluidPureCrystal, shardValue), defaultPower);
		
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.BLUE_NUGGET.getMetadata()), new FluidStack(ModFluids.fluidBlueCrystal, shardValue), defaultPower);
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.RED_NUGGET.getMetadata()), new FluidStack(ModFluids.fluidRedCrystal, shardValue), defaultPower);
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.GREEN_NUGGET.getMetadata()), new FluidStack(ModFluids.fluidGreenCrystal, shardValue), defaultPower);
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.DARK_NUGGET.getMetadata()), new FluidStack(ModFluids.fluidDarkCrystal, shardValue), defaultPower);
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.PURE_NUGGET.getMetadata()), new FluidStack(ModFluids.fluidPureCrystal, shardValue), defaultPower);
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.DIRON_NUGGET.getMetadata()), new FluidStack(ModFluids.fluidDarkIron, shardValue), defaultPower);

		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.BLUE.getMetadata()), new FluidStack(ModFluids.fluidBlueCrystal, shardValue*9), defaultPower);
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.RED.getMetadata()), new FluidStack(ModFluids.fluidRedCrystal, shardValue*9), defaultPower);
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.GREEN.getMetadata()), new FluidStack(ModFluids.fluidGreenCrystal, shardValue*9), defaultPower);
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.DARK.getMetadata()), new FluidStack(ModFluids.fluidDarkCrystal, shardValue*9), defaultPower);
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.PURE.getMetadata()), new FluidStack(ModFluids.fluidPureCrystal, shardValue*9), defaultPower);
		
		addRecipe(new ItemStack(ModItems.ingots, 1, IngotType.BLUE.getMetadata()), new FluidStack(ModFluids.fluidBlueCrystal, shardValue*9), defaultPower);
		addRecipe(new ItemStack(ModItems.ingots, 1, IngotType.RED.getMetadata()), new FluidStack(ModFluids.fluidRedCrystal, shardValue*9), defaultPower);
		addRecipe(new ItemStack(ModItems.ingots, 1, IngotType.GREEN.getMetadata()), new FluidStack(ModFluids.fluidGreenCrystal, shardValue*9), defaultPower);
		addRecipe(new ItemStack(ModItems.ingots, 1, IngotType.DARK.getMetadata()), new FluidStack(ModFluids.fluidDarkCrystal, shardValue*9), defaultPower);
		addRecipe(new ItemStack(ModItems.ingots, 1, IngotType.PURE.getMetadata()), new FluidStack(ModFluids.fluidPureCrystal, shardValue*9), defaultPower);
		addRecipe(new ItemStack(ModItems.ingots, 1, IngotType.DARK_IRON.getMetadata()), new FluidStack(ModFluids.fluidDarkIron, shardValue*9), defaultPower);
	}
	
}
