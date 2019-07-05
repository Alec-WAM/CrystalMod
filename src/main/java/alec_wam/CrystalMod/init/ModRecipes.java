package alec_wam.CrystalMod.init;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ModRecipes {

	public static final ResourceLocation CATEGORY_GRINDER = CrystalMod.resourceL("grinder");
	//public static final RecipeType<GrinderRecipe> GRINDER = RecipeType.get(CATEGORY_GRINDER, GrinderRecipe.class);
	public static final IRecipeType<GrinderRecipe> GRINDER_TYPE = IRecipeType.<GrinderRecipe>register(CATEGORY_GRINDER.toString());
	public static final IRecipeSerializer<GrinderRecipe> GRINDER_SERIALIZER = createRecipeSerializer(CATEGORY_GRINDER, new GrinderRecipe.Serializer());	
	
	public static final ResourceLocation CATEGORY_PRESS = CrystalMod.resourceL("press");
	//public static final RecipeType<PressRecipe> PRESS = RecipeType.get(CATEGORY_PRESS, PressRecipe.class);
	public static final IRecipeType<PressRecipe> PRESS_TYPE = IRecipeType.<PressRecipe>register(CATEGORY_PRESS.toString());
	public static final IRecipeSerializer<PressRecipe> PRESS_SERIALIZER = createRecipeSerializer(CATEGORY_PRESS, new PressRecipe.Serializer());
	
	public static final ResourceLocation CATEGORY_SHIELD = CrystalMod.resourceL("shield_decoration");
	//public static final RecipeType<PoweredShieldRecipes> SHIELD = RecipeType.get(CATEGORY_SHIELD, PoweredShieldRecipes.class);
	public static final IRecipeType<PoweredShieldRecipes> SHIELD_TYPE = IRecipeType.<PoweredShieldRecipes>register(CATEGORY_SHIELD.toString());
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
			if(recipe.getType() == type){
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

	public static void addRecipeToManager(IRecipe<?> recipe) {
		RecipeManager manager = getRecipeManager();
		Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> map = Maps.newHashMap();
		Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> otherMap =  ObfuscationReflectionHelper.getPrivateValue(RecipeManager.class, manager, 2);
		for(Entry<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> entry : otherMap.entrySet()){
			map.put(entry.getKey(), entry.getValue());
		}	
		Map<ResourceLocation, IRecipe<?>> map1 = Maps.newHashMap();
		if(otherMap.containsKey(recipe.getType())){
			Map<ResourceLocation, IRecipe<?>> other = otherMap.get(recipe.getType());
			for(Entry<ResourceLocation, IRecipe<?>> entry : other.entrySet()){
				map1.put(entry.getKey(), entry.getValue());
			}
		}
		IRecipe<?> irecipe = map1.put(recipe.getId(), recipe);
		if (irecipe != null) {
			throw new IllegalStateException("Duplicate recipe ignored with ID " + recipe.getId());
		}
		map.put(recipe.getType(), map1);
		ObfuscationReflectionHelper.setPrivateValue(RecipeManager.class, manager, ImmutableMap.copyOf(map), 2);
	}
}
