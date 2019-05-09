package alec_wam.CrystalMod.tiles.fusion;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.init.ModItems;
import alec_wam.CrystalMod.tiles.fusion.recipe.FusionRecipe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public class ModFusionRecipes {

	public static void init() {
		Vec3d colorPure = new Vec3d(255, 255, 255);
		List<Item> allCrystals = Lists.newArrayList();
		allCrystals.add(ModItems.crystalGroup.getItem(EnumCrystalColorSpecial.BLUE));
		allCrystals.add(ModItems.crystalGroup.getItem(EnumCrystalColorSpecial.RED));
		allCrystals.add(ModItems.crystalGroup.getItem(EnumCrystalColorSpecial.GREEN));
		allCrystals.add(ModItems.crystalGroup.getItem(EnumCrystalColorSpecial.DARK));
		CrystalModAPI.registerFusion(new FusionRecipe(allCrystals, allCrystals, new ItemStack(ModItems.crystalGroup.getItem(EnumCrystalColorSpecial.PURE), 1), colorPure));
		
		List<Block> allCrystalBlocks = Lists.newArrayList();
		allCrystalBlocks.add(ModBlocks.crystalBlockGroup.getBlock(EnumCrystalColorSpecial.BLUE));
		allCrystalBlocks.add(ModBlocks.crystalBlockGroup.getBlock(EnumCrystalColorSpecial.RED));
		allCrystalBlocks.add(ModBlocks.crystalBlockGroup.getBlock(EnumCrystalColorSpecial.GREEN));
		allCrystalBlocks.add(ModBlocks.crystalBlockGroup.getBlock(EnumCrystalColorSpecial.DARK));
		CrystalModAPI.registerFusion(new FusionRecipe(allCrystalBlocks, allCrystalBlocks, new ItemStack(ModBlocks.crystalBlockGroup.getBlock(EnumCrystalColorSpecial.PURE), 1), colorPure));
	}

}
