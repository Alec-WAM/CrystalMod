package alec_wam.CrystalMod.tiles.machine.crafting.furnace;

import java.util.HashMap;
import java.util.Map;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.crystexium.CrystexiumBlock.CrystexiumBlockType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.crystex.ItemCrystex.CrystexItemType;
import alec_wam.CrystalMod.tiles.machine.BasicMachineRecipe;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class CrystalFurnaceManager {

	private static Map<ItemStack, BasicMachineRecipe> recipeMap = new HashMap<ItemStack, BasicMachineRecipe>();
	
	
	public static BasicMachineRecipe getRecipe(ItemStack input){
		if (ItemStackTools.isNullStack(input)) {
			return null;
		}
		for(ItemStack stack : recipeMap.keySet()){
			if(ItemUtil.stackMatchUseOre(input, stack)){
				return recipeMap.get(stack);
			}
		}
		if(ItemStackTools.isValid(FurnaceRecipes.instance().getSmeltingResult(input))){
			ItemStack output = FurnaceRecipes.instance().getSmeltingResult(input).copy();
	    	int i = 1600;
	    	if ((output.getItem() instanceof ItemFood)) {
	    		i /= 2;
	    	}
	    	if ((input.getItem() instanceof ItemFood)) {
	    		i /= 2;
	    	}
	    	if ((ItemUtil.isDust(output)) && (ItemUtil.isIngot(output))) {
	    		i = 1000;
	    	}
	    	return new BasicMachineRecipe(input, output, i);
		}
		return null;
	}


	public static void initRecipes() {		
		ItemStack crystexiumBlock = new ItemStack(ModBlocks.crystexiumBlock, 1, CrystexiumBlockType.NORMAL.getMeta());
		ItemStack crystexiumBlockBlue = new ItemStack(ModBlocks.blueCrystexiumBlock, 1, CrystexiumBlockType.NORMAL.getMeta());
		ItemStack crystexiumBlockRed = new ItemStack(ModBlocks.redCrystexiumBlock, 1, CrystexiumBlockType.NORMAL.getMeta());
		ItemStack crystexiumBlockGreen = new ItemStack(ModBlocks.greenCrystexiumBlock, 1, CrystexiumBlockType.NORMAL.getMeta());
		ItemStack crystexiumBlockDark = new ItemStack(ModBlocks.darkCrystexiumBlock, 1, CrystexiumBlockType.NORMAL.getMeta());
		ItemStack crystexiumBlockPure = new ItemStack(ModBlocks.pureCrystexiumBlock, 1, CrystexiumBlockType.NORMAL.getMeta());
		
		ItemStack crystexiumEssence = new ItemStack(ModItems.crystexItems, 1, CrystexItemType.CRYSTEXIUM_ESSENCE.getMeta());
		ItemStack crystexiumEssenceBlue = new ItemStack(ModItems.crystexItems, 1, CrystexItemType.CRYSTEXIUM_ESSENCE_BLUE.getMeta());
		ItemStack crystexiumEssenceRed = new ItemStack(ModItems.crystexItems, 1, CrystexItemType.CRYSTEXIUM_ESSENCE_RED.getMeta());
		ItemStack crystexiumEssenceGreen = new ItemStack(ModItems.crystexItems, 1, CrystexItemType.CRYSTEXIUM_ESSENCE_GREEN.getMeta());
		ItemStack crystexiumEssenceDark = new ItemStack(ModItems.crystexItems, 1, CrystexItemType.CRYSTEXIUM_ESSENCE_DARK.getMeta());
		ItemStack crystexiumEssencePure = new ItemStack(ModItems.crystexItems, 1, CrystexItemType.CRYSTEXIUM_ESSENCE_PURE.getMeta());
		
		addRecipe(crystexiumBlock, crystexiumEssence, 1600);
		addRecipe(crystexiumBlockBlue, crystexiumEssenceBlue, 1600);
		addRecipe(crystexiumBlockRed, crystexiumEssenceRed, 1600);
		addRecipe(crystexiumBlockGreen, crystexiumEssenceGreen, 1600);
		addRecipe(crystexiumBlockDark, crystexiumEssenceDark, 1600);
		addRecipe(crystexiumBlockPure, crystexiumEssencePure, 1600);
	}
	
	public static BasicMachineRecipe addRecipe(ItemStack input, ItemStack output, int energy){
		BasicMachineRecipe recipe = new BasicMachineRecipe(input, output, energy);
		return addRecipe(input, recipe);
	}
	
	public static BasicMachineRecipe addRecipe(ItemStack input, BasicMachineRecipe recipe){
		if(recipeMap.containsKey(input)) return null;
		recipeMap.put(input, recipe);
		return recipe;
	}
	
}
