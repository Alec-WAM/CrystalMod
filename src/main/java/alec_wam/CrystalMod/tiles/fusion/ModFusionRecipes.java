package alec_wam.CrystalMod.tiles.fusion;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.blocks.BlockCrystal.CrystalBlockType;
import alec_wam.CrystalMod.blocks.BlockCrystalIngot.CrystalIngotBlockType;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ItemIngot.IngotType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.fusion.recipes.FusionRecipeEnchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public class ModFusionRecipes {

	public static void initRecipes(){
		Vec3d colorPure = new Vec3d(255, 255, 255);
		List<ItemStack> ingredientsPure = Lists.newArrayList();
		ingredientsPure.add(new ItemStack(ModItems.crystals, 1, CrystalType.BLUE.getMetadata()));
		ingredientsPure.add(new ItemStack(ModItems.crystals, 1, CrystalType.RED.getMetadata()));
		ingredientsPure.add(new ItemStack(ModItems.crystals, 1, CrystalType.GREEN.getMetadata()));
		ingredientsPure.add(new ItemStack(ModItems.crystals, 1, CrystalType.DARK.getMetadata()));
		CrystalModAPI.registerFusion(new FusionRecipe("gemCrystal", ingredientsPure, new ItemStack(ModItems.crystals, 1, CrystalType.PURE.getMetadata()), colorPure));
		
		List<ItemStack> ingredientsPureIngot = Lists.newArrayList();
		ingredientsPureIngot.add(new ItemStack(ModItems.ingots, 1, IngotType.BLUE.getMetadata()));
		ingredientsPureIngot.add(new ItemStack(ModItems.ingots, 1, IngotType.RED.getMetadata()));
		ingredientsPureIngot.add(new ItemStack(ModItems.ingots, 1, IngotType.GREEN.getMetadata()));
		ingredientsPureIngot.add(new ItemStack(ModItems.ingots, 1, IngotType.DARK.getMetadata()));
		CrystalModAPI.registerFusion(new FusionRecipe("ingotCrystal", ingredientsPureIngot, new ItemStack(ModItems.ingots, 1, IngotType.PURE.getMetadata()), colorPure));
		
		List<ItemStack> ingredientsPureBlock = Lists.newArrayList();
		ingredientsPureBlock.add(new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.BLUE.getMeta()));
		ingredientsPureBlock.add(new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.RED.getMeta()));
		ingredientsPureBlock.add(new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.GREEN.getMeta()));
		ingredientsPureBlock.add(new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.DARK.getMeta()));
		CrystalModAPI.registerFusion(new FusionRecipe("blockCrystal", ingredientsPureBlock, new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.PURE.getMeta()), colorPure));
		
		List<ItemStack> ingredientsPureIngotBlock = Lists.newArrayList();
		ingredientsPureIngotBlock.add(new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.BLUE.getMeta()));
		ingredientsPureIngotBlock.add(new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.RED.getMeta()));
		ingredientsPureIngotBlock.add(new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.GREEN.getMeta()));
		ingredientsPureIngotBlock.add(new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.DARK.getMeta()));
		CrystalModAPI.registerFusion(new FusionRecipe("blockIngotCrystal", ingredientsPureIngotBlock, new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.PURE.getMeta()), colorPure));
		
		List<Object> ingredientsPearl = Lists.newArrayList();
		for(int i = 0; i < 4; i++)ingredientsPearl.add("nuggetCrystal");
		for(int i = 0; i < 4; i++)ingredientsPearl.add(new ItemStack(ModItems.ingots, 1, IngotType.PURE.getMetadata()));
		addRecipe(new ItemStack(Items.ENDER_EYE), new ItemStack(ModItems.telePearl), ingredientsPearl.toArray());
		
		CrystalModAPI.registerFusion(new FusionRecipeEnchantment());
	}
	
	public static void addRecipe(ItemStack input, ItemStack output, Object[] ingredients){
		addRecipe(input, output, ingredients, new Vec3d(0, 255, 255));
	}

	public static void addRecipe(ItemStack input, ItemStack output, Object[] ingredients, Vec3d color){
		CrystalModAPI.registerFusion(new FusionRecipe(input, Arrays.asList(ingredients), output, color));
	}
	
}
