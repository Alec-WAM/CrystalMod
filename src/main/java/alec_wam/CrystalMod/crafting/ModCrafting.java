package alec_wam.CrystalMod.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.BlockCrystal.CrystalBlockType;
import alec_wam.CrystalMod.blocks.BlockCrystalIngot.CrystalIngotBlockType;
import alec_wam.CrystalMod.blocks.BlockCrystalOre.CrystalOreType;
import alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass.GlassType;
import alec_wam.CrystalMod.crafting.recipes.ShapedNBTCopy;
import alec_wam.CrystalMod.crafting.recipes.UpgradeItemRecipe;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ItemIngot.IngotType;
import alec_wam.CrystalMod.items.ItemMetalPlate.PlateType;
import alec_wam.CrystalMod.items.guide.ItemCrystalGuide.GuideType;
import alec_wam.CrystalMod.items.tools.ItemToolParts.PartType;
import alec_wam.CrystalMod.tiles.cauldron.CauldronRecipeManager;
import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCrystalMachine.MachineType;
import alec_wam.CrystalMod.tiles.machine.crafting.furnace.CrystalFurnaceManager;
import alec_wam.CrystalMod.tiles.machine.crafting.infuser.CrystalInfusionManager;
import alec_wam.CrystalMod.tiles.machine.crafting.liquidizer.LiquidizerRecipeManager;
import alec_wam.CrystalMod.tiles.machine.crafting.press.PressRecipeManager;
import alec_wam.CrystalMod.tiles.machine.elevator.ItemMiscCard;
import alec_wam.CrystalMod.tiles.machine.power.converter.BlockPowerConverter.ConverterType;
import alec_wam.CrystalMod.tiles.machine.power.engine.BlockEngine.EngineType;
import alec_wam.CrystalMod.tiles.pipes.BlockPipe.PipeType;
import alec_wam.CrystalMod.tiles.pipes.covers.ItemPipeCover;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.BlockPatternEncoder.EncoderType;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.BlockPanel.PanelType;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.ItemHDD;
import alec_wam.CrystalMod.tiles.pipes.item.filters.ItemPipeFilter.FilterType;
import alec_wam.CrystalMod.tiles.tank.BlockTank.TankType;
import alec_wam.CrystalMod.tiles.workbench.BlockCrystalWorkbench.WorkbenchType;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ModCrafting {

	public static void init() {
		initOreDic();
		
		GameRegistry.addRecipe(new UpgradeItemRecipe());
		
		ItemStack blueCrystal = new ItemStack(ModItems.crystals, 1, CrystalType.BLUE.getMetadata());
		ItemStack blueIngot = new ItemStack(ModItems.ingots, 1, IngotType.BLUE.getMetadata());
		ItemStack blueNugget = new ItemStack(ModItems.crystals, 1, CrystalType.BLUE_NUGGET.getMetadata());
		ItemStack bluePlate = new ItemStack(ModItems.plates, 1, PlateType.BLUE.getMetadata());
		
		ItemStack redCrystal = new ItemStack(ModItems.crystals, 1, CrystalType.RED.getMetadata());
		ItemStack redIngot = new ItemStack(ModItems.ingots, 1, IngotType.RED.getMetadata());
		ItemStack redNugget = new ItemStack(ModItems.crystals, 1, CrystalType.RED_NUGGET.getMetadata());
		ItemStack redPlate = new ItemStack(ModItems.plates, 1, PlateType.RED.getMetadata());
		
		ItemStack greenCrystal = new ItemStack(ModItems.crystals, 1, CrystalType.GREEN.getMetadata());
		ItemStack greenIngot = new ItemStack(ModItems.ingots, 1, IngotType.GREEN.getMetadata());
		ItemStack greenNugget = new ItemStack(ModItems.crystals, 1, CrystalType.GREEN_NUGGET.getMetadata());
		ItemStack greenPlate = new ItemStack(ModItems.plates, 1, PlateType.GREEN.getMetadata());
		
		ItemStack darkCrystal = new ItemStack(ModItems.crystals, 1, CrystalType.DARK.getMetadata());
		ItemStack darkIngot = new ItemStack(ModItems.ingots, 1, IngotType.DARK.getMetadata());
		ItemStack darkNugget = new ItemStack(ModItems.crystals, 1, CrystalType.DARK_NUGGET.getMetadata());
		ItemStack darkPlate = new ItemStack(ModItems.plates, 1, PlateType.DARK.getMetadata());
		
		ItemStack pureCrystal = new ItemStack(ModItems.crystals, 1, CrystalType.PURE.getMetadata());
		ItemStack pureIngot = new ItemStack(ModItems.ingots, 1, IngotType.PURE.getMetadata());
		ItemStack pureNugget = new ItemStack(ModItems.crystals, 1, CrystalType.PURE_NUGGET.getMetadata());
		ItemStack purePlate = new ItemStack(ModItems.plates, 1, PlateType.PURE.getMetadata());
		
		ItemStack dIronIngot = new ItemStack(ModItems.ingots, 1, IngotType.DARK_IRON.getMetadata());
		ItemStack dIronNugget = new ItemStack(ModItems.crystals, 1, CrystalType.DIRON_NUGGET.getMetadata());
		ItemStack dIronPlate = new ItemStack(ModItems.plates, 1, PlateType.DARK_IRON.getMetadata());
		
		ItemStack crystalRod = new ItemStack(ModItems.toolParts);
    	ItemNBTHelper.setString(crystalRod, "Type", PartType.ROD.getName());
		
		GameRegistry.addSmelting(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.BLUE.getMeta()), blueCrystal, 1.0F);
		GameRegistry.addSmelting(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.RED.getMeta()), redCrystal, 1.0F);
		GameRegistry.addSmelting(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.GREEN.getMeta()), greenCrystal, 1.0F);
		GameRegistry.addSmelting(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.DARK.getMeta()), darkCrystal, 1.0F);
		
		GameRegistry.addSmelting(new ItemStack(ModItems.crystals, 1, CrystalType.BLUE_SHARD.getMetadata()), blueNugget, 0.1F);
		GameRegistry.addSmelting(new ItemStack(ModItems.crystals, 1, CrystalType.RED_SHARD.getMetadata()), redNugget, 0.1F);
		GameRegistry.addSmelting(new ItemStack(ModItems.crystals, 1, CrystalType.GREEN_SHARD.getMetadata()), greenNugget, 0.1F);
		GameRegistry.addSmelting(new ItemStack(ModItems.crystals, 1, CrystalType.DARK_SHARD.getMetadata()), darkNugget, 0.1F);
		GameRegistry.addSmelting(new ItemStack(ModItems.crystals, 1, CrystalType.PURE_SHARD.getMetadata()), pureNugget, 0.1F);
		
		GameRegistry.addSmelting(blueCrystal, blueIngot, 1.0F);
		GameRegistry.addSmelting(redCrystal, redIngot, 1.0F);
		GameRegistry.addSmelting(greenCrystal, greenIngot, 1.0F);
		GameRegistry.addSmelting(darkCrystal, darkIngot, 1.0F);
		GameRegistry.addSmelting(pureCrystal, pureIngot, 1.0F);

		addShapedRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.BLUE_SHARD.getMetadata()), "X  ", "X  ", "X  ", 'X', ModItems.crystalReeds);
		addShapedRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.RED_SHARD.getMetadata()), " X ", " X ", " X ", 'X', ModItems.crystalReeds);
		addShapedRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.GREEN_SHARD.getMetadata()), "X  ", " X ", "  X", 'X', ModItems.crystalReeds);
		addShapedRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.DARK_SHARD.getMetadata()), "XXX", 'X', ModItems.crystalReeds);
		
		create9x9Recipe(blueCrystal, new ItemStack(ModItems.crystals, 1, CrystalType.BLUE_SHARD.getMetadata()), 9);
		create9x9Recipe(blueIngot, blueNugget, 9);
		create9x9Recipe(redCrystal, new ItemStack(ModItems.crystals, 1, CrystalType.RED_SHARD.getMetadata()), 9);
		create9x9Recipe(redIngot, redNugget, 9);
		create9x9Recipe(greenCrystal, new ItemStack(ModItems.crystals, 1, CrystalType.GREEN_SHARD.getMetadata()), 9);
		create9x9Recipe(greenIngot, greenNugget, 9);
		create9x9Recipe(darkCrystal, new ItemStack(ModItems.crystals, 1, CrystalType.DARK_SHARD.getMetadata()), 9);
		create9x9Recipe(darkIngot, darkNugget, 9);
		create9x9Recipe(pureCrystal, new ItemStack(ModItems.crystals, 1, CrystalType.PURE_SHARD.getMetadata()), 9);
		create9x9Recipe(pureIngot, pureNugget, 9);
		create9x9Recipe(dIronIngot, dIronNugget, 9);

		create9x9Recipe(new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.BLUE.getMeta()), blueCrystal, 9);
		create9x9Recipe(new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.RED.getMeta()), redCrystal, 9);
		create9x9Recipe(new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.GREEN.getMeta()), greenCrystal, 9);
		create9x9Recipe(new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.DARK.getMeta()), darkCrystal, 9);
		create9x9Recipe(new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.PURE.getMeta()), pureCrystal, 9);
		
		addShapedRecipe(new ItemStack(ModItems.wrench), new Object[] { "N N", " I ", " I ", 'N', dIronNugget, 'I', dIronIngot });
		addShapelessOreRecipe(new ItemStack(ModItems.guide, 1, GuideType.CRYSTAL.getMetadata()), new Object[] {Items.BOOK, "gemCrystal"});
		addShapelessRecipe(new ItemStack(ModItems.guide, 1, GuideType.ESTORAGE.getMetadata()), new Object[] {new ItemStack(ModItems.guide, 1, GuideType.CRYSTAL.getMetadata()), new ItemStack(ModBlocks.crystalPipe, 1, PipeType.ESTORAGE.getMeta())});
		final ItemStack machineFrame = new ItemStack(ModItems.machineFrame);
		addShapedRecipe(machineFrame, new Object[] { "NPN", "P P", "NPN", 'P', dIronPlate, 'N', dIronNugget});
		
		addShapedRecipe(new ItemStack(ModBlocks.crystal, 8, CrystalBlockType.BLUE_BRICK.getMeta()), new Object[] { "BBB", "BCB", "BBB", 'C', blueCrystal, 'B', Blocks.STONEBRICK });
		addShapedRecipe(new ItemStack(ModBlocks.crystal, 8, CrystalBlockType.RED_BRICK.getMeta()), new Object[] { "BBB", "BCB", "BBB", 'C', redCrystal, 'B', Blocks.STONEBRICK });
		addShapedRecipe(new ItemStack(ModBlocks.crystal, 8, CrystalBlockType.GREEN_BRICK.getMeta()), new Object[] { "BBB", "BCB", "BBB", 'C', greenCrystal, 'B', Blocks.STONEBRICK });
		addShapedRecipe(new ItemStack(ModBlocks.crystal, 8, CrystalBlockType.DARK_BRICK.getMeta()), new Object[] { "BBB", "BCB", "BBB", 'C', darkCrystal, 'B', Blocks.STONEBRICK });
		addShapedRecipe(new ItemStack(ModBlocks.crystal, 8, CrystalBlockType.PURE_BRICK.getMeta()), new Object[] { "BBB", "BCB", "BBB", 'C', pureCrystal, 'B', Blocks.STONEBRICK });

		create9x9Recipe(new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.BLUE.getMeta()), blueIngot, 9);
		create9x9Recipe(new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.RED.getMeta()), redIngot, 9);
		create9x9Recipe(new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.GREEN.getMeta()), greenIngot, 9);
		create9x9Recipe(new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.DARK.getMeta()), darkIngot, 9);
		create9x9Recipe(new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.PURE.getMeta()), pureIngot, 9);
		create9x9Recipe(new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.DARKIRON.getMeta()), dIronIngot, 9);

		//addShapelessRecipe(new ItemStack(ModBlocks.crystalIngot, 9, CrystalIngotBlockType.DARKIRON.getMeta()), new Object[] { new ItemStack(Blocks.IRON_BLOCK), darkIngot });
		//addShapelessRecipe(dIronIngot, new Object[] { new ItemStack(Items.IRON_INGOT), new ItemStack(ModItems.crystals, 1, CrystalType.DARK_SHARD.getMetadata()) });

		addShapedOreRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.PURE_SHARD.getMetadata()), new Object[]{" B ", "RQG", " D ", 'Q', "gemQuartz", 
			'B', new ItemStack(ModItems.crystals, 1, CrystalType.BLUE_SHARD.getMetadata()), 
			'R', new ItemStack(ModItems.crystals, 1, CrystalType.RED_SHARD.getMetadata()), 
			'G', new ItemStack(ModItems.crystals, 1, CrystalType.GREEN_SHARD.getMetadata()), 
			'D', new ItemStack(ModItems.crystals, 1, CrystalType.DARK_SHARD.getMetadata())});
		addShapedOreRecipe(pureCrystal, new Object[]{" B ", "RQG", " D ", 'Q', "blockQuartz", 'B', blueCrystal, 'R', redCrystal, 'G', greenCrystal, 'D', darkCrystal});

		addShapedOreRecipe(new ItemStack(ModBlocks.crystalGlass, 8, GlassType.BLUE.getMeta()), new Object[]{"GGG", "GCG", "GGG", 'G', "blockGlass", 'C', blueIngot});
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalGlass, 8, GlassType.RED.getMeta()), new Object[]{"GGG", "GCG", "GGG", 'G', "blockGlass", 'C', redIngot});
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalGlass, 8, GlassType.GREEN.getMeta()), new Object[]{"GGG", "GCG", "GGG", 'G', "blockGlass", 'C', greenIngot});
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalGlass, 8, GlassType.DARK.getMeta()), new Object[]{"GGG", "GCG", "GGG", 'G', "blockGlass", 'C', darkIngot});
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalGlass, 8, GlassType.PURE.getMeta()), new Object[]{"GGG", "GCG", "GGG", 'G', "blockGlass", 'C', pureIngot});

		
		//TOOLS
		ItemStack cShears = new ItemStack(ModItems.shears);
		ItemNBTHelper.setString(cShears, "Color", "blue");
		addShapedRecipe(cShears, new Object[] {" #", "# ", '#', blueIngot});
		cShears = new ItemStack(ModItems.shears);
		ItemNBTHelper.setString(cShears, "Color", "red");
		addShapedRecipe(cShears, new Object[] {" #", "# ", '#', redIngot});
		cShears = new ItemStack(ModItems.shears);
		ItemNBTHelper.setString(cShears, "Color", "green");
		addShapedRecipe(cShears, new Object[] {" #", "# ", '#', greenIngot});
		cShears = new ItemStack(ModItems.shears);
		ItemNBTHelper.setString(cShears, "Color", "dark");
		addShapedRecipe(cShears, new Object[] {" #", "# ", '#', darkIngot});
		cShears = new ItemStack(ModItems.shears);
		ItemNBTHelper.setString(cShears, "Color", "pure");
		addShapedRecipe(cShears, new Object[] {" #", "# ", '#', pureIngot});
		cShears = new ItemStack(ModItems.shears);
		ItemNBTHelper.setString(cShears, "Color", "darkIron");
		addShapedRecipe(cShears, new Object[] {" #", "# ", '#', dIronIngot});
		
		addShapedOreRecipe(ModItems.darkIronAxe, new Object[]{"XX", "X#", " #", 'X', dIronIngot, '#', "stickWood"});
		addShapedOreRecipe(ModItems.darkIronHoe, new Object[]{"XX", " #", " #", 'X', dIronIngot, '#', "stickWood"});
		addShapedOreRecipe(ModItems.darkIronShovel, new Object[]{"X", "#", "#", 'X', dIronIngot, '#', "stickWood"});
		addShapedOreRecipe(ModItems.darkIronSword, new Object[]{"X", "X", "#", 'X', dIronIngot, '#', "stickWood"});
		addShapedOreRecipe(ModItems.darkIronBow, new Object[]{" X#", "X #", " X#", 'X', dIronIngot, '#', "string"});

		addShapedRecipe(ModItems.darkIronHelmet, new Object[]{"XXX", "X X", 'X', dIronIngot});
		addShapedRecipe(ModItems.darkIronChestplate, new Object[]{"X X", "XXX", "XXX", 'X', dIronIngot});
		addShapedRecipe(ModItems.darkIronLeggings, new Object[]{"XXX", "X X", "X X", 'X', dIronIngot});
		addShapedRecipe(ModItems.darkIronBoots, new Object[]{"X X", "X X", 'X', dIronIngot});
		
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalWorkbench, 1, WorkbenchType.BLUE.getMeta()), new Object[]{"###", "#W#", "###", '#', blueIngot, 'W', "workbench"});
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalWorkbench, 1, WorkbenchType.RED.getMeta()), new Object[]{"###", "#W#", "###", '#', redIngot, 'W', "workbench"});
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalWorkbench, 1, WorkbenchType.GREEN.getMeta()), new Object[]{"###", "#W#", "###", '#', greenIngot, 'W', "workbench"});
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalWorkbench, 1, WorkbenchType.DARK.getMeta()), new Object[]{"###", "#W#", "###", '#', darkIngot, 'W', "workbench"});

		ItemStack pipeEStorage = new ItemStack(ModBlocks.crystalPipe, 1, PipeType.ESTORAGE.getMeta());
		
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalPipe, 8, PipeType.ITEM.getMeta()), new Object[]{"###", "NHN", "###", '#', dIronPlate, 'N', dIronNugget, 'H', "chestWood" });
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalPipe, 8, PipeType.FLUID.getMeta()), new Object[]{"###", "NHN", "###", '#', dIronPlate, 'N', dIronNugget, 'B', "bucket" });
				
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalPipe, 4, PipeType.ESTORAGE.getMeta()), new Object[]{" # ", "#I#", " # ", '#', "nuggetCrystal", 'I', new ItemStack(ModBlocks.crystalPipe, 1, PipeType.ITEM.getMeta())});

		ItemStack powerPipeCU = new ItemStack(ModBlocks.crystalPipe, 8, PipeType.POWERCU.getMeta());
		ItemStack tier0CU = powerPipeCU.copy();ItemNBTHelper.setInteger(tier0CU, "Tier", 0);
		ItemStack tier1CU = powerPipeCU.copy();ItemNBTHelper.setInteger(tier1CU, "Tier", 1);
		ItemStack tier2CU = powerPipeCU.copy();ItemNBTHelper.setInteger(tier2CU, "Tier", 2);
		ItemStack tier3CU = powerPipeCU.copy();ItemNBTHelper.setInteger(tier3CU, "Tier", 3);
		addShapedOreRecipe(tier0CU, new Object[]{"###", "NIN", "###", '#', dIronPlate, 'I', blueIngot, 'N', blueNugget });
		addShapedOreRecipe(tier1CU, new Object[]{" # ", "NPN", " # ", '#', redPlate, 'N', redNugget, 'P', ItemUtil.copy(tier0CU, 1)});
		addShapedOreRecipe(tier2CU, new Object[]{" # ", "NPN", " # ", '#', greenPlate, 'N', greenNugget, 'P', ItemUtil.copy(tier1CU, 1)});
		addShapedOreRecipe(tier3CU, new Object[]{" # ", "NPN", " # ", '#', darkPlate, 'N', darkNugget, 'P', ItemUtil.copy(tier2CU, 1)});
		
		ItemStack powerPipeRF = new ItemStack(ModBlocks.crystalPipe, 8, PipeType.POWERRF.getMeta());
		ItemStack tier0RF = powerPipeRF.copy();ItemNBTHelper.setInteger(tier0RF, "Tier", 0);
		ItemStack tier1RF = powerPipeRF.copy();ItemNBTHelper.setInteger(tier1RF, "Tier", 1);
		ItemStack tier2RF = powerPipeRF.copy();ItemNBTHelper.setInteger(tier2RF, "Tier", 2);
		ItemStack tier3RF = powerPipeRF.copy();ItemNBTHelper.setInteger(tier3RF, "Tier", 3);
		addShapedOreRecipe(tier0RF, new Object[]{"###", "NIN", "###", '#', dIronPlate, 'I', blueIngot, 'N', "dustRedstone" });
		addShapedOreRecipe(tier1RF, new Object[]{" # ", "NPN", " # ", '#', redPlate, 'N', "nuggetGold", 'P', ItemUtil.copy(tier0RF, 1)});
		addShapedOreRecipe(tier2RF, new Object[]{" # ", "NPN", " # ", '#', greenPlate, 'N', "ingotGold", 'P', ItemUtil.copy(tier1RF, 1)});
		addShapedOreRecipe(tier3RF, new Object[]{" # ", "NPN", " # ", '#', darkPlate, 'N', Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, 'P', ItemUtil.copy(tier2RF, 1)});

		
		addShapedOreRecipe(new ItemStack(ModItems.pipeFilter, 1, FilterType.NORMAL.ordinal()), new Object[]{"#P#", "PHP", "#P#", '#', "nuggetCrystal", 'P', "paper", 'H', Blocks.HOPPER});
		addShapelessRecipe(new ItemStack(ModItems.pipeFilter, 1, FilterType.MOD.ordinal()), new Object[]{new ItemStack(ModItems.pipeFilter, 1, FilterType.NORMAL.ordinal()), Items.BOOK});
		addShapelessOreRecipe(new ItemStack(ModItems.pipeFilter, 1, FilterType.CAMERA.ordinal()), new Object[]{new ItemStack(ModItems.pipeFilter, 1, FilterType.NORMAL.ordinal()), Blocks.REDSTONE_LAMP, "dustRedstone"});
		
		List<String> copyListTank = Lists.newArrayList();
		copyListTank.add("tankContents");
		
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalTank, 1, TankType.BLUE.getMeta()), new Object[]{"###", "G G", "###", '#', bluePlate, 'G', "paneGlass"});
		ModCrafting.addNBTRecipe(new ItemStack(ModBlocks.crystalTank, 1, TankType.RED.getMeta()), copyListTank, new Object[]{"N#N", "#T#", "N#N", '#', redPlate, 'N', redNugget, 'T', new ItemStack(ModBlocks.crystalTank, 1, TankType.BLUE.getMeta())});
		ModCrafting.addNBTRecipe(new ItemStack(ModBlocks.crystalTank, 1, TankType.GREEN.getMeta()), copyListTank, new Object[]{"N#N", "#T#", "N#N", '#', greenPlate, 'N', greenNugget, 'T', new ItemStack(ModBlocks.crystalTank, 1, TankType.RED.getMeta())});
		ModCrafting.addNBTRecipe(new ItemStack(ModBlocks.crystalTank, 1, TankType.DARK.getMeta()), copyListTank, new Object[]{"N#N", "#T#", "N#N", '#', darkPlate, 'N', darkNugget, 'T', new ItemStack(ModBlocks.crystalTank, 1, TankType.GREEN.getMeta())});
		ModCrafting.addNBTRecipe(new ItemStack(ModBlocks.crystalTank, 1, TankType.PURE.getMeta()), copyListTank, new Object[]{"N#N", "#T#", "N#N", '#', purePlate, 'N', pureNugget, 'T', new ItemStack(ModBlocks.crystalTank, 1, TankType.DARK.getMeta())});

		addShapedOreRecipe(ModBlocks.customSpawner, new Object[]{"BBB", "RDR", "BBB", 'B', Blocks.IRON_BARS, 'R', "rodBlaze", 'D', new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.DARKIRON.getMeta())});
		
		addShapedOreRecipe(ModBlocks.weather, new Object[]{"#S#", "CFB", "###", '#', dIronPlate, 'B', "bucket", 'S', Blocks.DAYLIGHT_DETECTOR, 'C', Items.CLOCK, 'F', machineFrame});

		addShapedOreRecipe(ModBlocks.hddInterface, new Object[]{"###", "#FH", "#P#", '#', dIronPlate, 'H', Blocks.TRIPWIRE_HOOK, 'P', pipeEStorage, 'F', machineFrame});
		addShapedOreRecipe(ModBlocks.externalInterface, new Object[]{"#S#", "#H#", "#P#", '#', dIronPlate, 'S', Blocks.STICKY_PISTON, 'H', "chest", 'P', pipeEStorage});

		addShapedOreRecipe(new ItemStack(ModBlocks.storagePanel, 1, PanelType.STORAGE.getMeta()), new Object[]{"###", "IGI", "#P#", '#', dIronPlate, 'G', "blockGlassBlack", 'I', "ingotCrystal", 'P', pipeEStorage});
		addShapelessOreRecipe(new ItemStack(ModBlocks.storagePanel, 1, PanelType.CRAFTING.getMeta()), new Object[]{new ItemStack(ModBlocks.storagePanel), "workbench"});
		addShapelessOreRecipe(new ItemStack(ModBlocks.storagePanel, 1, PanelType.DISPLAY.getMeta()), new Object[]{new ItemStack(ModBlocks.storagePanel), Items.COMPARATOR});
		addShapelessRecipe(new ItemStack(ModBlocks.storagePanel, 1, PanelType.MONITOR.getMeta()), new Object[]{ModBlocks.storagePanel, ModItems.craftingPattern});

		addShapelessOreRecipe(ModBlocks.wirelessPanel, new Object[]{new ItemStack(ModBlocks.storagePanel), new ItemStack(ModBlocks.wirelessPipe)});
		addShapedOreRecipe(ModItems.wirelessPanel, new Object[]{"E  ", "SP ", 'E', Items.ENDER_EYE, 'P', new ItemStack(ModBlocks.storagePanel), 'S', crystalRod});

		addShapedOreRecipe(ModBlocks.wirelessPipe, new Object[]{"#I#", "ICI", "#P#", '#', Items.ENDER_EYE, 'C', new ItemStack(ModBlocks.enderBuffer), 'I', pureIngot, 'P', pipeEStorage});
		addShapedOreRecipe(ModBlocks.crafter, new Object[]{"#W#", "WCW", "#P#", '#', dIronPlate, 'W', "workbench", 'C', "chest", 'P', pipeEStorage});
		addShapedOreRecipe(new ItemStack(ModBlocks.encoder, 1, EncoderType.NORMAL.getMeta()), new Object[]{"#P#", "IWI", "#I#", '#', dIronPlate, 'W', "workbench", 'I', dIronPlate, 'P', ModItems.craftingPattern});
		addShapelessRecipe(new ItemStack(ModBlocks.encoder, 1, EncoderType.PROCESSING.getMeta()), new Object[]{new ItemStack(ModBlocks.encoder, 1, EncoderType.NORMAL.getMeta())});

		
		addShapedOreRecipe(ItemHDD.getFromMeta(0), new Object[]{"#R#", "CIC", "#T#", '#', dIronIngot, 'R', "dustRedstone", 'I', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, 'C', blueIngot, 'T', Blocks.TRIPWIRE_HOOK});
		List<String> copyList = Lists.newArrayList();
		copyList.add(ItemHDD.NBT_ITEM_LIST);
		
		ItemStack hddPlate = new ItemStack(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
		
		ItemStack redHDD = ItemHDD.getFromMeta(1);
		ModCrafting.addNBTRecipe(redHDD, copyList, new Object[]{"CCC", "IHI", "CCC", 'I', hddPlate, 'C', redIngot, 'H', ItemHDD.getFromMeta(0)});
		ItemStack greenHDD = ItemHDD.getFromMeta(2);
		ModCrafting.addNBTRecipe(greenHDD, copyList, new Object[]{"CCC", "IHI", "CCC", 'I', hddPlate, 'C', greenIngot, 'H', ItemHDD.getFromMeta(1)});
		ItemStack darkHDD = ItemHDD.getFromMeta(3);
		ModCrafting.addNBTRecipe(darkHDD, copyList, new Object[]{"CCC", "IHI", "CCC", 'I', hddPlate, 'C', darkIngot, 'H', ItemHDD.getFromMeta(2)});
		ItemStack pureHDD = ItemHDD.getFromMeta(4);
		ModCrafting.addNBTRecipe(pureHDD, copyList, new Object[]{"CCC", "IHI", "CCC", 'I', hddPlate, 'C', pureIngot, 'H', ItemHDD.getFromMeta(3)});
		addShapedOreRecipe(new ItemStack(ModItems.craftingPattern), new Object[]{" # ", "#P#", " # ", '#', dIronNugget, 'P', "paper"});

		for(ItemStack cover : ItemPipeCover.coverRecipes.keySet()){
			ItemStack cover6 = cover.copy();
			cover6.stackSize = 6;
			addShapedOreRecipe(cover6, "s", "cn", 'c', ItemPipeCover.coverRecipes.get(cover), 's', "slimeball", 'n', "nuggetCrystal");
		}
		
		addShapedOreRecipe(ModBlocks.cubePortal, new Object[]{"#P#", "PEP", "#P#", '#', dIronPlate, 'E', "endereye", 'P', "enderpearl"});

		addShapedOreRecipe(new ItemStack(ModBlocks.elevator, 4), new Object[]{"#C#", "SGP", "#C#", '#', dIronPlate, 'G', "ingotGold", 'P', "piston", 'S', "pistonSticky", 'C', "ingotCrystal"});
		addShapedOreRecipe(ModBlocks.elevatorFloor, new Object[]{"#C#", "GSG", "#C#", '#', dIronPlate, 'S', "pistonSticky", 'C', "ingotCrystal", 'G', "ingotGold"});
		addShapedOreRecipe(ModBlocks.elevatorCaller, new Object[]{" E ", "BFB", " E ", 'E', "enderpearl", 'B', Blocks.STONE_BUTTON, 'F', ModBlocks.elevatorFloor});

		addShapedOreRecipe(ModBlocks.cauldron, new Object[]{"NCN", "NMN", "NCN", 'M', Items.CAULDRON, 'C', "gemCrystal", 'N', "shardCrystal"});

		addShapelessOreRecipe(new ItemStack(ModItems.miscCard, 1, ItemMiscCard.CardType.EPORTAL.getMetadata()), new Object[]{dIronPlate, "paper", "enderpearl"});

		addShapelessOreRecipe(new ItemStack(ModItems.miscCard, 1, ItemMiscCard.CardType.CUBE.getMetadata()), new Object[]{dIronPlate, "paper", Items.NAME_TAG});

		addShapelessRecipe(Items.SLIME_BALL, new Object[]{Items.WATER_BUCKET, Items.MILK_BUCKET});
		addShapedOreRecipe(Items.NAME_TAG, new Object[]{" PP", "SBI", " PP", 'S', "string", 'I', "ingotIron", 'P', "paper", 'B', "slimeball"});
		
		addShapedOreRecipe(ModItems.backpack, new Object[]{"LTL", "SCS", "L#L", 'S', Items.LEAD, 'T', Blocks.TRIPWIRE_HOOK, 'C', "chestWood", 'L', "leather", '#', "ingotCrystal"});

		addShapedOreRecipe(ModItems.telePearl, new Object[]{"#P#", "PEP", "#P#", '#', pureIngot, 'P', "nuggetCrystal", 'E', "endereye"});

		addShapedOreRecipe(new ItemStack(ModBlocks.darkIronRail, 16), new Object[] {"X X", "X#X", "X X", 'X', dIronIngot, '#', "stick"});
		
		addShapedRecipe(ModBlocks.enderBuffer, new Object[]{"ICI", "RFP", "ITI", 'I', dIronPlate, 'P', ItemUtil.copy(tier2CU, 1), 'R', ItemUtil.copy(tier2RF, 1), 'T', new ItemStack(ModBlocks.crystalTank, 1, TankType.GREEN.getMeta()), 'C', new ItemStack(ModBlocks.crystalChest, 1, CrystalChestType.DARKIRON.ordinal()), 'F', machineFrame});

		ItemStack engineTier0 = new ItemStack(ModBlocks.engine, 1, EngineType.FURNACE.getMeta()); ItemNBTHelper.setInteger(engineTier0, "Tier", 0);
		ItemStack engineTier1 = new ItemStack(ModBlocks.engine, 1, EngineType.FURNACE.getMeta()); ItemNBTHelper.setInteger(engineTier1, "Tier", 1);
		ItemStack engineTier2 = new ItemStack(ModBlocks.engine, 1, EngineType.FURNACE.getMeta()); ItemNBTHelper.setInteger(engineTier2, "Tier", 2);

		addShapedOreRecipe(engineTier0, new Object[]{"CCC", "IFI", "IPI", 'I', dIronPlate, 'F', Blocks.FURNACE, 'P', ItemUtil.copy(tier0CU, 1), 'C', "cobblestone"});
		addShapedRecipe(engineTier1, new Object[]{"EEE", "EPE", "EEE", 'E', engineTier0, 'P', ItemUtil.copy(tier2CU, 1)});
		addShapedRecipe(engineTier2, new Object[]{"EEE", "EPE", "EEE", 'E', engineTier1, 'P', ItemUtil.copy(tier3CU, 1)});

		ItemStack lavaEngineTier0 = new ItemStack(ModBlocks.engine, 1, EngineType.LAVA.getMeta()); ItemNBTHelper.setInteger(lavaEngineTier0, "Tier", 0);
		ItemStack lavaEngineTier1 = new ItemStack(ModBlocks.engine, 1, EngineType.LAVA.getMeta()); ItemNBTHelper.setInteger(lavaEngineTier1, "Tier", 1);
		ItemStack lavaEngineTier2 = new ItemStack(ModBlocks.engine, 1, EngineType.LAVA.getMeta()); ItemNBTHelper.setInteger(lavaEngineTier2, "Tier", 2);

		addShapedRecipe(lavaEngineTier0, new Object[]{"CCC", "LFL", "IPI", 'I', dIronPlate, 'L', Items.LAVA_BUCKET, 'P', ItemUtil.copy(tier0CU, 1), 'C', Blocks.NETHER_BRICK, 'F', machineFrame});
		addShapedRecipe(lavaEngineTier1, new Object[]{"EEE", "EPE", "EEE", 'E', lavaEngineTier0, 'P', ItemUtil.copy(tier2CU, 1)});
		addShapedRecipe(lavaEngineTier2, new Object[]{"EEE", "EPE", "EEE", 'E', lavaEngineTier1, 'P', ItemUtil.copy(tier3CU, 1)});
		
		
		addShapedRecipe(new ItemStack(ModBlocks.crystalMachine, 1, MachineType.FURNACE.getMeta()), new Object[]{"III", "IFI", "IPI", 'I', dIronPlate, 'F', Blocks.FURNACE, 'P', ItemUtil.copy(tier0CU, 1)});
		addShapedRecipe(new ItemStack(ModBlocks.crystalMachine, 1, MachineType.PRESS.getMeta()), new Object[]{"IPI", "I I", "ICI", 'I', dIronIngot, 'P', Blocks.PISTON, 'C', ItemUtil.copy(tier0CU, 1)});
		addShapedRecipe(new ItemStack(ModBlocks.crystalMachine, 1, MachineType.LIQUIDIZER.getMeta()), new Object[]{"III", "PFB", "ICI", 'I', dIronPlate, 'P', Blocks.PISTON, 'B', Items.BUCKET, 'C', ItemUtil.copy(tier0CU, 1), 'F', machineFrame});
		addShapedRecipe(new ItemStack(ModBlocks.crystalMachine, 1, MachineType.INFUSER.getMeta()), new Object[]{"ICI", "IFI", "IPI", 'I', dIronPlate, 'C', ModBlocks.cauldron, 'P', ItemUtil.copy(tier0CU, 1), 'F', machineFrame});

		
		addShapedRecipe(new ItemStack(ModBlocks.converter, 1, ConverterType.CU.getMeta()), new Object[]{"III", "RFC", "III", 'I', dIronPlate, 'C', ItemUtil.copy(tier0CU, 1), 'R', ItemUtil.copy(tier0RF, 1), 'F', machineFrame});
		addShapedRecipe(new ItemStack(ModBlocks.converter, 1, ConverterType.RF.getMeta()), new Object[]{"III", "CFR", "III", 'I', dIronPlate, 'C', ItemUtil.copy(tier0CU, 1), 'R', ItemUtil.copy(tier0RF, 1), 'F', machineFrame});

		CrystalChestType.registerBlocksAndRecipes(ModBlocks.crystalChest);
		CauldronRecipeManager.initRecipes();
		
		CrystalFurnaceManager.initRecipes();
		PressRecipeManager.initRecipes();
		LiquidizerRecipeManager.initRecipes();
		CrystalInfusionManager.initRecipes();
	}
	
	public static void addShapedRecipe(Item result, Object... recipe){ addShapedRecipe(new ItemStack(result), recipe); }
	public static void addShapedRecipe(Block result, Object... recipe){ addShapedRecipe(new ItemStack(result), recipe); }
	public static void addShapedRecipe(ItemStack output, Object... params){
		GameRegistry.addShapedRecipe(output, params);
	}
	
	public static void addShapelessRecipe(Item result, Object... recipe){ addShapelessRecipe(new ItemStack(result), recipe); }
	public static void addShapelessRecipe(Block result, Object... recipe){ addShapelessRecipe(new ItemStack(result), recipe); }
	public static void addShapelessRecipe(ItemStack output, Object... params){
		GameRegistry.addShapelessRecipe(output, params);
	}
	
	public static void addShapedOreRecipe(Item result, Object... recipe){ addShapedOreRecipe(new ItemStack(result), recipe); }
	public static void addShapedOreRecipe(Block result, Object... recipe){ addShapedOreRecipe(new ItemStack(result), recipe); }
	public static void addShapedOreRecipe(ItemStack result, Object... recipe){
		GameRegistry.addRecipe(new ShapedOreRecipe(result, recipe));
	}
	
	public static void addShapelessOreRecipe(Item result, Object... recipe){ addShapelessOreRecipe(new ItemStack(result), recipe); }
	public static void addShapelessOreRecipe(Block result, Object... recipe){ addShapelessOreRecipe(new ItemStack(result), recipe); }
	public static void addShapelessOreRecipe(ItemStack result, Object... recipe){
		GameRegistry.addRecipe(new ShapelessOreRecipe(result, recipe));
	}
	
	public static void create9x9Recipe(ItemStack output, ItemStack input, int reverseOut){
		addShapedRecipe(output, new Object[]{"###", "###", "###", '#', input});
		ItemStack copy = input.copy();
		copy.stackSize = reverseOut;
		addShapelessRecipe(copy, new Object[] { output });
	}
	
	public static void initOreDic(){
		oredict(Blocks.CRAFTING_TABLE, "workbench");
		
		for(CrystalChestType chest : CrystalChestType.values()){
			if(chest == CrystalChestType.WOOD)continue;
			ItemStack cStack = new ItemStack(ModBlocks.crystalChest, 1, chest.ordinal());
			oredict(cStack, "chest");
			oredict(cStack, "chestCrystal");
		}
		
		oredict(Items.PAPER, "paper");
		oredict(Items.BUCKET, "bucket");
		oredict(Items.ENDER_PEARL, "enderpearl");
		oredict(Items.ENDER_EYE, "endereye");
		oredict(Items.STRING, "string");
		oredict(Items.LEATHER, "leather");
		oredict(Items.SLIME_BALL, "slimeball");
		oredict(Items.BLAZE_ROD, "rodBlaze");

		oredict(Blocks.PISTON, "piston");
		oredict(Blocks.STICKY_PISTON, "pistonSticky");
		
		oredict(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.BLUE.getMeta()),  "oreCrystal");
		oredict(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.RED.getMeta()),  "oreCrystal");
		oredict(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.GREEN.getMeta()),  "oreCrystal");
		oredict(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.DARK.getMeta()),  "oreCrystal");
		oredictCrystal(CrystalType.BLUE_SHARD.getMetadata(), CrystalType.BLUE_NUGGET.getMetadata(), CrystalType.BLUE.getMetadata(), IngotType.BLUE.getMetadata(), CrystalBlockType.BLUE.getMeta(), CrystalIngotBlockType.BLUE.getMeta());
		oredictCrystal(CrystalType.RED_SHARD.getMetadata(), CrystalType.RED_NUGGET.getMetadata(), CrystalType.RED.getMetadata(), IngotType.RED.getMetadata(), CrystalBlockType.RED.getMeta(), CrystalIngotBlockType.RED.getMeta());
		oredictCrystal(CrystalType.GREEN_SHARD.getMetadata(), CrystalType.GREEN_NUGGET.getMetadata(), CrystalType.GREEN.getMetadata(), IngotType.GREEN.getMetadata(), CrystalBlockType.GREEN.getMeta(), CrystalIngotBlockType.GREEN.getMeta());
		oredictCrystal(CrystalType.DARK_SHARD.getMetadata(), CrystalType.DARK_NUGGET.getMetadata(), CrystalType.DARK.getMetadata(), IngotType.DARK.getMetadata(), CrystalBlockType.DARK.getMeta(), CrystalIngotBlockType.DARK.getMeta());

		oredict(new ItemStack(ModItems.ingots, 1, IngotType.DARK_IRON.getMetadata()), "ingotIronDark");
		oredict(new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.DARKIRON.getMeta()), "blockIronDark");
		oredict(new ItemStack(ModItems.crystals, 1, CrystalType.DIRON_NUGGET.getMetadata()), "nuggetIronDark");
		
		oredict(new ItemStack(ModItems.plates, 1, PlateType.BLUE.getMetadata()), "plateCrystal", "plateCrystalBlue");
		oredict(new ItemStack(ModItems.plates, 1, PlateType.RED.getMetadata()), "plateCrystal", "plateCrystalRed");
		oredict(new ItemStack(ModItems.plates, 1, PlateType.GREEN.getMetadata()), "plateCrystal", "plateCrystalGreen");
		oredict(new ItemStack(ModItems.plates, 1, PlateType.DARK.getMetadata()), "plateCrystal", "plateCrystalDark");
		oredict(new ItemStack(ModItems.plates, 1, PlateType.PURE.getMetadata()), "plateCrystal", "plateCrystalPure");

		oredict(new ItemStack(ModItems.plates, 1, PlateType.DARK_IRON.getMetadata()), "plateIronDark");
	}
	
	private static void oredictCrystal(int shard, int nugget, int crystal, int ingot, int block, int ingotBlock) {
		oredict(new ItemStack(ModItems.crystals, 1, shard), "shardCrystal");
	    oredict(new ItemStack(ModItems.crystals, 1, nugget), "nuggetCrystal");
	    oredict(new ItemStack(ModItems.crystals, 1, crystal), "gemCrystal");
	    oredict(new ItemStack(ModItems.ingots, 1, ingot),  "ingotCrystal");
	    oredict(new ItemStack(ModBlocks.crystal, 1, block),  "blockCrystal");
	    oredict(new ItemStack(ModBlocks.crystalIngot, 1, ingotBlock),  "blockIngotCrystal");
	}
	
	/* Helper functions */

	public static void oredict(Item item, String... name) {
		oredict(item, OreDictionary.WILDCARD_VALUE, name);
	}

	public static void oredict(Block block, String... name) {
	    oredict(block, OreDictionary.WILDCARD_VALUE, name);
	}

	public static void oredict(Item item, int meta, String... name) {
	    oredict(new ItemStack(item, 1, meta), name);
	}

	public static void oredict(Block block, int meta, String... name) {
	    oredict(new ItemStack(block, 1, meta), name);
	}

	public static void oredict(ItemStack stack, String... names) {
		if(stack != null && stack.getItem() != null) {
			for(String name : names) {
				OreDictionary.registerOre(name, stack);
			}
		}
	}
	
	public static ShapedNBTCopy addNBTRecipe(ItemStack stack, List<String> tags, Object... recipeComponents)
    {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;

        if (recipeComponents[i] instanceof String[])
        {
            String[] astring = (String[])((String[])recipeComponents[i++]);

            for (int l = 0; l < astring.length; ++l)
            {
                String s2 = astring[l];
                ++k;
                j = s2.length();
                s = s + s2;
            }
        }
        else
        {
            while (recipeComponents[i] instanceof String)
            {
                String s1 = (String)recipeComponents[i++];
                ++k;
                j = s1.length();
                s = s + s1;
            }
        }

        Map<Character, ItemStack> map;

        for (map = Maps.<Character, ItemStack>newHashMap(); i < recipeComponents.length; i += 2)
        {
            Character character = (Character)recipeComponents[i];
            ItemStack itemstack = null;

            if (recipeComponents[i + 1] instanceof Item)
            {
                itemstack = new ItemStack((Item)recipeComponents[i + 1]);
            }
            else if (recipeComponents[i + 1] instanceof Block)
            {
                itemstack = new ItemStack((Block)recipeComponents[i + 1], 1, 32767);
            }
            else if (recipeComponents[i + 1] instanceof ItemStack)
            {
                itemstack = (ItemStack)recipeComponents[i + 1];
            }

            map.put(character, itemstack);
        }

        ItemStack[] aitemstack = new ItemStack[j * k];

        for (int i1 = 0; i1 < j * k; ++i1)
        {
            char c0 = s.charAt(i1);

            if (map.containsKey(Character.valueOf(c0)))
            {
                aitemstack[i1] = ((ItemStack)map.get(Character.valueOf(c0))).copy();
            }
            else
            {
                aitemstack[i1] = null;
            }
        }

        ShapedNBTCopy shapedrecipes = new ShapedNBTCopy(j, k, aitemstack, stack, tags);
        GameRegistry.addRecipe(shapedrecipes);
        return shapedrecipes;
    }
	
	@SuppressWarnings("unchecked")
	public static void addSlabToBlocks(){
		List<IRecipe> recipeList = new ArrayList<IRecipe>(CraftingManager.getInstance().getRecipeList());
		for(IRecipe recipe : recipeList) {
			if(recipe instanceof ShapedRecipes || recipe instanceof ShapedOreRecipe) {
				Object[] recipeItems;
				if(recipe instanceof ShapedRecipes)
					recipeItems = ((ShapedRecipes) recipe).recipeItems;
				else recipeItems = ((ShapedOreRecipe) recipe).getInput();

				ItemStack output = recipe.getRecipeOutput();
				if(output != null && output.stackSize == 6) {
					Item outputItem = output.getItem();
					Block outputBlock = Block.getBlockFromItem(outputItem);
					if(outputBlock != null && outputBlock instanceof BlockSlab) {
						ItemStack outStack = null;

						for (Object recipeItem2 : recipeItems) {
							Object recipeItem = recipeItem2;
							if(recipeItem instanceof List) {
								List<ItemStack> ores = (List<ItemStack>) recipeItem;
								if(!ores.isEmpty())
									recipeItem = ores.get(0);
							}

							if(recipeItem != null) {
								outStack = (ItemStack) recipeItem;
								break;
							}
						}

						ItemStack outCopy = outStack.copy();
						if(outCopy.getItemDamage() == OreDictionary.WILDCARD_VALUE)
							outCopy.setItemDamage(0);

						addShapedRecipe(outCopy,
								"B", "B",
								'B', output.copy());
					}
				}
			}
		}
	}
	
}
