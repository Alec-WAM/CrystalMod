package alec_wam.CrystalMod.tiles.machine.crafting.press;

import java.util.ArrayList;
import java.util.List;

import alec_wam.CrystalMod.items.ItemIngot.IngotType;
import alec_wam.CrystalMod.items.ItemMetalPlate.PlateType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.machine.BasicMachineRecipe;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

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
		if(recipes.contains(recipe)) return null;
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

	public static void oreSearch() {
		ModLogger.info("[Press Recipe Manager] Searching for metal plates....");
		for(String name : OreDictionary.getOreNames()){
			if(name.length() > 5 && name.substring(0, 5).equals("ingot")){
				String type = name.substring(5);
				NonNullList<ItemStack> plates = OreDictionary.getOres("plate"+type);
				if(!plates.isEmpty()){
					ModLogger.info("Found "+type+" plate adding recipe now.");
					ItemStack plateCopy = ItemStackTools.safeCopy(plates.get(0));
					if(plateCopy.getItemDamage() == OreDictionary.WILDCARD_VALUE){
						plateCopy.setItemDamage(0);
					}
					NonNullList<ItemStack> ingots = OreDictionary.getOres(name);
					for(ItemStack ingot : ingots){
						ItemStack ingotCopy = ItemStackTools.safeCopy(ingot);
						if(ingotCopy.getItemDamage() == OreDictionary.WILDCARD_VALUE){
							ingotCopy.setItemDamage(0);
						}
						if(getRecipe(ingotCopy) == null){
							if(addRecipe(ingotCopy, plateCopy, 1600) !=null){
								ModLogger.info(ingotCopy+" -> "+plateCopy);
							}
						}
					}
				}
			}
		}
	}
	
}
