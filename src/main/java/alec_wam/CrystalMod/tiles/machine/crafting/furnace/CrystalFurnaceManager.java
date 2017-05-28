package alec_wam.CrystalMod.tiles.machine.crafting.furnace;

import java.util.HashMap;
import java.util.Map;

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
	}
	
}
