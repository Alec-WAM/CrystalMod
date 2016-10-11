package alec_wam.CrystalMod.tiles.cauldron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.BlockCrystalIngot.CrystalIngotBlockType;
import alec_wam.CrystalMod.fluids.Fluids;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ItemIngot.IngotType;
import alec_wam.CrystalMod.tiles.cauldron.TileEntityCrystalCauldron.LiquidCrystalColor;
import alec_wam.CrystalMod.tiles.machine.BasicMachineRecipe;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ModLogger;

import com.google.common.collect.Lists;

import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class CauldronRecipeManager {

	public static class InfusionRecipe {

		final Object input;
		final FluidStack finput;
	    final ItemStack output;
	    
	    public InfusionRecipe(Object input, FluidStack finput, ItemStack output)
	    {
	      this.input = input;
	      this.finput = finput;
	      this.output = output;
	    }
	    
	    public Object getInput()
	    {
	    	if(input instanceof ItemStack){
	    		return ((ItemStack)input).copy();
	    	}
	    	return this.input;
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
	    
	    public ItemStack getOutput()
	    {
	      return this.output.copy();
	    }
	    
	    public FluidStack getFluidInput()
	    {
	      return this.finput.copy();
	    }
		
	}


	private static List<InfusionRecipe> recipes = new ArrayList<InfusionRecipe>();

	
	public static InfusionRecipe addRecipe(Object input, FluidStack finput, ItemStack output){
		InfusionRecipe recipe = new InfusionRecipe(input, finput, output);
		return addRecipe(recipe);
	}
	
	public static InfusionRecipe addRecipe(InfusionRecipe recipe){
		recipes.add(recipe);
		return recipe;
	}
	
	public static boolean isInput(ItemStack input){
		for(InfusionRecipe recipe : recipes){
			boolean passInput = false;
			if(recipe.input instanceof String){
				passInput = ItemUtil.itemStackMatchesOredict(input, (String)recipe.getInput());
			}
			else if(recipe.input instanceof ItemStack){
				passInput = ItemUtil.canCombine(input, (ItemStack)recipe.getInput());
			}
			if(passInput){
				return true;
			}
		}
		return false;
	}
	
	public static InfusionRecipe getRecipe(ItemStack input, FluidStack cinput){
		if (input == null) {
			return null;
		}
		for(InfusionRecipe recipe : recipes){
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
		int bucket = 100;
		addRecipe("ingotIron", new FluidStack(Fluids.fluidDarkCrystal, 1 * bucket), new ItemStack(ModItems.ingots, 1, IngotType.DARK_IRON.getMetadata()));
		addRecipe("blockIron", new FluidStack(Fluids.fluidDarkCrystal, 9 * bucket), new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.DARKIRON.getMeta()));

		addRecipe("gemQuartz", new FluidStack(Fluids.fluidBlueCrystal, 6 * bucket), new ItemStack(ModItems.crystals, 1, CrystalType.BLUE.getMetadata()));
		addRecipe("gemQuartz", new FluidStack(Fluids.fluidRedCrystal, 6 * bucket), new ItemStack(ModItems.crystals, 1, CrystalType.RED.getMetadata()));
		addRecipe("gemQuartz", new FluidStack(Fluids.fluidGreenCrystal, 6 * bucket), new ItemStack(ModItems.crystals, 1, CrystalType.GREEN.getMetadata()));
		addRecipe("gemQuartz", new FluidStack(Fluids.fluidDarkCrystal, 6 * bucket), new ItemStack(ModItems.crystals, 1, CrystalType.DARK.getMetadata()));
		
		addRecipe(new ItemStack(Items.REEDS), new FluidStack(Fluids.fluidBlueCrystal, 3 * bucket), new ItemStack(ModItems.crystalReeds));
		addRecipe(new ItemStack(Items.REEDS), new FluidStack(Fluids.fluidRedCrystal, 3 * bucket), new ItemStack(ModItems.crystalReeds));
		addRecipe(new ItemStack(Items.REEDS), new FluidStack(Fluids.fluidGreenCrystal, 3 * bucket), new ItemStack(ModItems.crystalReeds));
		addRecipe(new ItemStack(Items.REEDS), new FluidStack(Fluids.fluidDarkCrystal, 3 * bucket), new ItemStack(ModItems.crystalReeds));
		addRecipe(new ItemStack(Items.REEDS), new FluidStack(Fluids.fluidPureCrystal, 1 * bucket), new ItemStack(ModItems.crystalReeds));

		addRecipe(new ItemStack(Items.NETHER_WART), new FluidStack(Fluids.fluidBlueCrystal, 6 * bucket), new ItemStack(ModItems.crystalSeedsBlue));
		addRecipe(new ItemStack(Items.NETHER_WART), new FluidStack(Fluids.fluidRedCrystal, 6 * bucket), new ItemStack(ModItems.crystalSeedsRed));
		addRecipe(new ItemStack(Items.NETHER_WART), new FluidStack(Fluids.fluidGreenCrystal, 6 * bucket), new ItemStack(ModItems.crystalSeedsGreen));
		addRecipe(new ItemStack(Items.NETHER_WART), new FluidStack(Fluids.fluidDarkCrystal, 6 * bucket), new ItemStack(ModItems.crystalSeedsDark));
	}

	public static List<InfusionRecipe> getRecipes() {
		return recipes;
	}
	
}
