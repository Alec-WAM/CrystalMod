package alec_wam.CrystalMod.api;

import java.util.ArrayList;
import java.util.List;

import alec_wam.CrystalMod.api.recipes.IFusionRecipe;
import alec_wam.CrystalMod.api.tile.IFusionPedistal;
import alec_wam.CrystalMod.api.tile.IPedistal;
import net.minecraft.world.World;

public class CrystalModAPI {

	//Fusion
	private static final List<IFusionRecipe> FUSION_RECIPES = new ArrayList<IFusionRecipe>();

	public static IFusionRecipe registerFusion(IFusionRecipe recipe){
		FUSION_RECIPES.add(recipe);
		return recipe;
	}

	public static IFusionRecipe findFusionRecipe(IFusionPedistal pedistal, World world, List<IPedistal> linkedPedistals) {
		for(IFusionRecipe recipe : FUSION_RECIPES){
			if(recipe.matches(pedistal, world, linkedPedistals)){
				return recipe;
			}
		}
		return null;
	}

	public static List<IFusionRecipe> getFusionRecipes(){
		return FUSION_RECIPES;
	}

}
