package alec_wam.CrystalMod.tiles.machine.crafting.liquidizer;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.blocks.BlockSpecialCrystalLog;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ItemIngot.IngotType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.CrystalColors;
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
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.BLUE_SHARD.getMeta()), new FluidStack(ModFluids.fluidBlueCrystal, shardValue), 800);
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.RED_SHARD.getMeta()), new FluidStack(ModFluids.fluidRedCrystal, shardValue), 800);
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.GREEN_SHARD.getMeta()), new FluidStack(ModFluids.fluidGreenCrystal, shardValue), 800);
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.DARK_SHARD.getMeta()), new FluidStack(ModFluids.fluidDarkCrystal, shardValue), 800);
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.PURE_SHARD.getMeta()), new FluidStack(ModFluids.fluidPureCrystal, shardValue), 800);
		
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.BLUE.getMeta()), new FluidStack(ModFluids.fluidBlueCrystal, shardValue*9), defaultPower);
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.RED.getMeta()), new FluidStack(ModFluids.fluidRedCrystal, shardValue*9), defaultPower);
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.GREEN.getMeta()), new FluidStack(ModFluids.fluidGreenCrystal, shardValue*9), defaultPower);
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.DARK.getMeta()), new FluidStack(ModFluids.fluidDarkCrystal, shardValue*9), defaultPower);
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.PURE.getMeta()), new FluidStack(ModFluids.fluidPureCrystal, shardValue*9), defaultPower);
		
		addRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.DIRON_NUGGET.getMeta()), new FluidStack(ModFluids.fluidDarkIron, shardValue), defaultPower);
		addRecipe(new ItemStack(ModItems.ingots, 1, IngotType.DARK_IRON.getMeta()), new FluidStack(ModFluids.fluidDarkIron, shardValue*9), defaultPower);
		
		addRecipe(new ItemStack(ModBlocks.crystalLeaves, 1, CrystalColors.Basic.BLUE.getMeta()), new FluidStack(ModFluids.fluidBlueCrystal, 125), 800);
		addRecipe(new ItemStack(ModBlocks.crystalLeaves, 1, CrystalColors.Basic.RED.getMeta()), new FluidStack(ModFluids.fluidRedCrystal, 125), 800);
		addRecipe(new ItemStack(ModBlocks.crystalLeaves, 1, CrystalColors.Basic.GREEN.getMeta()), new FluidStack(ModFluids.fluidGreenCrystal, 125), 800);
		addRecipe(new ItemStack(ModBlocks.crystalLeaves, 1, CrystalColors.Basic.DARK.getMeta()), new FluidStack(ModFluids.fluidDarkCrystal, 125), 800);
		addRecipe(new ItemStack(ModBlocks.crystalSpecialLeaves, 1, BlockSpecialCrystalLog.SpecialCrystalLog.PURE.getMeta()), new FluidStack(ModFluids.fluidPureCrystal, 100), 800);
		
		addRecipe("enderpearl", 1, new FluidStack(ModFluids.fluidEnder, 200), defaultPower);

		addRecipe(new ItemStack(Blocks.ICE), new FluidStack(FluidRegistry.WATER, 1000), defaultPower);
		addRecipe(new ItemStack(Blocks.PACKED_ICE), new FluidStack(FluidRegistry.WATER, 1000), defaultPower);
		addRecipe(new ItemStack(Blocks.SNOW), new FluidStack(FluidRegistry.WATER, 1000), defaultPower);
		addRecipe(new ItemStack(Items.SNOWBALL), new FluidStack(FluidRegistry.WATER, 250), 800);
		addRecipe("treeLeaves", 1, new FluidStack(FluidRegistry.WATER, 125), 800);
	}
	
}
