package alec_wam.CrystalMod.init;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.init.recipes.PoweredShieldRecipes;
import alec_wam.CrystalMod.tiles.fusion.ModFusionRecipes;
import alec_wam.CrystalMod.tiles.machine.crafting.grinder.GrinderRecipe;
import alec_wam.CrystalMod.tiles.machine.crafting.press.PressRecipe;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.crafting.RecipeType;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ModRecipes {

	public static final ResourceLocation CATEGORY_GRINDER = CrystalMod.resourceL("grinder");
	public static final RecipeType<GrinderRecipe> GRINDER = RecipeType.get(CATEGORY_GRINDER, GrinderRecipe.class);
	public static final IRecipeType<GrinderRecipe> GRINDER_TYPE = IRecipeType.<GrinderRecipe>func_222147_a(CATEGORY_GRINDER.toString());
	public static final IRecipeSerializer<GrinderRecipe> GRINDER_SERIALIZER = createRecipeSerializer(CATEGORY_GRINDER, new GrinderRecipe.Serializer());	
	
	public static final ResourceLocation CATEGORY_PRESS = CrystalMod.resourceL("press");
	public static final RecipeType<PressRecipe> PRESS = RecipeType.get(CATEGORY_PRESS, PressRecipe.class);
	public static final IRecipeType<PressRecipe> PRESS_TYPE = IRecipeType.<PressRecipe>func_222147_a(CATEGORY_PRESS.toString());
	public static final IRecipeSerializer<PressRecipe> PRESS_SERIALIZER = createRecipeSerializer(CATEGORY_PRESS, new PressRecipe.Serializer());
	
	public static final ResourceLocation CATEGORY_SHIELD = CrystalMod.resourceL("shield_decoration");
	public static final RecipeType<PoweredShieldRecipes> SHIELD = RecipeType.get(CATEGORY_SHIELD, PoweredShieldRecipes.class);
	public static final IRecipeType<PoweredShieldRecipes> SHIELD_TYPE = IRecipeType.<PoweredShieldRecipes>func_222147_a(CATEGORY_SHIELD.toString());
	public static final IRecipeSerializer<PoweredShieldRecipes> SHIELD_SERIALIZER = createRecipeSerializer(CATEGORY_SHIELD, new SpecialRecipeSerializer<>(PoweredShieldRecipes::new));
	
	
	public static void registerModRecipes(){
		ModFusionRecipes.init();	
	}
	
	public static <S extends IRecipeSerializer<T>, T extends IRecipe<?>> S createRecipeSerializer(ResourceLocation name, S serializer) {
		serializer.setRegistryName(name);
		return serializer;
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
	
	public static RecipeManager getRecipeManager() {
		if(FMLEnvironment.dist == Dist.DEDICATED_SERVER){
			return ServerLifecycleHooks.getCurrentServer().getRecipeManager();
		} else {
			return ServerLifecycleHooks.getCurrentServer().getRecipeManager();
		}
	}
	
}
