package alec_wam.CrystalMod.tiles.fusion;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.init.ModItems;
import alec_wam.CrystalMod.items.ItemGrassSeeds.EnumGrassSeedItem;
import alec_wam.CrystalMod.tiles.fusion.recipe.FusionRecipe;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public class ModFusionRecipes {

	public static void init() {
		Vec3d colorPure = new Vec3d(255, 255, 255);
		Vec3d colorBlue = new Vec3d(0, 0, 255);
		Vec3d colorGreen = new Vec3d(0, 255, 0);
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
		
		List<Block> allCrystalSaplings = Lists.newArrayList();
		allCrystalSaplings.add(ModBlocks.crystalSaplingGroup.getBlock(EnumCrystalColorSpecial.BLUE));
		allCrystalSaplings.add(ModBlocks.crystalSaplingGroup.getBlock(EnumCrystalColorSpecial.RED));
		allCrystalSaplings.add(ModBlocks.crystalSaplingGroup.getBlock(EnumCrystalColorSpecial.GREEN));
		allCrystalSaplings.add(ModBlocks.crystalSaplingGroup.getBlock(EnumCrystalColorSpecial.DARK));
		CrystalModAPI.registerFusion(new FusionRecipe(allCrystalSaplings, allCrystalSaplings, new ItemStack(ModBlocks.crystalSaplingGroup.getBlock(EnumCrystalColorSpecial.PURE), 1), colorPure));
		
		for(EnumCrystalColorSpecial color : new EnumCrystalColorSpecial[]{EnumCrystalColorSpecial.BLUE, EnumCrystalColorSpecial.RED, EnumCrystalColorSpecial.GREEN, EnumCrystalColorSpecial.DARK}){
			List<Item> sapling = Lists.newArrayList();
			for(int i = 0; i < 4; i++){
				sapling.add(ModItems.crystalShardGroup.getItem(color));
			}
			CrystalModAPI.registerFusion(new FusionRecipe(Blocks.OAK_SAPLING, sapling, new ItemStack(ModBlocks.crystalSaplingGroup.getBlock(color), 1), colorBlue));
		}
		
		List<Block> grassSeeds = Lists.newArrayList();
		grassSeeds.add(Blocks.DIRT);
		grassSeeds.add(Blocks.GRASS);
		CrystalModAPI.registerFusion(new FusionRecipe(Items.WHEAT_SEEDS, grassSeeds, new ItemStack(ModItems.grassSeeds.getItem(EnumGrassSeedItem.GRASS), 1), colorGreen));	
		List<Block> podzolSeeds = Lists.newArrayList();
		podzolSeeds.add(Blocks.DIRT);
		podzolSeeds.add(Blocks.OAK_LEAVES);
		podzolSeeds.add(Blocks.OAK_LEAVES);
		CrystalModAPI.registerFusion(new FusionRecipe(Items.WHEAT_SEEDS, podzolSeeds, new ItemStack(ModItems.grassSeeds.getItem(EnumGrassSeedItem.PODZOL), 1), colorGreen));
		List<Block> myceliumSeeds = Lists.newArrayList();
		myceliumSeeds.add(Blocks.DIRT);
		myceliumSeeds.add(Blocks.BROWN_MUSHROOM);
		myceliumSeeds.add(Blocks.RED_MUSHROOM);
		CrystalModAPI.registerFusion(new FusionRecipe(Items.WHEAT_SEEDS, myceliumSeeds, new ItemStack(ModItems.grassSeeds.getItem(EnumGrassSeedItem.MYCELIUM), 1), colorGreen));
	}

}
