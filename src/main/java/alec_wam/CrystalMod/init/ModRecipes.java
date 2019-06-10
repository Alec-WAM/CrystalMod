package alec_wam.CrystalMod.init;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.tiles.fusion.ModFusionRecipes;
import alec_wam.CrystalMod.tiles.machine.crafting.grinder.GrinderRecipe;
import alec_wam.CrystalMod.tiles.machine.crafting.press.PressRecipe;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraftforge.common.crafting.RecipeType;

public class ModRecipes {

	public static final String CATEGORY_GRINDER = CrystalMod.resource("grinder");
	public static final RecipeType<GrinderRecipe> GRINDER = RecipeType.get(CrystalMod.resourceL("grinder"), GrinderRecipe.class);
	public static final IRecipeType<GrinderRecipe> GRINDER_TYPE = IRecipeType.<GrinderRecipe>func_222147_a(CATEGORY_GRINDER);
	public static final IRecipeSerializer<GrinderRecipe> GRINDER_SERIALIZER = IRecipeSerializer.func_222156_a(CATEGORY_GRINDER, new GrinderRecipe.Serializer());	
	
	public static final String CATEGORY_PRESS = CrystalMod.resource("press");
	public static final RecipeType<PressRecipe> PRESS = RecipeType.get(CrystalMod.resourceL("press"), PressRecipe.class);
	public static final IRecipeType<PressRecipe> PRESS_TYPE = IRecipeType.<PressRecipe>func_222147_a(CATEGORY_PRESS);
	public static final IRecipeSerializer<PressRecipe> PRESS_SERIALIZER = IRecipeSerializer.func_222156_a(CATEGORY_PRESS, new PressRecipe.Serializer());
	
	public static void registerModRecipes(){
		ModFusionRecipes.init();	
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends IRecipe<? extends IInventory>> List<T> getRecipes(RecipeManager manager, IRecipeType<T> type) {
		List<IRecipe<?>> masterList = new ArrayList<IRecipe<?>>(manager.getRecipes());
		List<T> sortedList = Lists.newArrayList();
		for(IRecipe<?> recipe : masterList){
			if(recipe.func_222127_g() == type){
				sortedList.add((T)recipe);
			}
		}
		return sortedList;
	}
	
}
