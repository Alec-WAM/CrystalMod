package alec_wam.CrystalMod.tiles.cauldron;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.blocks.BlockCrystal.CrystalBlockType;
import alec_wam.CrystalMod.blocks.BlockCrystalIngot.CrystalIngotBlockType;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass.GlassType;
import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ItemIngot.IngotType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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
		addRecipe("nuggetIron", new FluidStack(ModFluids.fluidDarkCrystal, 10), new ItemStack(ModItems.crystals, 1, CrystalType.DIRON_NUGGET.getMetadata()));
		addRecipe("ingotIron", new FluidStack(ModFluids.fluidDarkCrystal, 90), new ItemStack(ModItems.ingots, 1, IngotType.DARK_IRON.getMetadata()));
		addRecipe("blockIron", new FluidStack(ModFluids.fluidDarkCrystal, 900), new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.DARKIRON.getMeta()));

		addRecipe("gemQuartz", new FluidStack(ModFluids.fluidBlueCrystal, 6 * bucket), new ItemStack(ModItems.crystals, 1, CrystalType.BLUE.getMetadata()));
		addRecipe("gemQuartz", new FluidStack(ModFluids.fluidRedCrystal, 6 * bucket), new ItemStack(ModItems.crystals, 1, CrystalType.RED.getMetadata()));
		addRecipe("gemQuartz", new FluidStack(ModFluids.fluidGreenCrystal, 6 * bucket), new ItemStack(ModItems.crystals, 1, CrystalType.GREEN.getMetadata()));
		addRecipe("gemQuartz", new FluidStack(ModFluids.fluidDarkCrystal, 6 * bucket), new ItemStack(ModItems.crystals, 1, CrystalType.DARK.getMetadata()));
		
		addRecipe(new ItemStack(ModBlocks.crystalGlass, 1, GlassType.BLUE.getMeta()), new FluidStack(ModFluids.fluidBlueCrystal, 10), new ItemStack(ModBlocks.crystalGlassTinted, 1, GlassType.BLUE.getMeta()));
		addRecipe(new ItemStack(ModBlocks.crystalGlass, 1, GlassType.RED.getMeta()), new FluidStack(ModFluids.fluidRedCrystal, 10), new ItemStack(ModBlocks.crystalGlassTinted, 1, GlassType.RED.getMeta()));
		addRecipe(new ItemStack(ModBlocks.crystalGlass, 1, GlassType.GREEN.getMeta()), new FluidStack(ModFluids.fluidGreenCrystal, 10), new ItemStack(ModBlocks.crystalGlassTinted, 1, GlassType.GREEN.getMeta()));
		addRecipe(new ItemStack(ModBlocks.crystalGlass, 1, GlassType.DARK.getMeta()), new FluidStack(ModFluids.fluidDarkCrystal, 10), new ItemStack(ModBlocks.crystalGlassTinted, 1, GlassType.DARK.getMeta()));
		addRecipe(new ItemStack(ModBlocks.crystalGlass, 1, GlassType.PURE.getMeta()), new FluidStack(ModFluids.fluidPureCrystal, 10), new ItemStack(ModBlocks.crystalGlassTinted, 1, GlassType.PURE.getMeta()));

		addRecipe(new ItemStack(ModBlocks.crystalGlassTinted, 1, GlassType.PURE.getMeta()), new FluidStack(ModFluids.fluidPureCrystal, 10), new ItemStack(ModBlocks.crystalGlassPainted, 1, GlassType.PURE.getMeta()));

		addRecipe(new ItemStack(Blocks.STONEBRICK), new FluidStack(ModFluids.fluidBlueCrystal, 100), new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.BLUE_BRICK.getMeta()));
		addRecipe(new ItemStack(Blocks.STONEBRICK), new FluidStack(ModFluids.fluidRedCrystal, 100), new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.RED_BRICK.getMeta()));
		addRecipe(new ItemStack(Blocks.STONEBRICK), new FluidStack(ModFluids.fluidGreenCrystal, 100), new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.GREEN_BRICK.getMeta()));
		addRecipe(new ItemStack(Blocks.STONEBRICK), new FluidStack(ModFluids.fluidDarkCrystal, 100), new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.DARK_BRICK.getMeta()));
		addRecipe(new ItemStack(Blocks.STONEBRICK), new FluidStack(ModFluids.fluidPureCrystal, 100), new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.PURE_BRICK.getMeta()));
		
		addRecipe(new ItemStack(Items.REEDS), new FluidStack(ModFluids.fluidBlueCrystal, 3 * bucket), new ItemStack(ModItems.crystalReedsBlue));
		addRecipe(new ItemStack(Items.REEDS), new FluidStack(ModFluids.fluidRedCrystal, 3 * bucket), new ItemStack(ModItems.crystalReedsRed));
		addRecipe(new ItemStack(Items.REEDS), new FluidStack(ModFluids.fluidGreenCrystal, 3 * bucket), new ItemStack(ModItems.crystalReedsGreen));
		addRecipe(new ItemStack(Items.REEDS), new FluidStack(ModFluids.fluidDarkCrystal, 3 * bucket), new ItemStack(ModItems.crystalReedsDark));

		addRecipe(new ItemStack(Items.NETHER_WART), new FluidStack(ModFluids.fluidBlueCrystal, 6 * bucket), new ItemStack(ModItems.crystalSeedsBlue));
		addRecipe(new ItemStack(Items.NETHER_WART), new FluidStack(ModFluids.fluidRedCrystal, 6 * bucket), new ItemStack(ModItems.crystalSeedsRed));
		addRecipe(new ItemStack(Items.NETHER_WART), new FluidStack(ModFluids.fluidGreenCrystal, 6 * bucket), new ItemStack(ModItems.crystalSeedsGreen));
		addRecipe(new ItemStack(Items.NETHER_WART), new FluidStack(ModFluids.fluidDarkCrystal, 6 * bucket), new ItemStack(ModItems.crystalSeedsDark));
		
		addRecipe(new ItemStack(Items.PAINTING), new FluidStack(ModFluids.fluidBlueCrystal, 100), new ItemStack(ModItems.crystalmodPainting));
		addRecipe(new ItemStack(Items.PAINTING), new FluidStack(ModFluids.fluidRedCrystal, 100), new ItemStack(ModItems.crystalmodPainting));
		addRecipe(new ItemStack(Items.PAINTING), new FluidStack(ModFluids.fluidGreenCrystal, 100), new ItemStack(ModItems.crystalmodPainting));
		addRecipe(new ItemStack(Items.PAINTING), new FluidStack(ModFluids.fluidDarkCrystal, 100), new ItemStack(ModItems.crystalmodPainting));
		
	}

	public static List<InfusionRecipe> getRecipes() {
		return recipes;
	}
	
}
