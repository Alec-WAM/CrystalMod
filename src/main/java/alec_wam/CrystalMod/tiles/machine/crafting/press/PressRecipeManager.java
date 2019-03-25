package alec_wam.CrystalMod.tiles.machine.crafting.press;

import java.util.ArrayList;
import java.util.List;

import alec_wam.CrystalMod.items.ItemIngot.IngotType;
import alec_wam.CrystalMod.items.ItemMetalPlate.PlateType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.machine.BasicMachineRecipe;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.init.Blocks;
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
		addRecipe(new ItemStack(ModItems.ingots, 1, IngotType.BLUE.getMeta()), new ItemStack(ModItems.plates, 1, PlateType.BLUE.getMeta()), defaultPower);
		addRecipe(new ItemStack(ModItems.ingots, 1, IngotType.RED.getMeta()), new ItemStack(ModItems.plates, 1, PlateType.RED.getMeta()), defaultPower);
		addRecipe(new ItemStack(ModItems.ingots, 1, IngotType.GREEN.getMeta()), new ItemStack(ModItems.plates, 1, PlateType.GREEN.getMeta()), defaultPower);
		addRecipe(new ItemStack(ModItems.ingots, 1, IngotType.DARK.getMeta()), new ItemStack(ModItems.plates, 1, PlateType.DARK.getMeta()), defaultPower);
		addRecipe(new ItemStack(ModItems.ingots, 1, IngotType.PURE.getMeta()), new ItemStack(ModItems.plates, 1, PlateType.PURE.getMeta()), defaultPower);
		addRecipe(new ItemStack(ModItems.ingots, 1, IngotType.DARK_IRON.getMeta()), new ItemStack(ModItems.plates, 1, PlateType.DARK_IRON.getMeta()), defaultPower);
		addRecipe(new ItemStack(Items.BLAZE_POWDER, 2, 0), new ItemStack(Items.BLAZE_ROD), defaultPower);
		addRecipe(new ItemStack(Blocks.ICE, 1), new ItemStack(Blocks.PACKED_ICE, 1), 400);
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
