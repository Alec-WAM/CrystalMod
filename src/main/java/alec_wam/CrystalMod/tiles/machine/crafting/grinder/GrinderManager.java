package alec_wam.CrystalMod.tiles.machine.crafting.grinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.StringUtils;
import net.minecraft.block.BlockDoublePlant.EnumPlantType;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class GrinderManager {

	private static List<GrinderRecipe> recipes = new ArrayList<GrinderRecipe>();
	private static List<OreSearch> searches = new ArrayList<OreSearch>();
	
	public static class GrinderRecipe
	{
	    final Object input;
	    final int inputSize;
	    @Nonnull
	    final ItemStack output;
	    @Nonnull
	    final ItemStack output2;
	    final int secondChance;
	    final int energy;
	    
	    public GrinderRecipe(ItemStack input, @Nonnull ItemStack output, @Nonnull ItemStack output2, int chance, int energy)
	    {
	    	this.input = input;
	        this.inputSize = ItemStackTools.getStackSize(input);
	        this.output = output;
	        this.output2 = output2;
	        this.secondChance = chance;
	        this.energy = energy;
	    }
	    
	    public GrinderRecipe(String input, int size, @Nonnull ItemStack output, @Nullable ItemStack output2, int chance, int energy)
	    {
	      this.input = input;
	      this.inputSize = size;
	      this.output = output;
	      this.output2 = output2;
	      this.secondChance = chance;
	      this.energy = energy;
	    }
	    
	    public Object getInput()
	    {
	    	return input;
	    }
	    
	    public int getInputSize(){
	    	return inputSize;
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
	    
	    public ItemStack getMainOutput()
	    {
	      return ItemStackTools.safeCopy(output);
	    }
	    
	    public ItemStack getSecondaryOutput()
	    {
	      return ItemStackTools.safeCopy(output2);
	    }
	    
	    public int getSecondaryChance(){
	    	return secondChance;
	    }
	    
	    public int getEnergy()
	    {
	      return this.energy;
	    }
	}

	
	public static GrinderRecipe getRecipe(ItemStack input){
		if (ItemStackTools.isNullStack(input)) {
			return null;
		}
		for(GrinderRecipe recipe : recipes){
			boolean passInput = false;
			if(recipe.input instanceof String){
				passInput = ItemUtil.itemStackMatchesOredict(input, (String)recipe.getInput());
			}
			else if(recipe.input instanceof ItemStack){
				passInput = ItemUtil.canCombine(input, (ItemStack)recipe.getInput());
			}
			if(passInput){
				return recipe;
			}
		}
		return null;
	}


	public static void initRecipes() {
		addRecipeBothList(OreDictionary.getOres("cobblestone", false), 1, OreDictionary.getOres("gravel", false), 1, OreDictionary.getOres("sand", false), 1, 20, 1600);
		addRecipeBothList(OreDictionary.getOres("gravel", false), 1, OreDictionary.getOres("sand", false), 1, Collections.singletonList(new ItemStack(Items.FLINT)), 1, 40, 1600);
		addRecipeBothList(OreDictionary.getOres("sand", false), 1, OreDictionary.getOres("itemSilicon", false), 1, null, 0, 0, 800);
		addRecipeListSecondOutput(new ItemStack(Items.BLAZE_ROD), new ItemStack(Items.BLAZE_POWDER, 4), OreDictionary.getOres("dustSulfer", false), 1, 50, 1600);
		addRecipe(new ItemStack(Items.BONE), new ItemStack(Items.DYE, 6, EnumDyeColor.WHITE.getDyeDamage()), ItemStackTools.getEmptyStack(), 0, 1600);
		addRecipe(new ItemStack(Items.REEDS), new ItemStack(Items.SUGAR, 3), ItemStackTools.getEmptyStack(), 0, 800);
		for(EnumDyeColor dye : EnumDyeColor.values()){
			addRecipe(new ItemStack(Blocks.WOOL, 1, dye.getMetadata()), new ItemStack(Items.STRING, 3), new ItemStack(Items.DYE, 1, dye.getDyeDamage()), 20, 1600);
		}
		int powerDye = 800;
		addRecipe(new ItemStack(Blocks.YELLOW_FLOWER), new ItemStack(Items.DYE, 3, EnumDyeColor.YELLOW.getDyeDamage()), ItemStackTools.getEmptyStack(), 0, powerDye);
		addRecipe(new ItemStack(Blocks.RED_FLOWER, 1, EnumFlowerType.POPPY.getMeta()), new ItemStack(Items.DYE, 3, EnumDyeColor.RED.getDyeDamage()), ItemStackTools.getEmptyStack(), 0, powerDye);
		addRecipe(new ItemStack(Blocks.RED_FLOWER, 1, EnumFlowerType.BLUE_ORCHID.getMeta()), new ItemStack(Items.DYE, 3, EnumDyeColor.LIGHT_BLUE.getDyeDamage()), ItemStackTools.getEmptyStack(), 0, powerDye);
		addRecipe(new ItemStack(Blocks.RED_FLOWER, 1, EnumFlowerType.ALLIUM.getMeta()), new ItemStack(Items.DYE, 3, EnumDyeColor.MAGENTA.getDyeDamage()), ItemStackTools.getEmptyStack(), 0, powerDye);
		addRecipe(new ItemStack(Blocks.RED_FLOWER, 1, EnumFlowerType.HOUSTONIA.getMeta()), new ItemStack(Items.DYE, 3, EnumDyeColor.SILVER.getDyeDamage()), ItemStackTools.getEmptyStack(), 0, powerDye);
		addRecipe(new ItemStack(Blocks.RED_FLOWER, 1, EnumFlowerType.RED_TULIP.getMeta()), new ItemStack(Items.DYE, 3, EnumDyeColor.RED.getDyeDamage()), ItemStackTools.getEmptyStack(), 0, powerDye);
		addRecipe(new ItemStack(Blocks.RED_FLOWER, 1, EnumFlowerType.ORANGE_TULIP.getMeta()), new ItemStack(Items.DYE, 3, EnumDyeColor.ORANGE.getDyeDamage()), ItemStackTools.getEmptyStack(), 0, powerDye);
		addRecipe(new ItemStack(Blocks.RED_FLOWER, 1, EnumFlowerType.WHITE_TULIP.getMeta()), new ItemStack(Items.DYE, 3, EnumDyeColor.SILVER.getDyeDamage()), ItemStackTools.getEmptyStack(), 0, powerDye);
		addRecipe(new ItemStack(Blocks.RED_FLOWER, 1, EnumFlowerType.PINK_TULIP.getMeta()), new ItemStack(Items.DYE, 3, EnumDyeColor.PINK.getDyeDamage()), ItemStackTools.getEmptyStack(), 0, powerDye);
		addRecipe(new ItemStack(Blocks.RED_FLOWER, 1, EnumFlowerType.OXEYE_DAISY.getMeta()), new ItemStack(Items.DYE, 3, EnumDyeColor.SILVER.getDyeDamage()), ItemStackTools.getEmptyStack(), 0, powerDye);
		addRecipe(new ItemStack(Blocks.DOUBLE_PLANT, 1, EnumPlantType.SUNFLOWER.getMeta()), new ItemStack(Items.DYE, 4, EnumDyeColor.YELLOW.getDyeDamage()), ItemStackTools.getEmptyStack(), 0, 1600);
		addRecipe(new ItemStack(Blocks.DOUBLE_PLANT, 1, EnumPlantType.SYRINGA.getMeta()), new ItemStack(Items.DYE, 4, EnumDyeColor.MAGENTA.getDyeDamage()), ItemStackTools.getEmptyStack(), 0, 1600);
		addRecipe(new ItemStack(Blocks.DOUBLE_PLANT, 1, EnumPlantType.ROSE.getMeta()), new ItemStack(Items.DYE, 4, EnumDyeColor.RED.getDyeDamage()), ItemStackTools.getEmptyStack(), 0, 1600);
		addRecipe(new ItemStack(Blocks.DOUBLE_PLANT, 1, EnumPlantType.PAEONIA.getMeta()), new ItemStack(Items.DYE, 4, EnumDyeColor.PINK.getDyeDamage()), ItemStackTools.getEmptyStack(), 0, 1600);
		addRecipe(new ItemStack(Items.BEETROOT), new ItemStack(Items.DYE, 2, EnumDyeColor.RED.getDyeDamage()), new ItemStack(Items.BEETROOT_SEEDS), 30, powerDye);

		addRecipeBothList(OreDictionary.getOres("oreRedstone", false), 1, OreDictionary.getOres("dustRedstone", false), 8, null, 0, 0, 3200);
		addRecipeBothList(OreDictionary.getOres("coal", false), 1, OreDictionary.getOres("dustCoal", false), 1, null, 0, 0, 2400);
		addRecipeBothList(OreDictionary.getOres("oreCoal", false), 1, Collections.singletonList(new ItemStack(Items.COAL)), 3, OreDictionary.getOres("dustCoal", false), 1, 60, 3200);
		addRecipeBothList(OreDictionary.getOres("oreLapis", false), 1, OreDictionary.getOres("gemLapis", false), 8, null, 0, 0, 3200);
		addRecipeListInput(OreDictionary.getOres("oreDiamond", false), 1, new ItemStack(Items.DIAMOND), new ItemStack(Items.DIAMOND), 25, 3200);
		addRecipeListInput(OreDictionary.getOres("oreEmerald", false), 1, new ItemStack(Items.EMERALD), new ItemStack(Items.EMERALD), 25, 3200);
		addRecipeBothList(OreDictionary.getOres("oreQuartz", false), 1, OreDictionary.getOres("gemQuartz", false), 3, OreDictionary.getOres("dustNetherQuartz", false), 1, 10, 3200);
		addRecipeListOutput(new ItemStack(Items.QUARTZ), OreDictionary.getOres("dustNetherQuartz", false), 1, ItemStackTools.getEmptyStack(), 0, 1600);

		addRecipe(new ItemStack(Blocks.GLOWSTONE), new ItemStack(Items.GLOWSTONE_DUST, 4), ItemStackTools.getEmptyStack(), 0, 1600);
		List<ItemStack> flourList = Lists.newArrayList();
		flourList.addAll(OreDictionary.getOres("dustWheat", false));
		flourList.addAll(OreDictionary.getOres("dustFlour", false));
		addRecipeListOutput(new ItemStack(Items.WHEAT), flourList, 1, new ItemStack(Items.WHEAT_SEEDS), 30, 800);
		addRecipeListOutput(new ItemStack(Items.ENDER_PEARL), OreDictionary.getOres("nuggetEnderpearl", false), 9, ItemStackTools.getEmptyStack(), 0, 1600);
		addRecipeListOutput(new ItemStack(Blocks.OBSIDIAN), OreDictionary.getOres("dustObsidian", false), 4, ItemStackTools.getEmptyStack(), 0, 4000);

		searches.add(new OreSearch("gem", 1, 2400));
		searches.add(new OreSearch("ingot", 1, 2400));
		searches.add(new OreSearch("ore", 2, 3200));
		searches.add(new OreSearch("oreNether", 4, 3200));
		searches.add(new OreSearch("denseore", 4, 2400, "ore"));
		searches.add(new OreSearch("ore", 2, 3200, "gem"));
	}
	
	public static void oreSearch(){
		final int sizePreSearch = recipes.size();
		List<String> failedSearches = Lists.newArrayList();
		for(String oreName : OreDictionary.getOreNames()){
			for(OreSearch search : searches){
				if(oreName.length() > search.type.length()){
					if(oreName.substring(0, search.type.length()).equals(search.type)){
						String fullName = search.start+oreName.substring(search.type.length());
						if(!addRecipeBothList(OreDictionary.getOres(oreName, false), 1, OreDictionary.getOres(fullName, false), search.amount, null, 0, 0, search.power)){
							if(!failedSearches.contains(oreName)){
								failedSearches.add(oreName);
							}
						}
					}
				}
			}
		}
		List<String> addedInfo = Lists.newArrayList();
		for(int r = sizePreSearch; r < recipes.size(); r++){
			GrinderRecipe recipe = recipes.get(r);
			addedInfo.add(recipe.getInput()+" > "+recipe.getMainOutput());
		}
		ModLogger.info("Found and added "+addedInfo.size()+" Grinder recipes: "+StringUtils.makeReadable(addedInfo));
		if(failedSearches.size() > 0)ModLogger.warning("Failed "+failedSearches.size()+" Grinder Recipes: "+failedSearches);
	}
	
	public static boolean addRecipeListInput(List<ItemStack> inputs, int inputAmount, ItemStack output, @Nullable ItemStack secondOutput, int chance, int power){
		for(ItemStack input : inputs){
			if(ItemStackTools.isValid(input) && getRecipe(input) == null){
				if(ItemStackTools.isValid(output)){
					ItemStack inputCopy = ItemUtil.copy(input, inputAmount);
					if(addRecipe(inputCopy, output, secondOutput, chance, power)){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean addRecipeListOutput(ItemStack input, List<ItemStack> outputs, int outputAmount, @Nullable ItemStack secondOutput, int chance, int power){
		if(ItemStackTools.isValid(input) && getRecipe(input) == null){
			for(ItemStack output : outputs){
				if(ItemStackTools.isValid(output)){
					ItemStack outputCopy = ItemUtil.copy(output, outputAmount);
					if(addRecipe(input, outputCopy, secondOutput, chance, power)){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean addRecipeListSecondOutput(ItemStack input, ItemStack output, List<ItemStack> secondOutputs, int secondOutputAmount, int chance, int power){
		if(ItemStackTools.isValid(input) && getRecipe(input) == null){
			if(ItemStackTools.isValid(output)){
				if(secondOutputs == null || secondOutputs.isEmpty()){
					if(addRecipe(input, output, ItemStackTools.getEmptyStack(), 0, power)){
						return true;
					}
				} else {
					for(ItemStack secondOutput : secondOutputs){
						if(ItemStackTools.isValid(secondOutput)){
							ItemStack secondOutputCopy = ItemUtil.copy(secondOutput, secondOutputAmount);
							if(addRecipe(input, output, secondOutputCopy, chance, power)){
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	public static boolean addRecipe(ItemStack input, ItemStack output, @Nullable ItemStack secondOutput, int chance, int power){
		if(ItemStackTools.isValid(input) && getRecipe(input) == null){
			if(ItemStackTools.isValid(output)){
				recipes.add(new GrinderRecipe(input, output, secondOutput, chance, power));
				return true;
			}
		}
		return false;
	}
	
	public static boolean addRecipeBothList(List<ItemStack> inputs, int inputAmount, List<ItemStack> outputs, int outputAmount, List<ItemStack> secondOutputs, int secondOutputAmount, int chance, int power){
		for(ItemStack input : inputs){
			if(ItemStackTools.isValid(input) && getRecipe(input) == null){
				ItemStack inputCopy = ItemUtil.copy(input, inputAmount);
				for(ItemStack output : outputs){
					if(ItemStackTools.isValid(output)){
						ItemStack outputCopy = ItemUtil.copy(output, outputAmount);
						if(secondOutputs == null || secondOutputs.isEmpty()){
							if(addRecipe(inputCopy, outputCopy, ItemStackTools.getEmptyStack(), 0, power)){
								return true;
							}
						} else {
							for(ItemStack secondOutput : secondOutputs){
								if(ItemStackTools.isValid(secondOutput)){
									ItemStack secondOutputCopy = ItemUtil.copy(secondOutput, secondOutputAmount);
									if(addRecipe(inputCopy, outputCopy, secondOutputCopy, chance, power)){
										return true;
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	public static List<GrinderRecipe> getRecipes(){
		return recipes;
	}
	
	public static class OreSearch{

        final String type;
        final int amount;
        final String start;
        final int power;

        public OreSearch(String type, int amount, int power){
            this(type, amount, power, "dust");
        }

        public OreSearch(String type, int amount, int power, String start){
            this.type = type;
            this.amount = amount;
            this.power = power;
            this.start = start;
        }
    }
}
