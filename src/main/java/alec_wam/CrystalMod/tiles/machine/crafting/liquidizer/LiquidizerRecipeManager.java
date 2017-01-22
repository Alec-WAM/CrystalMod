package alec_wam.CrystalMod.tiles.machine.crafting.liquidizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ItemIngot.IngotType;
import alec_wam.CrystalMod.items.ItemMetalPlate.PlateType;
import alec_wam.CrystalMod.tiles.machine.BasicMachineRecipe;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class LiquidizerRecipeManager {

	public static class LiquidizerRecipe
	{
	    final Object input;
	    final int inputSize;
	    final FluidStack output;
	    final int energy;
	    
	    public LiquidizerRecipe(Object input, int size, FluidStack output, int energy)
	    {
	      this.input = input;
	      this.inputSize = size;
	      this.output = output;
	      this.energy = energy;
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
	    
	    public boolean matchesInput(ItemStack stack){
	    	if(this.input instanceof String){
	    		return ItemUtil.itemStackMatchesOredict(stack, (String)input);
	    	}
	    	if(input instanceof ItemStack){
	    		return ItemUtil.canCombine(stack, (ItemStack)input);
	    	}
	    	return false;
	    }
	    
	    public FluidStack getOutput()
	    {
	      return this.output.copy();
	    }
	    
	    public int getEnergy()
	    {
	      return this.energy;
	    }

		public int getInputSize() {
			return inputSize;
		}
	}

	
	private static List<LiquidizerRecipe> recipes = new ArrayList<LiquidizerRecipe>();
	
	public static LiquidizerRecipe getRecipe(ItemStack input){
		if (ItemStackTools.isNullStack(input)) {
			return null;
		}
		
		for(LiquidizerRecipe recipe : recipes){
			if(recipe.matchesInput(input) && recipe.getInputSize() <=ItemStackTools.getStackSize(input)){
				return recipe;
			}
		}
		return null;
	}
	
	public static List<LiquidizerRecipe> getRecipes(){
		return recipes;
	}
	
	public static LiquidizerRecipe addRecipe(String input, int size, FluidStack output, int energy){
		LiquidizerRecipe recipe = new LiquidizerRecipe(input, size, output, energy);
		return addRecipe(recipe);
	}
	
	public static LiquidizerRecipe addRecipe(ItemStack input, FluidStack output, int energy){
		LiquidizerRecipe recipe = new LiquidizerRecipe(input, ItemStackTools.getStackSize(input), output, energy);
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
		
		addRecipe("enderpearl", 1, new FluidStack(ModFluids.fluidEnder, 200), defaultPower);

		addRecipe(new ItemStack(Blocks.ICE), new FluidStack(FluidRegistry.WATER, 1000), defaultPower);
		addRecipe(new ItemStack(Blocks.PACKED_ICE), new FluidStack(FluidRegistry.WATER, 1000), defaultPower);
		addRecipe(new ItemStack(Blocks.SNOW), new FluidStack(FluidRegistry.WATER, 1000), defaultPower);
		addRecipe(new ItemStack(Items.SNOWBALL), new FluidStack(FluidRegistry.WATER, 250), 800);
		addRecipe("treeLeaves", 1, new FluidStack(FluidRegistry.WATER, 125), 800);
	}
	
}
