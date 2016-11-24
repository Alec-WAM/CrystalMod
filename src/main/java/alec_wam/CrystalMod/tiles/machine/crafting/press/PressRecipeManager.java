package alec_wam.CrystalMod.tiles.machine.crafting.press;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.ItemIngot.IngotType;
import alec_wam.CrystalMod.items.ItemMetalPlate.PlateType;
import alec_wam.CrystalMod.tiles.machine.BasicMachineRecipe;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class PressRecipeManager {

	private static List<BasicMachineRecipe> recipes = new ArrayList<BasicMachineRecipe>();
	
	public static BasicMachineRecipe getRecipe(ItemStack input){
		if (ItemStackTools.isNullStack(input)) {
			return null;
		}
		
		for(BasicMachineRecipe recipe : recipes){
			if(recipe.matchesInput(input)){
				return recipe;
			}
		}
		return null;
	}
	
	public static List<BasicMachineRecipe> getRecipes(){
		return recipes;
	}
	
	public static BasicMachineRecipe addRecipe(ItemStack input, ItemStack output, int energy){
		BasicMachineRecipe recipe = new BasicMachineRecipe(input, output, energy);
		return addRecipe(recipe);
	}
	
	public static BasicMachineRecipe addRecipe(BasicMachineRecipe recipe){
		recipes.add(recipe);
		return recipe;
	}
	
	public static void initRecipes(){
		final int defaultPower = 1600;
		addRecipe(new ItemStack(ModItems.ingots, 1, IngotType.BLUE.getMetadata()), new ItemStack(ModItems.plates, 1, PlateType.BLUE.getMetadata()), defaultPower);
		addRecipe(new ItemStack(ModItems.ingots, 1, IngotType.RED.getMetadata()), new ItemStack(ModItems.plates, 1, PlateType.RED.getMetadata()), defaultPower);
		addRecipe(new ItemStack(ModItems.ingots, 1, IngotType.GREEN.getMetadata()), new ItemStack(ModItems.plates, 1, PlateType.GREEN.getMetadata()), defaultPower);
		addRecipe(new ItemStack(ModItems.ingots, 1, IngotType.DARK.getMetadata()), new ItemStack(ModItems.plates, 1, PlateType.DARK.getMetadata()), defaultPower);
		addRecipe(new ItemStack(ModItems.ingots, 1, IngotType.PURE.getMetadata()), new ItemStack(ModItems.plates, 1, PlateType.PURE.getMetadata()), defaultPower);
		addRecipe(new ItemStack(ModItems.ingots, 1, IngotType.DARK_IRON.getMetadata()), new ItemStack(ModItems.plates, 1, PlateType.DARK_IRON.getMetadata()), defaultPower);
		addRecipe(new ItemStack(Items.BLAZE_POWDER, 2, 0), new ItemStack(Items.BLAZE_ROD), defaultPower);
	}
	
}
