package alec_wam.CrystalMod.init;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.tiles.fusion.ModFusionRecipes;
import alec_wam.CrystalMod.tiles.machine.crafting.grinder.GrinderRecipe;
import alec_wam.CrystalMod.tiles.machine.crafting.press.PressRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraftforge.common.crafting.RecipeType;

public class ModRecipes {

	public static final RecipeType<GrinderRecipe> GRINDER = RecipeType.get(CrystalMod.resourceL("grinder"), GrinderRecipe.class);
	public static final IRecipeSerializer<GrinderRecipe> GRINDER_SERIALIZER = RecipeSerializers.register(new GrinderRecipe.Serializer());
	public static final RecipeType<PressRecipe> PRESS = RecipeType.get(CrystalMod.resourceL("press"), PressRecipe.class);
	public static final IRecipeSerializer<PressRecipe> PRESS_SERIALIZER = RecipeSerializers.register(new PressRecipe.Serializer());
	
	public static void registerModRecipes(){
		ModFusionRecipes.init();	
	}
	
}
