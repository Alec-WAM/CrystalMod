package alec_wam.CrystalMod.compatibility.materials;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.compatibility.materials.ItemMaterial.DustRecipe;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class MaterialSerializer {

	@SuppressWarnings("deprecation")
	public static ItemMaterial read(ResourceLocation recipeId, JsonObject json) {
		int color = JSONUtils.getInt(json, "color", 0);
		boolean dust = JSONUtils.getBoolean(json, "dust", false);
		boolean plate = JSONUtils.getBoolean(json, "plate", false);
		
		if(!dust && !plate){
			throw new JsonSyntaxException("Material must have at least one item type! " + recipeId.getPath());
		}
		DustRecipe[] dustRecipes;
		Ingredient plateIngredient = null;
		if(json.has("dust_recipes") && dust){
			JsonArray recipeArray = JSONUtils.getJsonArray(json, "dust_recipes", new JsonArray());
			dustRecipes = new DustRecipe[recipeArray.size()];
			for(int r = 0; r < recipeArray.size(); r++){
				JsonElement element = recipeArray.get(r);
				JsonObject obj = element.getAsJsonObject();
				if(!JSONUtils.isString(obj, "name")){
					CrystalMod.LOGGER.warn("Material " + recipeId.getPath()+ " dust recipe " + r + " is missing a name!");
					continue;
				}
				
				String name = JSONUtils.getString(obj, "name");
				int output = JSONUtils.getInt(obj, "count", 1);
				int energy = JSONUtils.getInt(obj, "energy", 800);
				Ingredient ingredient;
				if (JSONUtils.isJsonArray(obj, "ingredient")) {
					ingredient = Ingredient.deserialize(JSONUtils.getJsonArray(obj, "ingredient"));
				} else {
					ingredient = Ingredient.deserialize(JSONUtils.getJsonObject(obj, "ingredient"));
				}
				dustRecipes[r] = new DustRecipe(name, ingredient, output, energy);
			}
		} else {
			dustRecipes = new DustRecipe[0];
		}
		ItemStack dustSmeltOutput = ItemStackTools.getEmptyStack();
		if(dust){
			if(json.has("dust_smelt")){
				String s1 = JSONUtils.getString(json, "dust_smelt");
				ResourceLocation resourcelocation = new ResourceLocation(s1);
				dustSmeltOutput = new ItemStack(Registry.field_212630_s.func_218349_b(resourcelocation).orElseThrow(() -> {
					return new IllegalStateException("Item: " + s1 + " does not exist");
				}));
			}
		}
		if(json.has("plate_ingredient") && plate){
			if (JSONUtils.isJsonArray(json, "plate_ingredient")) {
				plateIngredient = Ingredient.deserialize(JSONUtils.getJsonArray(json, "plate_ingredient"));
			} else {
				plateIngredient = Ingredient.deserialize(JSONUtils.getJsonObject(json, "plate_ingredient"));
			}
		}
		return new ItemMaterial(dustRecipes, plateIngredient, color).setDust(dust).setDustSmeltOutput(dustSmeltOutput).setPlate(plate).setRegistryName(recipeId);
	}
	
}
