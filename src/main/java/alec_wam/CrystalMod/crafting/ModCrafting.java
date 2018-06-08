package alec_wam.CrystalMod.crafting;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPED;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.BlockCompressed.CompressedBlockType;
import alec_wam.CrystalMod.blocks.BlockCrystal.CrystalBlockType;
import alec_wam.CrystalMod.blocks.BlockCrystalIngot.CrystalIngotBlockType;
import alec_wam.CrystalMod.blocks.BlockCrystalLight.LightBlockType;
import alec_wam.CrystalMod.blocks.BlockCrystalLog.WoodType;
import alec_wam.CrystalMod.blocks.BlockCrystalOre.CrystalOreType;
import alec_wam.CrystalMod.blocks.BlockDecorative.DecorativeBlockType;
import alec_wam.CrystalMod.blocks.BlockFallingCompressed.FallingCompressedBlockType;
import alec_wam.CrystalMod.blocks.BlockMetalBars.EnumMetalBarType;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalPlant.PlantType;
import alec_wam.CrystalMod.blocks.crops.BlockNormalSapling.SaplingType;
import alec_wam.CrystalMod.blocks.crops.ItemCorn.CornItemType;
import alec_wam.CrystalMod.blocks.crops.bamboo.ItemWrappedFood;
import alec_wam.CrystalMod.blocks.crops.bamboo.ItemWrappedFood.WrappedFoodType;
import alec_wam.CrystalMod.blocks.crops.material.ModCrops;
import alec_wam.CrystalMod.blocks.crystexium.CrystexiumBlock.CrystexiumBlockType;
import alec_wam.CrystalMod.blocks.decorative.BlockBetterRoses.RoseType;
import alec_wam.CrystalMod.blocks.decorative.BlockFancyGlowstone.GlowstoneType;
import alec_wam.CrystalMod.blocks.decorative.BlockFancyLadder.FancyLadderType;
import alec_wam.CrystalMod.blocks.decorative.BlockFancyLadder2.FancyLadderType2;
import alec_wam.CrystalMod.blocks.decorative.BlockFancyObsidian.ObsidianType;
import alec_wam.CrystalMod.blocks.decorative.BlockFancyPumpkin.PumpkinType;
import alec_wam.CrystalMod.blocks.decorative.BlockFancySeaLantern.SeaLanternType;
import alec_wam.CrystalMod.blocks.decorative.BlockOctagonalBricks.OctagonBrickType;
import alec_wam.CrystalMod.blocks.decorative.tiles.BlockBasicTiles.BasicTileType;
import alec_wam.CrystalMod.blocks.decorative.tiles.BlockBasicTiles2.BasicTileType2;
import alec_wam.CrystalMod.blocks.decorative.tiles.BlockCrystalTiles.CrystalTileType;
import alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass.GlassType;
import alec_wam.CrystalMod.crafting.recipes.ChisledBlockRecipe;
import alec_wam.CrystalMod.crafting.recipes.CustomToolRepairRecipe;
import alec_wam.CrystalMod.crafting.recipes.RecipeSuperTorchAdd;
import alec_wam.CrystalMod.crafting.recipes.ShapedNBTCopy;
import alec_wam.CrystalMod.crafting.recipes.ShapedOreRecipeNBT;
import alec_wam.CrystalMod.crafting.recipes.ShapedRecipeNBT;
import alec_wam.CrystalMod.crafting.recipes.ShapelessRecipeNBT;
import alec_wam.CrystalMod.entities.accessories.WolfAccessories.WolfArmor;
import alec_wam.CrystalMod.entities.minecarts.chests.wireless.RecipeWirelessChestMinecart;
import alec_wam.CrystalMod.entities.minions.ItemMinion;
import alec_wam.CrystalMod.entities.minions.MinionType;
import alec_wam.CrystalMod.entities.misc.EntityCustomBoat;
import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.integration.baubles.BaublesIntegration;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ItemCrystalSap.SapType;
import alec_wam.CrystalMod.items.ItemCursedBone.BoneType;
import alec_wam.CrystalMod.items.ItemIngot.IngotType;
import alec_wam.CrystalMod.items.ItemMachineFrame.FrameType;
import alec_wam.CrystalMod.items.ItemMetalPlate.PlateType;
import alec_wam.CrystalMod.items.ItemMiscFood.FoodType;
import alec_wam.CrystalMod.items.crystex.ItemCrystex.CrystexItemType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.guide.ItemCrystalGuide.GuideType;
import alec_wam.CrystalMod.items.tools.ItemToolParts.PartType;
import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.bat.BatHelper;
import alec_wam.CrystalMod.items.tools.bat.RecipeBatUpgrade;
import alec_wam.CrystalMod.items.tools.blowdart.ItemDart.DartType;
import alec_wam.CrystalMod.tiles.cases.BlockCase.EnumCaseType;
import alec_wam.CrystalMod.tiles.cauldron.CauldronRecipeManager;
import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestHelper;
import alec_wam.CrystalMod.tiles.chest.wooden.WoodenCrystalChestType;
import alec_wam.CrystalMod.tiles.cluster.BlockCrystalCluster;
import alec_wam.CrystalMod.tiles.cluster.BlockCrystalCluster.EnumClusterType;
import alec_wam.CrystalMod.tiles.cluster.TileCrystalCluster.ClusterData;
import alec_wam.CrystalMod.tiles.crate.BlockCrate.CrateType;
import alec_wam.CrystalMod.tiles.darkinfection.BlockInfected.InfectedBlockType;
import alec_wam.CrystalMod.tiles.explosives.remover.BlockRemoverExplosion.RemoverType;
import alec_wam.CrystalMod.tiles.fusion.ModFusionRecipes;
import alec_wam.CrystalMod.tiles.lamps.BlockAdvancedLamp.LampType;
import alec_wam.CrystalMod.tiles.machine.BlockMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCrystalMachine.MachineType;
import alec_wam.CrystalMod.tiles.machine.crafting.furnace.CrystalFurnaceManager;
import alec_wam.CrystalMod.tiles.machine.crafting.grinder.GrinderManager;
import alec_wam.CrystalMod.tiles.machine.crafting.infuser.CrystalInfusionManager;
import alec_wam.CrystalMod.tiles.machine.crafting.liquidizer.LiquidizerRecipeManager;
import alec_wam.CrystalMod.tiles.machine.crafting.press.PressRecipeManager;
import alec_wam.CrystalMod.tiles.machine.dna.ItemDNA.DNAItemType;
import alec_wam.CrystalMod.tiles.machine.elevator.ItemMiscCard;
import alec_wam.CrystalMod.tiles.machine.inventory.charger.BlockInventoryCharger.ChargerBlockType;
import alec_wam.CrystalMod.tiles.machine.power.battery.BlockBattery.BatteryType;
import alec_wam.CrystalMod.tiles.machine.power.converter.BlockPowerConverter.ConverterType;
import alec_wam.CrystalMod.tiles.machine.power.engine.BlockEngine.EngineType;
import alec_wam.CrystalMod.tiles.machine.power.redstonereactor.ItemReactorUpgrade.UpgradeType;
import alec_wam.CrystalMod.tiles.machine.specialengines.BlockSpecialEngine.SpecialEngineType;
import alec_wam.CrystalMod.tiles.machine.specialengines.ItemEngineCore.EngineCoreType;
import alec_wam.CrystalMod.tiles.machine.worksite.BlockWorksite.WorksiteType;
import alec_wam.CrystalMod.tiles.machine.worksite.WorksiteUpgrade;
import alec_wam.CrystalMod.tiles.pipes.BlockPipe.PipeType;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentUtil;
import alec_wam.CrystalMod.tiles.pipes.attachments.ItemPipeAttachment;
import alec_wam.CrystalMod.tiles.pipes.covers.RecipePipeCover;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.BlockPatternEncoder.EncoderType;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.BlockPanel.PanelType;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.ItemHDD;
import alec_wam.CrystalMod.tiles.pipes.item.filters.ItemPipeFilter.FilterType;
import alec_wam.CrystalMod.tiles.tank.BlockTank.TankType;
import alec_wam.CrystalMod.tiles.workbench.BlockCrystalWorkbench.WorkbenchType;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.TimeUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStone;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ModCrafting {

	public static void preInit(){
		initOreDic();
	}
	
	public static void init() {
		GameRegistry.addRecipe(new RecipeBatUpgrade());
		GameRegistry.addRecipe(new RecipeSuperTorchAdd());
		GameRegistry.addRecipe(new RecipeWirelessChestMinecart());
		GameRegistry.addRecipe(new CustomToolRepairRecipe());
		GameRegistry.addRecipe(new ChisledBlockRecipe());
		GameRegistry.addRecipe(new RecipePipeCover());
		BatHelper.addBatCrafting();
		
		ItemStack blueCrystal = new ItemStack(ModItems.crystals, 1, CrystalType.BLUE.getMetadata());
		ItemStack blueIngot = new ItemStack(ModItems.ingots, 1, IngotType.BLUE.getMetadata());
		ItemStack blueShard = new ItemStack(ModItems.crystals, 1, CrystalType.BLUE_SHARD.getMetadata());
		ItemStack blueNugget = new ItemStack(ModItems.crystals, 1, CrystalType.BLUE_NUGGET.getMetadata());
		ItemStack bluePlate = new ItemStack(ModItems.plates, 1, PlateType.BLUE.getMetadata());
		
		ItemStack redCrystal = new ItemStack(ModItems.crystals, 1, CrystalType.RED.getMetadata());
		ItemStack redIngot = new ItemStack(ModItems.ingots, 1, IngotType.RED.getMetadata());
		ItemStack redShard = new ItemStack(ModItems.crystals, 1, CrystalType.RED_SHARD.getMetadata());
		ItemStack redNugget = new ItemStack(ModItems.crystals, 1, CrystalType.RED_NUGGET.getMetadata());
		ItemStack redPlate = new ItemStack(ModItems.plates, 1, PlateType.RED.getMetadata());
		
		ItemStack greenCrystal = new ItemStack(ModItems.crystals, 1, CrystalType.GREEN.getMetadata());
		ItemStack greenIngot = new ItemStack(ModItems.ingots, 1, IngotType.GREEN.getMetadata());
		ItemStack greenShard = new ItemStack(ModItems.crystals, 1, CrystalType.GREEN_SHARD.getMetadata());
		ItemStack greenNugget = new ItemStack(ModItems.crystals, 1, CrystalType.GREEN_NUGGET.getMetadata());
		ItemStack greenPlate = new ItemStack(ModItems.plates, 1, PlateType.GREEN.getMetadata());
		
		ItemStack darkCrystal = new ItemStack(ModItems.crystals, 1, CrystalType.DARK.getMetadata());
		ItemStack darkIngot = new ItemStack(ModItems.ingots, 1, IngotType.DARK.getMetadata());
		ItemStack darkShard = new ItemStack(ModItems.crystals, 1, CrystalType.DARK_SHARD.getMetadata());
		ItemStack darkNugget = new ItemStack(ModItems.crystals, 1, CrystalType.DARK_NUGGET.getMetadata());
		ItemStack darkPlate = new ItemStack(ModItems.plates, 1, PlateType.DARK.getMetadata());
		ItemStack darkBlock = new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.DARK.getMeta());
		
		ItemStack pureCrystal = new ItemStack(ModItems.crystals, 1, CrystalType.PURE.getMetadata());
		ItemStack pureIngot = new ItemStack(ModItems.ingots, 1, IngotType.PURE.getMetadata());
		ItemStack pureShard = new ItemStack(ModItems.crystals, 1, CrystalType.PURE_SHARD.getMetadata());
		ItemStack pureNugget = new ItemStack(ModItems.crystals, 1, CrystalType.PURE_NUGGET.getMetadata());
		ItemStack purePlate = new ItemStack(ModItems.plates, 1, PlateType.PURE.getMetadata());
		ItemStack pureBlock = new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.PURE.getMeta());
		
		ItemStack dIronIngot = new ItemStack(ModItems.ingots, 1, IngotType.DARK_IRON.getMetadata());
		ItemStack dIronNugget = new ItemStack(ModItems.crystals, 1, CrystalType.DIRON_NUGGET.getMetadata());
		ItemStack dIronPlate = new ItemStack(ModItems.plates, 1, PlateType.DARK_IRON.getMetadata());
		ItemStack dIronBlock = new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.DARKIRON.getMeta());
		
		ItemStack crystalRod = new ItemStack(ModItems.toolParts);
    	ItemNBTHelper.setString(crystalRod, "Type", PartType.ROD.getName());
		
    	
    	//Pipes
    	ItemStack itemPipe = new ItemStack(ModBlocks.crystalPipe, 1, PipeType.ITEM.getMeta());
    	ItemStack fluidPipe = new ItemStack(ModBlocks.crystalPipe, 1, PipeType.FLUID.getMeta());

		GameRegistry.addSmelting(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.BLUE.getMeta()), blueCrystal, 1.0F);
		GameRegistry.addSmelting(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.RED.getMeta()), redCrystal, 1.0F);
		GameRegistry.addSmelting(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.GREEN.getMeta()), greenCrystal, 1.0F);
		GameRegistry.addSmelting(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.DARK.getMeta()), darkCrystal, 1.0F);
		
		GameRegistry.addSmelting(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.BLUE_NETHER.getMeta()), blueCrystal, 1.0F);
		GameRegistry.addSmelting(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.RED_NETHER.getMeta()), redCrystal, 1.0F);
		GameRegistry.addSmelting(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.GREEN_NETHER.getMeta()), greenCrystal, 1.0F);
		GameRegistry.addSmelting(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.DARK_NETHER.getMeta()), darkCrystal, 1.0F);
		
		GameRegistry.addSmelting(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.BLUE_END.getMeta()), blueCrystal, 1.0F);
		GameRegistry.addSmelting(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.RED_END.getMeta()), redCrystal, 1.0F);
		GameRegistry.addSmelting(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.GREEN_END.getMeta()), greenCrystal, 1.0F);
		GameRegistry.addSmelting(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.DARK_END.getMeta()), darkCrystal, 1.0F);
		
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

		if(Config.dyeFromCoral){
			for(EnumDyeColor color : EnumDyeColor.values()){
				GameRegistry.addSmelting(new ItemStack(ModBlocks.coral, 1, color.getMetadata()), new ItemStack(Items.DYE, 1, color.getDyeDamage()), 0.5F);
			}
		}
		
		addShapedRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.BLUE_SHARD.getMetadata()), "RRR", 'R', ModItems.crystalReedsBlue);
		addShapedRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.RED_SHARD.getMetadata()), "RRR", 'R', ModItems.crystalReedsRed);
		addShapedRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.GREEN_SHARD.getMetadata()), "RRR", 'R', ModItems.crystalReedsGreen);
		addShapedRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.DARK_SHARD.getMetadata()), "RRR", 'R', ModItems.crystalReedsDark);
		
		addShapelessRecipe(new ItemStack(ModItems.corn, 2, CornItemType.KERNELS.getMetadata()), new Object[]{new ItemStack(ModItems.corn, 1, CornItemType.CORN.getMetadata())});
		GameRegistry.addSmelting(new ItemStack(ModItems.corn, 1, CornItemType.KERNELS.getMetadata()), new ItemStack(ModItems.miscFood, 1, FoodType.POPCORN.getMetadata()), 0.1F);
		GameRegistry.addSmelting(new ItemStack(ModItems.corn, 1, CornItemType.CORN.getMetadata()), new ItemStack(ModItems.miscFood, 1, FoodType.CORN_COB.getMetadata()), 0.1F);
		GameRegistry.addSmelting(new ItemStack(ModItems.miscFood, 1, FoodType.WHITE_FISH_RAW.getMetadata()), new ItemStack(ModItems.miscFood, 1, FoodType.WHITE_FISH_COOKED.getMetadata()), 0.5F);
		GameRegistry.addSmelting(new ItemStack(ModItems.miscFood, 1, FoodType.SEAWEED.getMetadata()), new ItemStack(ModItems.miscFood, 1, FoodType.SEAWEED_COOKED.getMetadata()), 0.5F);
		GameRegistry.addSmelting(new ItemStack(ModItems.miscFood, 1, FoodType.KELP.getMetadata()), new ItemStack(ModItems.miscFood, 1, FoodType.KELP_COOKED.getMetadata()), 0.5F);
		GameRegistry.addSmelting(new ItemStack(ModItems.miscFood, 1, FoodType.YELLOW_KELP.getMetadata()), new ItemStack(ModItems.miscFood, 1, FoodType.YELLOW_KELP_COOKED.getMetadata()), 0.5F);
		addShapelessRecipe(new ItemStack(ModItems.miscFood, 1, FoodType.SUPER_MUSHROOM_STEW.getMetadata()), new Object[] {Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM, ModBlocks.crysineMushroom, Items.BOWL});
		BrewingRecipeRegistry.addRecipe(new ItemStack(Items.POTIONITEM), new ItemStack(ModBlocks.crysineMushroom), PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.LONG_FIRE_RESISTANCE));
		
		for(WrappedFoodType foodType : WrappedFoodType.values()){
			if(!ItemStackTools.isValid(foodType.getFoodStack()))continue;
			ItemStack wrapped = new ItemStack(ModItems.wrappedFood);
			ItemWrappedFood.setFood(wrapped, foodType);
			addShapelessRecipe(wrapped, new Object[]{foodType.getFoodStack(), new ItemStack(ModItems.miscFood, 1, FoodType.EUCALYPTUS.getMetadata())});
		}
		
		addShapelessRecipe(new ItemStack(ModItems.cursedBone, 3, BoneType.BONEMEAL.getMetadata()), new Object[]{new ItemStack(ModItems.cursedBone, 1, BoneType.BONE.getMetadata())});
		addShapedOreRecipe(new ItemStack(Blocks.SOUL_SAND, 3), new Object[]{"SS ", "SX ", 'S', "sand", 'X', new ItemStack(ModItems.cursedBone, 1, BoneType.BONE.getMetadata())});

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
		
		create9x9Recipe(new ItemStack(ModBlocks.compressed, 1, CompressedBlockType.FLINT.getMeta()), new ItemStack(Items.FLINT), 9);
		create9x9Recipe(new ItemStack(ModBlocks.compressed, 1, CompressedBlockType.CHARCOAL.getMeta()), new ItemStack(Items.COAL, 1, 1), 9);
		create9x9Recipe(new ItemStack(ModBlocks.fallingCompressed, 1, FallingCompressedBlockType.GUNPOWDER.getMeta()), new ItemStack(Items.GUNPOWDER), 9);
		create9x9Recipe(new ItemStack(ModBlocks.fallingCompressed, 1, FallingCompressedBlockType.SUGAR.getMeta()), new ItemStack(Items.SUGAR), 9);
		create9x9Recipe(new ItemStack(ModBlocks.blazeRodBlock), new ItemStack(Items.BLAZE_ROD), 9);

		addShapedRecipe(new ItemStack(ModBlocks.crystalLight, 4, LightBlockType.BLUE.getMeta()), new Object[] { "N N", "CIC", "N N", 'N', pureNugget, 'I', dIronBlock, 'C', blueIngot});
		addShapedRecipe(new ItemStack(ModBlocks.crystalLight, 4, LightBlockType.RED.getMeta()), new Object[] { "N N", "CIC", "N N", 'N', pureNugget, 'I', dIronBlock, 'C', redIngot});
		addShapedRecipe(new ItemStack(ModBlocks.crystalLight, 4, LightBlockType.GREEN.getMeta()), new Object[] { "N N", "CIC", "N N", 'N', pureNugget, 'I', dIronBlock, 'C', greenIngot});
		addShapedRecipe(new ItemStack(ModBlocks.crystalLight, 4, LightBlockType.DARK.getMeta()), new Object[] { "N N", "CIC", "N N", 'N', pureNugget, 'I', dIronBlock, 'C', darkIngot});
		addShapedRecipe(new ItemStack(ModBlocks.crystalLight, 4, LightBlockType.PURE.getMeta()), new Object[] { "N N", "CIC", "N N", 'N', pureNugget, 'I', dIronBlock, 'C', pureIngot});
		addShapedRecipe(new ItemStack(ModBlocks.crystalLight, 8, LightBlockType.INFINITE.getMeta()), new Object[] { "II", "IB", 'I', pureIngot, 'B', new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.PURE_BRICK.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.crystalLight, 8, LightBlockType.SPIRAL_DARK.getMeta()), new Object[] { "NN ", "NBD", " DD", 'N', darkNugget, 'B', new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.DARK_BRICK.getMeta()), 'D', dIronNugget});
		addShapedRecipe(new ItemStack(ModBlocks.crystalLight, 8, LightBlockType.SPIRAL_PURE.getMeta()), new Object[] { "NN ", "NBD", " DD", 'N', pureNugget, 'B', new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.PURE_BRICK.getMeta()), 'D', dIronNugget});

		//More Expensive than using the Liguidizer and Infuser
		//addShapedOreRecipe(dIronIngot, " N ", "NIN", " N ", 'N', darkNugget, 'I', "ingotIron");
		
		addShapedRecipe(new ItemStack(ModItems.wrench), new Object[] { "N N", " I ", " I ", 'N', dIronNugget, 'I', dIronIngot });
		addShapelessOreRecipe(new ItemStack(ModItems.guide, 1, GuideType.CRYSTAL.getMetadata()), new Object[] {Items.BOOK, "gemCrystal"});
		addShapelessRecipe(new ItemStack(ModItems.guide, 1, GuideType.ESTORAGE.getMetadata()), new Object[] {new ItemStack(ModItems.guide, 1, GuideType.CRYSTAL.getMetadata()), new ItemStack(ModBlocks.crystalPipe, 1, PipeType.ESTORAGE.getMeta())});
		final ItemStack machineFrame = new ItemStack(ModItems.machineFrame, 1, FrameType.BASIC.getMetadata());
		final ItemStack machineFrameEnder = new ItemStack(ModItems.machineFrame, 1, FrameType.ENDER.getMetadata());
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
		
		addShapedRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.BLUE_SHARD.getMetadata()), new Object[] {"##", "##", '#', new ItemStack(ModItems.crystalSap, 1, SapType.BLUE.getMetadata())});
		addShapedRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.RED_SHARD.getMetadata()), new Object[] {"##", "##", '#', new ItemStack(ModItems.crystalSap, 1, SapType.RED.getMetadata())});
		addShapedRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.GREEN_SHARD.getMetadata()), new Object[] {"##", "##", '#', new ItemStack(ModItems.crystalSap, 1, SapType.GREEN.getMetadata())});
		addShapedRecipe(new ItemStack(ModItems.crystals, 1, CrystalType.DARK_SHARD.getMetadata()), new Object[] {"##", "##", '#', new ItemStack(ModItems.crystalSap, 1, SapType.DARK.getMetadata())});
		
		for(PlantType type : PlantType.values())addShapelessOreRecipe(new ItemStack(ModItems.glowBerry, 1, type.getMeta()), new Object[]{new ItemStack(ModItems.crystalBerry, 1, type.getMeta()), "dustGlowstone"});
		
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalGlass, 8, GlassType.BLUE.getMeta()), new Object[]{"GGG", "GCG", "GGG", 'G', "blockGlass", 'C', blueIngot});
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalGlass, 8, GlassType.RED.getMeta()), new Object[]{"GGG", "GCG", "GGG", 'G', "blockGlass", 'C', redIngot});
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalGlass, 8, GlassType.GREEN.getMeta()), new Object[]{"GGG", "GCG", "GGG", 'G', "blockGlass", 'C', greenIngot});
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalGlass, 8, GlassType.DARK.getMeta()), new Object[]{"GGG", "GCG", "GGG", 'G', "blockGlass", 'C', darkIngot});
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalGlass, 8, GlassType.PURE.getMeta()), new Object[]{"GGG", "GCG", "GGG", 'G', "blockGlass", 'C', pureIngot});
		
		addShapedRecipe(new ItemStack(ModBlocks.crystalGlassPainted, 8, GlassType.BLUE.getMeta()), new Object[]{"GGG", "GSG", "GGG", 'G', new ItemStack(ModBlocks.crystalGlass, 1, GlassType.BLUE.getMeta()), 'S', new ItemStack(ModItems.crystalSap, 1, SapType.BLUE.getMetadata())});
		addShapedRecipe(new ItemStack(ModBlocks.crystalGlassPainted, 8, GlassType.RED.getMeta()), new Object[]{"GGG", "GSG", "GGG", 'G', new ItemStack(ModBlocks.crystalGlass, 1, GlassType.RED.getMeta()), 'S', new ItemStack(ModItems.crystalSap, 1, SapType.RED.getMetadata())});
		addShapedRecipe(new ItemStack(ModBlocks.crystalGlassPainted, 8, GlassType.GREEN.getMeta()), new Object[]{"GGG", "GSG", "GGG", 'G', new ItemStack(ModBlocks.crystalGlass, 1, GlassType.GREEN.getMeta()), 'S', new ItemStack(ModItems.crystalSap, 1, SapType.GREEN.getMetadata())});
		addShapedRecipe(new ItemStack(ModBlocks.crystalGlassPainted, 8, GlassType.DARK.getMeta()), new Object[]{"GGG", "GSG", "GGG", 'G', new ItemStack(ModBlocks.crystalGlass, 1, GlassType.DARK.getMeta()), 'S', new ItemStack(ModItems.crystalSap, 1, SapType.DARK.getMetadata())});
		
		addShapedRecipe(new ItemStack(ModBlocks.metalBars, 16, EnumMetalBarType.DARK_IRON.getMeta()), new Object[]{"###", "###", '#', dIronIngot});
		addShapedRecipe(new ItemStack(ModBlocks.metalBars, 16, EnumMetalBarType.BLUE.getMeta()), new Object[]{"###", "###", '#', blueIngot});
		addShapedRecipe(new ItemStack(ModBlocks.metalBars, 16, EnumMetalBarType.RED.getMeta()), new Object[]{"###", "###", '#', redIngot});
		addShapedRecipe(new ItemStack(ModBlocks.metalBars, 16, EnumMetalBarType.GREEN.getMeta()), new Object[]{"###", "###", '#', greenIngot});
		addShapedRecipe(new ItemStack(ModBlocks.metalBars, 16, EnumMetalBarType.DARK.getMeta()), new Object[]{"###", "###", '#', darkIngot});
		addShapedRecipe(new ItemStack(ModBlocks.metalBars, 16, EnumMetalBarType.PURE.getMeta()), new Object[]{"###", "###", '#', pureIngot});
		
		for(GlassType type : GlassType.values()){
			addShapedRecipe(new ItemStack(ModBlocks.crystalGlassPane, 16, type.getMeta()), new Object[]{"###", "###", '#', new ItemStack(ModBlocks.crystalGlass, 1, type.getMeta())});
		}
		
		for(WoodType type : WoodType.values()){
			addShapelessRecipe(new ItemStack(ModBlocks.crystalPlanks, 4, type.getMeta()), new Object[]{new ItemStack(ModBlocks.crystalLog, 1, type.getMeta())});
			addShapedOreRecipe(new ItemStack(ModBlocks.ladder, 4, type.getMeta()), new Object[]{"S S", "S#S", "S S", 'S', "stickWood", '#', new ItemStack(ModBlocks.crystalPlanks, 1, type.getMeta())});
		}
		
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyLadders, 4, FancyLadderType.WIDE.getMeta()), new Object[]{"S S", "S#S", "S S", 'S', "stickWood", '#', Blocks.LADDER});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyLadders, 4, FancyLadderType.ARROW.getMeta()), new Object[]{" S ", "SSS", "S#S", 'S', "stickWood", '#', Blocks.LADDER});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyLadders, 3, FancyLadderType.DIAMOND.getMeta()), new Object[]{" S ", "S#S", " S ", 'S', "stickWood", '#', Blocks.LADDER});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyLadders, 4, FancyLadderType.HELIX.getMeta()), new Object[]{" S ", "S#S", "SSS", 'S', "stickWood", '#', Blocks.LADDER});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyLadders2, 4, FancyLadderType2.CIRCLE.getMeta()), new Object[]{"SS ", "S#S", " SS", 'S', "stickWood", '#', Blocks.LADDER});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyLadders2, 4, FancyLadderType2.LATTICED.getMeta()), new Object[]{"# #", "   ", "# #", 'S', "stickWood", '#', Blocks.LADDER});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyLadders2, 3, FancyLadderType2.X.getMeta()), new Object[]{"S S", " S ", "S#S", 'S', "stickWood", '#', Blocks.LADDER});

		//Glowstone
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyGlowstone, 4, GlowstoneType.ASYMMETRICAL.getMeta()), new Object[]{" X ", "X  ", " XX", 'X', "glowstone"});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyGlowstone, 6, GlowstoneType.CHECKERED.getMeta()), new Object[]{" XX", "XDX", "DXD", 'X', "glowstone", 'D', "dustGlowstone"});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyGlowstone, 8, GlowstoneType.FLAT.getMeta()), new Object[]{"XXX", "XDX", "XXX", 'X', "glowstone", 'D', "dustGlowstone"});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyGlowstone, 3, GlowstoneType.GLITTERING.getMeta()), new Object[]{"DDD", "DXD", "DDD", 'X', "glowstone", 'D', "dustGlowstone"});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyGlowstone, 9, GlowstoneType.GRIDDED.getMeta()), new Object[]{"XXX", "XXX", "XXX", 'X', "glowstone", 'D', "dustGlowstone"});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyGlowstone, 3, GlowstoneType.GRIDDED_LITE.getMeta()), new Object[]{"DDD", "DGD", "DDD", 'G', new ItemStack(ModBlocks.fancyGlowstone, 1, GlowstoneType.GRIDDED.getMeta()), 'D', "dustGlowstone"});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyGlowstone, 7, GlowstoneType.LATTICED.getMeta()), new Object[]{"XDX", "XDX", "XDX", 'X', "glowstone", 'D', "dustGlowstone"});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyGlowstone, 5, GlowstoneType.MOVING.getMeta()), new Object[]{"RXR", "XDX", "RDR", 'X', "glowstone", 'D', "dustGlowstone", 'R', "dustRedstone"});
		addShapedRecipe(new ItemStack(ModBlocks.fancyGlowstone, 4, GlowstoneType.NOISY.getMeta()), new Object[]{"GG", "GG", 'G', new ItemStack(ModBlocks.fancyGlowstone, 1, GlowstoneType.LATTICED.getMeta())});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyGlowstone, 5, GlowstoneType.ORGANIZED.getMeta()), new Object[]{"X X", " X ", "X X", 'X', "glowstone", 'D', "dustGlowstone"});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyGlowstone, 3, GlowstoneType.UNORGANIZED.getMeta()), new Object[]{"X D", " X ", "D X", 'X', "glowstone", 'D', "dustGlowstone"});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyGlowstone, 8, GlowstoneType.OUTER_SQUARE.getMeta()), new Object[]{"XXX", "X X", "XXX", 'X', "glowstone", 'D', "dustGlowstone"});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyGlowstone, 4, GlowstoneType.POPPED.getMeta()), new Object[]{"X X", " D ", "X X", 'X', "glowstone", 'D', "dustGlowstone"});
		addShapelessRecipe(new ItemStack(ModBlocks.fancyGlowstone, 2, GlowstoneType.POPPED_ORGANIZED.getMeta()), new Object[]{new ItemStack(ModBlocks.fancyGlowstone, 1, GlowstoneType.POPPED.getMeta()), new ItemStack(ModBlocks.fancyGlowstone, 1, GlowstoneType.ORGANIZED.getMeta())});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyGlowstone, 5, GlowstoneType.ROCKY.getMeta()), new Object[]{"X X", " XX", "X  ", 'X', "glowstone", 'D', "dustGlowstone"});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyGlowstone, 2, GlowstoneType.SPARKLING.getMeta()), new Object[]{" D ", "DXD", " D ", 'X', "glowstone", 'D', "dustGlowstone"});
		
		//Obsidian
		addShapedRecipe(new ItemStack(ModBlocks.fancyObsidian, 5, ObsidianType.FAN.getMeta()), new Object[]{" X ", "X#X", " X ", 'X', new ItemStack(ModBlocks.fancyObsidian, 1, ObsidianType.SEPARATED_SHARPLY.getMeta()), '#', new ItemStack(ModBlocks.fancyObsidian, 1, ObsidianType.FLAT.getMeta())});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyObsidian, 4, ObsidianType.FLAT.getMeta()), new Object[]{"XX", "XX", 'X', "obsidian"});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyObsidian, 5, ObsidianType.FLAT_CORNERED.getMeta()), new Object[]{"X X", " # ", "X X", 'X', "obsidian", '#', new ItemStack(ModBlocks.fancyObsidian, 1, ObsidianType.FLAT.getMeta())});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyObsidian, 9, ObsidianType.GRIDDED.getMeta()), new Object[]{"XXX", "XXX", "XXX", 'X', "obsidian"});
		addShapedOreRecipe(new ItemStack(ModBlocks.fancyObsidian, 9, ObsidianType.GRIDDED_LITE.getMeta()), new Object[]{"XXX", "X#X", "XXX", 'X', "obsidian", '#', new ItemStack(ModBlocks.fancyObsidian, 1, ObsidianType.GRIDDED.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.fancyObsidian, 3, ObsidianType.INSIGNIA.getMeta()), new Object[]{"X", "#", "X", 'X', new ItemStack(ModBlocks.fancyObsidian, 1, ObsidianType.SEPARATED_SHARPLY.getMeta()), '#', new ItemStack(ModBlocks.fancyObsidian, 1, ObsidianType.FLAT.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.fancyObsidian, 5, ObsidianType.INSIGNIA_CORNERED.getMeta()), new Object[]{"X  X", " # ", "X X", 'X', new ItemStack(ModBlocks.fancyObsidian, 1, ObsidianType.SEPARATED_SHARPLY.getMeta()), '#', new ItemStack(ModBlocks.fancyObsidian, 1, ObsidianType.INSIGNIA.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.fancyObsidian, 9, ObsidianType.PATTERNED.getMeta()), new Object[]{"XXX", "X#X", "XXX", 'X', new ItemStack(ModBlocks.fancyObsidian, 1, ObsidianType.FLAT_CORNERED.getMeta()), '#', new ItemStack(ModBlocks.fancyObsidian, 1, ObsidianType.FLAT.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.fancyObsidian, 4, ObsidianType.SEPARATED.getMeta()), new Object[]{"XX", "XX", 'X', new ItemStack(ModBlocks.fancyObsidian, 1, ObsidianType.PATTERNED.getMeta())});
		addShapelessRecipe(new ItemStack(ModBlocks.fancyObsidian, 1, ObsidianType.SEPARATED_SHARPLY.getMeta()), new Object[]{new ItemStack(ModBlocks.fancyObsidian, 1, ObsidianType.SEPARATED.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.fancyObsidian, 4, ObsidianType.SMOOTH.getMeta()), new Object[]{"XX", "XX", 'X', new ItemStack(ModBlocks.fancyObsidian, 1, ObsidianType.FLAT.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.fancyObsidian, 9, ObsidianType.SMOOTH_GRIDDED.getMeta()), new Object[]{"XXX", "XXX", "XXX", 'X', new ItemStack(ModBlocks.fancyObsidian, 1, ObsidianType.SMOOTH.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.fancyObsidian, 4, ObsidianType.SMOOTH_SPORADIC.getMeta()), new Object[]{"XX", "XX", 'X', new ItemStack(ModBlocks.fancyObsidian, 1, ObsidianType.SPORADIC.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.fancyObsidian, 9, ObsidianType.SPORADIC.getMeta()), new Object[]{"XXX", "XXX", "XXX", 'X', new ItemStack(ModBlocks.fancyObsidian, 1, ObsidianType.SEPARATED.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.fancyObsidian, 9, ObsidianType.SPORADIC_GRIDDED.getMeta()), new Object[]{"XXX", "XXX", "XXX", 'X', new ItemStack(ModBlocks.fancyObsidian, 1, ObsidianType.SPORADIC.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.fancyObsidian, 9, ObsidianType.SPORADIC_GRIDDED_SMOOTH.getMeta()), new Object[]{"XXX", "XXX", "XXX", 'X', new ItemStack(ModBlocks.fancyObsidian, 1, ObsidianType.SMOOTH_SPORADIC.getMeta())});

		//Sea Lanterns
		addShapedRecipe(new ItemStack(ModBlocks.fancySeaLantern, 1, SeaLanternType.BORDERLESS.getMeta()), new Object[]{" X ", "X#X", " X ", '#', Blocks.SEA_LANTERN, 'X', Items.PRISMARINE_CRYSTALS});
		addShapedRecipe(new ItemStack(ModBlocks.fancySeaLantern, 2, SeaLanternType.BORDERLESS_X.getMeta()), new Object[]{"X X", " X ", "X#X", '#', new ItemStack(ModBlocks.fancySeaLantern, 1, SeaLanternType.BORDERLESS.getMeta()), 'X', Items.PRISMARINE_SHARD});
		addShapedRecipe(new ItemStack(ModBlocks.fancySeaLantern, 2, SeaLanternType.CROSSED.getMeta()), new Object[]{"#X ", "XXX", " X ", '#', new ItemStack(ModBlocks.fancySeaLantern, 1, SeaLanternType.BORDERLESS.getMeta()), 'X', Items.PRISMARINE_SHARD});
		addShapedRecipe(new ItemStack(ModBlocks.fancySeaLantern, 2, SeaLanternType.CROSSED_X.getMeta()), new Object[]{"X X", " X ", "X#X", '#', Items.PRISMARINE_SHARD, 'X', Items.PRISMARINE_CRYSTALS});
		addShapedRecipe(new ItemStack(ModBlocks.fancySeaLantern, 3, SeaLanternType.GRATED.getMeta()), new Object[]{"XLX", "X#X", "XLX", '#', Items.PRISMARINE_SHARD, 'X', Items.PRISMARINE_CRYSTALS, 'L', Blocks.LADDER});
		addShapedRecipe(new ItemStack(ModBlocks.fancySeaLantern, 3, SeaLanternType.GRATED_BAR.getMeta()), new Object[]{"XBX", "X#X", "XBX", '#', Items.PRISMARINE_SHARD, 'X', Items.PRISMARINE_CRYSTALS, 'B', Blocks.IRON_BARS});
		addShapedRecipe(new ItemStack(ModBlocks.fancySeaLantern, 9, SeaLanternType.GRIDDED.getMeta()), new Object[]{"XXX", "XXX", "XXX", 'X', Items.PRISMARINE_SHARD});
		addShapedRecipe(new ItemStack(ModBlocks.fancySeaLantern, 2, SeaLanternType.GRIDDED_LITE.getMeta()), new Object[]{"XXX", "X#X", "XXX", 'X', Items.PRISMARINE_CRYSTALS, '#', new ItemStack(ModBlocks.fancySeaLantern, 1, SeaLanternType.GRIDDED.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.fancySeaLantern, 1, SeaLanternType.LATTICED.getMeta()), new Object[]{"X X", " # ", "X X", 'X', Items.PRISMARINE_SHARD, '#', Items.PRISMARINE_CRYSTALS});
		addShapedRecipe(new ItemStack(ModBlocks.fancySeaLantern, 1, SeaLanternType.LATTICED_DIAGONAL.getMeta()), new Object[]{"  #", " # ", "#X ", 'X', Items.PRISMARINE_SHARD, '#', Items.PRISMARINE_CRYSTALS});
		addShapelessRecipe(new ItemStack(ModBlocks.fancySeaLantern, 2, SeaLanternType.LATTICED_X.getMeta()), new Object[]{new ItemStack(ModBlocks.fancySeaLantern, 1, SeaLanternType.LATTICED_DIAGONAL.getMeta()), new ItemStack(ModBlocks.fancySeaLantern, 1, SeaLanternType.LATTICED_DIAGONAL.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.fancySeaLantern, 1, SeaLanternType.SHARPENED.getMeta()), new Object[]{" X ", "X#X", " X ", '#', new ItemStack(ModBlocks.fancySeaLantern, 1, SeaLanternType.BORDERLESS.getMeta()), 'X', Items.PRISMARINE_SHARD});
		addShapedRecipe(new ItemStack(ModBlocks.fancySeaLantern, 1, SeaLanternType.SEPERATED_HORIZONTAL.getMeta()), new Object[]{"X#X", '#', new ItemStack(ModBlocks.fancySeaLantern, 1, SeaLanternType.BORDERLESS.getMeta()), 'X', Items.PRISMARINE_SHARD});
		addShapedRecipe(new ItemStack(ModBlocks.fancySeaLantern, 1, SeaLanternType.SEPERATED_VERTICAL.getMeta()), new Object[]{"X", "#", "X", '#', new ItemStack(ModBlocks.fancySeaLantern, 1, SeaLanternType.BORDERLESS.getMeta()), 'X', Items.PRISMARINE_SHARD});
		addShapedRecipe(new ItemStack(ModBlocks.fancySeaLantern, 3, SeaLanternType.VINE.getMeta()), new Object[]{"XBX", "X#X", "XBX", '#', Items.PRISMARINE_SHARD, 'X', Items.PRISMARINE_CRYSTALS, 'B', Blocks.VINE});
		addShapedRecipe(new ItemStack(ModBlocks.fancySeaLantern, 3, SeaLanternType.WEB.getMeta()), new Object[]{"XBX", "X#X", "XBX", '#', Items.PRISMARINE_SHARD, 'X', Items.PRISMARINE_CRYSTALS, 'B', Blocks.WEB});

		
		addShapedRecipe(new ItemStack(ModBlocks.octagonBricks, 4, OctagonBrickType.NORMAL.getMeta()), new Object[]{"X X", "   ", "X X", 'X', Blocks.STONEBRICK});
		addShapedRecipe(new ItemStack(ModBlocks.octagonBricks, 4, OctagonBrickType.TILES.getMeta()), new Object[]{"XX", "XX", 'X', new ItemStack(ModBlocks.octagonBricks, 1, OctagonBrickType.NORMAL.getMeta())});

		addShapedOreRecipe(new ItemStack(ModBlocks.flumeRailBasic, 16), new Object[] {"X X", "XBX", "X X", 'X', "stickWood", 'B', Items.WATER_BUCKET});
		addShapedOreRecipe(new ItemStack(ModBlocks.flumeRailBooster, 8), new Object[] {"XRX", "XBX", "XGX", 'X', "stickWood", 'B', Items.WATER_BUCKET, 'R', "dustRedstone", 'G', "ingotGold"});
		addShapedOreRecipe(new ItemStack(ModBlocks.flumeRailEntrance, 1), new Object[] {"XPX", "XBX", "X X", 'X', "stickWood", 'B', Items.WATER_BUCKET, 'P', Blocks.OAK_FENCE_GATE});
		addShapedOreRecipe(new ItemStack(ModBlocks.flumeRailHolding, 4), new Object[] {"XPX", "XBX", "X X", 'X', "stickWood", 'B', Items.WATER_BUCKET, 'P', "dustRedstone"});
		addShapedOreRecipe(new ItemStack(ModBlocks.flumeRailDetector, 6), new Object[] {"XPX", "XBX", "X X", 'X', "stickWood", 'B', Items.WATER_BUCKET, 'P', Blocks.STONE_PRESSURE_PLATE});
		
		addShapedRecipe(new ItemStack(ModBlocks.flumeRailBasicGround, 16), new Object[] {"X X", "XBX", "XSX", 'X', Items.STICK, 'B', Items.WATER_BUCKET, 'S', new ItemStack(Blocks.STONE_SLAB, 1, 0)});
		addShapelessRecipe(new ItemStack(ModBlocks.flumeRailBoosterGround, 1), new Object[] {ModBlocks.flumeRailBooster, new ItemStack(Blocks.STONE_SLAB, 1, 0)});
		addShapedRecipe(new ItemStack(ModBlocks.flumeRailHoldingGround, 4), new Object[] {"XPX", "XBX", "XSX", 'X', Items.STICK, 'B', Items.WATER_BUCKET, 'P', Items.REDSTONE, 'S', new ItemStack(Blocks.STONE_SLAB, 1, 0)});
		addShapedRecipe(new ItemStack(ModBlocks.flumeRailRamp, 4), new Object[] {"F", "S", "R", 'F', ModBlocks.flumeRailBasic, 'S', Blocks.STONE_BRICK_STAIRS, 'R', Items.REDSTONE});
		
		addShapelessRecipe(new ItemStack(ModBlocks.bambooPlanks, 2, 0), new Object[]{new ItemStack(ModBlocks.bamboo)});
		addShapedRecipe(ModItems.bambooBoat, new Object[]{"X X", "XXX", 'X', ModBlocks.bambooPlanks});
		addShapedRecipe(new ItemStack(ModItems.bambooDoor, 3), new Object[]{"XX", "XX", "XX", 'X', ModBlocks.bambooPlanks});
		addShapedRecipe(ModItems.blowGun, new Object[]{"X", "X", "X", 'X', ModBlocks.bamboo});
		addShapedOreRecipe(new ItemStack(ModItems.dart, 4, DartType.BASIC.getMetadata()), new Object[] {"  #", " S ", "F  ", '#', Items.FLINT, 'F', Items.FEATHER, 'S', "stickWood"});
		addShapedOreRecipe(new ItemStack(ModItems.dart, 4, DartType.BLUE.getMetadata()), new Object[] {"  #", " S ", "F  ", '#', blueShard, 'F', Items.FEATHER, 'S', "stickWood"});
		addShapedOreRecipe(new ItemStack(ModItems.dart, 4, DartType.RED.getMetadata()), new Object[] {"  #", " S ", "F  ", '#', redShard, 'F', Items.FEATHER, 'S', "stickWood"});
		addShapedOreRecipe(new ItemStack(ModItems.dart, 4, DartType.GREEN.getMetadata()), new Object[] {"  #", " S ", "F  ", '#', greenShard, 'F', Items.FEATHER, 'S', "stickWood"});
		addShapedOreRecipe(new ItemStack(ModItems.dart, 4, DartType.DARK.getMetadata()), new Object[] {"  #", " S ", "F  ", '#', darkShard, 'F', Items.FEATHER, 'S', "stickWood"});
		addShapedOreRecipe(new ItemStack(ModItems.dart, 4, DartType.PURE.getMetadata()), new Object[] {"  #", " S ", "F  ", '#', pureShard, 'F', Items.FEATHER, 'S', "stickWood"});
		
		addShapedRecipe(new ItemStack(ModItems.chestBoat, 1, EntityCustomBoat.Type.OAK.getMetadata()), new Object[]{"X", "B", 'X', Blocks.CHEST, 'B', Items.BOAT});
		addShapedRecipe(new ItemStack(ModItems.chestBoat, 1, EntityCustomBoat.Type.SPRUCE.getMetadata()), new Object[]{"X", "B", 'X', Blocks.CHEST, 'B', Items.SPRUCE_BOAT});
		addShapedRecipe(new ItemStack(ModItems.chestBoat, 1, EntityCustomBoat.Type.BIRCH.getMetadata()), new Object[]{"X", "B", 'X', Blocks.CHEST, 'B', Items.BIRCH_BOAT});
		addShapedRecipe(new ItemStack(ModItems.chestBoat, 1, EntityCustomBoat.Type.JUNGLE.getMetadata()), new Object[]{"X", "B", 'X', Blocks.CHEST, 'B', Items.JUNGLE_BOAT});
		addShapedRecipe(new ItemStack(ModItems.chestBoat, 1, EntityCustomBoat.Type.ACACIA.getMetadata()), new Object[]{"X", "B", 'X', Blocks.CHEST, 'B', Items.ACACIA_BOAT});
		addShapedRecipe(new ItemStack(ModItems.chestBoat, 1, EntityCustomBoat.Type.DARK_OAK.getMetadata()), new Object[]{"X", "B", 'X', Blocks.CHEST, 'B', Items.DARK_OAK_BOAT});

		addShapedOreRecipe(new ItemStack(ModBlocks.crates, 1, CrateType.BLUE.getMeta()), new Object[] {"XXX", "# #", "XXX", 'X', new ItemStack(ModBlocks.crystalPlanks, 1, WoodType.BLUE.getMeta()), '#', "chestWood"});
		addShapedOreRecipe(new ItemStack(ModBlocks.crates, 1, CrateType.RED.getMeta()), new Object[] {"XXX", "#C#", "XXX", 'X', new ItemStack(ModBlocks.crystalPlanks, 1, WoodType.RED.getMeta()), '#', "chestWood", 'C', new ItemStack(ModBlocks.crates, 1, CrateType.BLUE.getMeta())});
		addShapedOreRecipe(new ItemStack(ModBlocks.crates, 1, CrateType.GREEN.getMeta()), new Object[] {"XXX", "#C#", "XXX", 'X', new ItemStack(ModBlocks.crystalPlanks, 1, WoodType.GREEN.getMeta()), '#', "chestWood", 'C', new ItemStack(ModBlocks.crates, 1, CrateType.RED.getMeta())});
		addShapedOreRecipe(new ItemStack(ModBlocks.crates, 1, CrateType.DARK.getMeta()), new Object[] {"XXX", "#C#", "XXX", 'X', new ItemStack(ModBlocks.crystalPlanks, 1, WoodType.DARK.getMeta()), '#', "chestWood", 'C', new ItemStack(ModBlocks.crates, 1, CrateType.GREEN.getMeta())});
		
		addShapedOreRecipe(new ItemStack(ModBlocks.storageCase, 4, EnumCaseType.NOTE.getMeta()), new Object[]{"WNW", "NCN", "WNW", 'W', "plankWood", 'N', Blocks.NOTEBLOCK, 'C', "chestTrapped"});
		addShapedOreRecipe(new ItemStack(ModBlocks.storageCase, 2, EnumCaseType.PISTON.getMeta()), new Object[]{"WNW", "NCN", "WNW", 'W', "cobblestone", 'N', Blocks.PISTON, 'C', "chestTrapped"});
		addShapedOreRecipe(new ItemStack(ModBlocks.storageCase, 2, EnumCaseType.STICKY_PISTON.getMeta()), new Object[]{"WNW", "NCN", "WNW", 'W', "cobblestone", 'N', Blocks.STICKY_PISTON, 'C', "chestTrapped"});
		
		addShapelessRecipe(new ItemStack(ModBlocks.flowerLilypad), new Object[]{Blocks.WATERLILY, new ItemStack(Blocks.RED_FLOWER, 1, EnumFlowerType.BLUE_ORCHID.getMeta())});
		
		addShapelessRecipe(ModFluids.bucketList.get(ModFluids.fluidInk), new Object[]{Items.BUCKET, 
				new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage()), new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage()), 
				new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage()), new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage()), 
				new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage()), new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage()), 
				new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage()), new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage())});		
		addShapelessRecipe(new ItemStack(Items.DYE, 8, EnumDyeColor.BLACK.getDyeDamage()), new Object[]{ModFluids.bucketList.get(ModFluids.fluidInk)});
		
		addShapelessRecipe(ModFluids.bucketList.get(ModFluids.fluidTears), new Object[]{Items.BUCKET, 
				Items.GHAST_TEAR, Items.GHAST_TEAR, Items.GHAST_TEAR, Items.GHAST_TEAR, 
				Items.GHAST_TEAR, Items.GHAST_TEAR, Items.GHAST_TEAR, Items.GHAST_TEAR});		
		addShapelessRecipe(new ItemStack(Items.GHAST_TEAR, 8), new Object[]{ModFluids.bucketList.get(ModFluids.fluidTears)});
		
		//TOOLS
		addShapedRecipe(ModItems.lock, new Object[]{" N ", "NPN", "NNN", 'N', dIronNugget, 'P', dIronPlate});

		addShapedOreRecipe(ModItems.emptyMobEssence, new Object[]{" D ", "DID", " D ", 'D', new ItemStack(ModBlocks.metalBars, 1, EnumMetalBarType.DARK_IRON.getMeta()) , 'I', "ingotIron"});
		
		addShapedOreRecipe(new ItemStack(ModItems.pumpkinScoop), new Object[] {" P ", " SP", "S  ", 'P', Blocks.PUMPKIN, 'S', "stickWood"});
		
		
		ItemStack cShears = new ItemStack(ModItems.shears);
		ItemNBTHelper.setString(cShears, "Color", "darkIron");
		addShapedRecipe(cShears, new Object[] {" #", "# ", '#', dIronIngot});
		addShapedOreRecipe(ModItems.darkIronAxe, new Object[]{"XX", "X#", " #", 'X', dIronIngot, '#', "stickWood"});
		addShapedOreRecipe(ModItems.darkIronPickaxe, new Object[]{"XXX", " # ", " # ", 'X', dIronIngot, '#', "stickWood"});
		addShapedOreRecipe(ModItems.darkIronHoe, new Object[]{"XX", " #", " #", 'X', dIronIngot, '#', "stickWood"});
		addShapedOreRecipe(ModItems.darkIronShovel, new Object[]{"X", "#", "#", 'X', dIronIngot, '#', "stickWood"});
		addShapedOreRecipe(ModItems.darkIronSword, new Object[]{"X", "X", "#", 'X', dIronIngot, '#', "stickWood"});
		addShapedOreRecipe(ModItems.darkIronBow, new Object[]{" X#", "X #", " X#", 'X', dIronIngot, '#', "string"});

		addShapedRecipe(ModItems.darkIronHelmet, new Object[]{"XXX", "X X", 'X', dIronIngot});
		addShapedRecipe(ModItems.darkIronChestplate, new Object[]{"X X", "XXX", "XXX", 'X', dIronIngot});
		addShapedRecipe(ModItems.darkIronLeggings, new Object[]{"XXX", "X X", "X X", 'X', dIronIngot});
		addShapedRecipe(ModItems.darkIronBoots, new Object[]{"X X", "X X", 'X', dIronIngot});
		
		addShapedRecipe(new ItemStack(ModItems.superTorch), new Object[] {" # ", "NTN", " N ", '#', Blocks.DAYLIGHT_DETECTOR, 'T', Blocks.TORCH, 'N', dIronNugget});
		addShapedRecipe(new ItemStack(ModBlocks.enderTorch), new Object[] {"#", "I", "I", '#', ModItems.telePearl, 'I', dIronIngot});
		addShapedOreRecipe(new ItemStack(ModItems.bombomb), new Object[] {" S ", "ICI", "P P", 'S', "string", 'I', "ingotIron", 'C', new ItemStack(Items.SKULL, 1, 4), 'P', Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE});

		if(BaublesIntegration.instance().hasBaubles()){
			if(ModItems.dragonWingsBauble !=null){
				addShapedOreRecipe(new ItemStack(ModItems.dragonWingsBauble), new Object[]{" W ", "I I", " I ", 'W', ModItems.wings, 'I', "ingotGold"});
			}
		}
		
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalWorkbench, 1, WorkbenchType.BLUE.getMeta()), new Object[]{"###", "#W#", "###", '#', blueIngot, 'W', "workbench"});
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalWorkbench, 1, WorkbenchType.RED.getMeta()), new Object[]{"###", "#W#", "###", '#', redIngot, 'W', "workbench"});
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalWorkbench, 1, WorkbenchType.GREEN.getMeta()), new Object[]{"###", "#W#", "###", '#', greenIngot, 'W', "workbench"});
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalWorkbench, 1, WorkbenchType.DARK.getMeta()), new Object[]{"###", "#W#", "###", '#', darkIngot, 'W', "workbench"});

		addShapedOreRecipe(new ItemStack(ModBlocks.enhancementTable), new Object[]{"#I#", "IWI", "#I#", '#', Items.ENCHANTED_BOOK, 'I', purePlate, 'W', new ItemStack(ModBlocks.crystalWorkbench, 1, WorkbenchType.DARK.getMeta())});
		
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
		addShapedOreRecipeNBT(tier1CU, new Object[]{" # ", "NPN", " # ", '#', redPlate, 'N', redNugget, 'P', ItemUtil.copy(tier0CU, 1)});
		addShapedOreRecipeNBT(tier2CU, new Object[]{" # ", "NPN", " # ", '#', greenPlate, 'N', greenNugget, 'P', ItemUtil.copy(tier1CU, 1)});
		addShapedOreRecipeNBT(tier3CU, new Object[]{" # ", "NPN", " # ", '#', darkPlate, 'N', darkNugget, 'P', ItemUtil.copy(tier2CU, 1)});
		
		ItemStack powerPipeRF = new ItemStack(ModBlocks.crystalPipe, 8, PipeType.POWERRF.getMeta());
		ItemStack tier0RF = powerPipeRF.copy();ItemNBTHelper.setInteger(tier0RF, "Tier", 0);
		ItemStack tier1RF = powerPipeRF.copy();ItemNBTHelper.setInteger(tier1RF, "Tier", 1);
		ItemStack tier2RF = powerPipeRF.copy();ItemNBTHelper.setInteger(tier2RF, "Tier", 2);
		ItemStack tier3RF = powerPipeRF.copy();ItemNBTHelper.setInteger(tier3RF, "Tier", 3);
		addShapedOreRecipe(tier0RF, new Object[]{"###", "NIN", "###", '#', dIronPlate, 'I', blueIngot, 'N', "dustRedstone" });
		addShapedOreRecipeNBT(tier1RF, new Object[]{" # ", "NPN", " # ", '#', redPlate, 'N', "nuggetGold", 'P', ItemUtil.copy(tier0RF, 1)});
		addShapedOreRecipeNBT(tier2RF, new Object[]{" # ", "NPN", " # ", '#', greenPlate, 'N', "ingotGold", 'P', ItemUtil.copy(tier1RF, 1)});
		addShapedOreRecipeNBT(tier3RF, new Object[]{" # ", "NPN", " # ", '#', darkPlate, 'N', Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, 'P', ItemUtil.copy(tier2RF, 1)});

		
		addShapedOreRecipe(new ItemStack(ModItems.pipeFilter, 1, FilterType.NORMAL.ordinal()), new Object[]{"#P#", "PHP", "#P#", '#', "nuggetCrystal", 'P', "paper", 'H', Blocks.HOPPER});
		addShapelessRecipe(new ItemStack(ModItems.pipeFilter, 1, FilterType.MOD.ordinal()), new Object[]{new ItemStack(ModItems.pipeFilter, 1, FilterType.NORMAL.ordinal()), Items.BOOK});
		addShapelessOreRecipe(new ItemStack(ModItems.pipeFilter, 1, FilterType.CAMERA.ordinal()), new Object[]{new ItemStack(ModItems.pipeFilter, 1, FilterType.NORMAL.ordinal()), Blocks.REDSTONE_LAMP, "dustRedstone"});
		
		List<String> copyListTank = Lists.newArrayList();
		copyListTank.add(FluidHandlerItemStack.FLUID_NBT_KEY);
		
		addShapedOreRecipe(new ItemStack(ModBlocks.crystalTank, 1, TankType.BLUE.getMeta()), new Object[]{"###", "#G#", "###", '#', bluePlate, 'G', "blockGlass"});
		ModCrafting.addNBTRecipe(new ItemStack(ModBlocks.crystalTank, 1, TankType.RED.getMeta()), copyListTank, new Object[]{"###", "#T#", "###", '#', redPlate, 'T', new ItemStack(ModBlocks.crystalTank, 1, TankType.BLUE.getMeta())});
		ModCrafting.addNBTRecipe(new ItemStack(ModBlocks.crystalTank, 1, TankType.GREEN.getMeta()), copyListTank, new Object[]{"###", "#T#", "###", '#', greenPlate, 'T', new ItemStack(ModBlocks.crystalTank, 1, TankType.RED.getMeta())});
		ModCrafting.addNBTRecipe(new ItemStack(ModBlocks.crystalTank, 1, TankType.DARK.getMeta()), copyListTank, new Object[]{"###", "#T#", "###", '#', darkPlate, 'T', new ItemStack(ModBlocks.crystalTank, 1, TankType.GREEN.getMeta())});
		ModCrafting.addNBTRecipe(new ItemStack(ModBlocks.crystalTank, 1, TankType.PURE.getMeta()), copyListTank, new Object[]{"###", "#T#", "###", '#', purePlate, 'T', new ItemStack(ModBlocks.crystalTank, 1, TankType.DARK.getMeta())});

		addShapedOreRecipe(ModBlocks.xpTank, new Object[]{"###", "#G#", "###", '#', dIronPlate, 'G', "blockGlass"});
		
		for(BlockPlanks.EnumType woodType : BlockPlanks.EnumType.values()){
			ItemStack slab = new ItemStack(Blocks.WOODEN_SLAB, 1, woodType.getMetadata());
			addShapedRecipe(new ItemStack(ModBlocks.shieldRack, 1, woodType.getMetadata()), new Object[]{"HSH", "SHS", " S ", 'S', slab, 'H', Blocks.TRIPWIRE_HOOK});
			addShapedOreRecipe(new ItemStack(ModBlocks.jar, 4, woodType.getMetadata()), new Object[]{" S ", "P P", "PPP", 'S', slab, 'P', "paneGlassColorless"});
			addShapedOreRecipe(new ItemStack(ModBlocks.bridge, 6, woodType.getMetadata()), new Object[]{"S S", "WWW", 'W', slab, 'S', "stickWood"});
		}		

		for(PumpkinType type : PumpkinType.values()){
			addShapelessRecipe(new ItemStack(ModBlocks.fancyPumpkinLit, 1, type.getMeta()), new Object[]{Blocks.TORCH, new ItemStack(ModBlocks.fancyPumpkin, 1, type.getMeta())});
			addShapedRecipe(new ItemStack(Items.PUMPKIN_SEEDS, 4), new Object[]{"P", 'P', new ItemStack(ModBlocks.fancyPumpkin, 1, type.getMeta())});
		}
		
		List<String> copyListBattery = Lists.newArrayList();
		copyListBattery.add("Energy");
		copyListBattery.add("BatteryData");
		for(EnumFacing face : EnumFacing.VALUES){
			copyListBattery.add("io."+face.name().toLowerCase());
		}
		addShapedRecipe(new ItemStack(ModBlocks.battery, 1, BatteryType.BLUE.getMeta()), new Object[]{"#I#", "IFI", "#I#", '#', bluePlate, 'F', machineFrame, 'I', blueIngot});
		ModCrafting.addNBTRecipe(new ItemStack(ModBlocks.battery, 1, BatteryType.RED.getMeta()), copyListBattery, new Object[]{"I#I", "#B#", "I#I", '#', redPlate, 'I', redIngot, 'B', new ItemStack(ModBlocks.battery, 1, BatteryType.BLUE.getMeta())});
		ModCrafting.addNBTRecipe(new ItemStack(ModBlocks.battery, 1, BatteryType.GREEN.getMeta()), copyListBattery, new Object[]{"I#I", "#B#", "I#I", '#', greenPlate, 'I', greenIngot, 'B', new ItemStack(ModBlocks.battery, 1, BatteryType.RED.getMeta())});
		ModCrafting.addNBTRecipe(new ItemStack(ModBlocks.battery, 1, BatteryType.DARK.getMeta()), copyListBattery, new Object[]{"I#I", "#B#", "I#I", '#', darkPlate, 'I', darkIngot, 'B', new ItemStack(ModBlocks.battery, 1, BatteryType.GREEN.getMeta())});
		ModCrafting.addNBTRecipe(new ItemStack(ModBlocks.battery, 1, BatteryType.PURE.getMeta()), copyListBattery, new Object[]{"I#I", "#B#", "I#I", '#', purePlate, 'I', pureIngot, 'B', new ItemStack(ModBlocks.battery, 1, BatteryType.DARK.getMeta())});

		addShapedOreRecipe(new ItemStack(ModBlocks.invCharger, 1, ChargerBlockType.RF.getMeta()), new Object[]{" C ", "PME", 'C', "chestWood", 'E', "endereye", 'P', ItemUtil.copy(tier0RF, 1), 'M', machineFrameEnder});
		addShapedOreRecipe(new ItemStack(ModBlocks.invCharger, 1, ChargerBlockType.CU.getMeta()), new Object[]{" C ", "PME", 'C', "chestWood", 'E', "endereye", 'P', ItemUtil.copy(tier0CU, 1), 'M', machineFrameEnder});

		addShapedOreRecipe(ModBlocks.customSpawner, new Object[]{"BBB", "BRB", "BBB", 'B', new ItemStack(ModBlocks.metalBars, 1, EnumMetalBarType.DARK_IRON.getMeta()), 'R', "rodBlaze"});
		addShapedRecipe(ModBlocks.xpVacuum, new Object[]{" P ", "PTP", " F ", 'T', ModBlocks.xpTank, 'P', dIronPlate, 'F', new ItemStack(ModItems.machineFrame, 1, FrameType.ENDER.getMetadata())});
		addShapedRecipe(ModBlocks.xpFountain, new Object[]{" B ", "XME", " P ", 'B', new ItemStack(ModBlocks.metalBars, 1, EnumMetalBarType.DARK_IRON.getMeta()), 'X', ModFluids.bucketList.get(ModFluids.fluidXpJuice), 'M', new ItemStack(ModItems.machineFrame, 1, FrameType.ENDER.getMetadata()), 'E', ModFluids.bucketList.get(ModFluids.fluidEnder), 'P', fluidPipe});

		ItemStack pureSword = new ItemStack(ModItems.crystalSword);
		ItemNBTHelper.getCompound(pureSword).setString("Color", "pure");
		addShapedOreRecipeNBT(ModBlocks.mobGrinder, new Object[]{"PHP", "ISL", "PFP", 'P', dIronPlate, 'H', "skull", 'S', pureSword, 'I', itemPipe, 'L', ModBlocks.xpVacuum, 'F', machineFrame});
		
		addShapedRecipe(new ItemStack(ModBlocks.decorativeBlock, 8, DecorativeBlockType.SQUARE_TURN.getMeta()), new Object[]{"P P", " B ", "P P", 'P', darkPlate, 'B', dIronBlock});
		
		addShapedOreRecipe(new ItemStack(ModBlocks.darkIronRail, 16), new Object[]{"I I", "ISI", "I I", 'I', dIronIngot, 'S', "stickWood"});

		addShapedOreRecipe(ModBlocks.weather, new Object[]{"#S#", "CFB", "###", '#', dIronPlate, 'B', "bucket", 'S', Blocks.DAYLIGHT_DETECTOR, 'C', Items.CLOCK, 'F', machineFrame});

		addShapedOreRecipe(ModBlocks.hddInterface, new Object[]{"###", "#FH", "#P#", '#', dIronPlate, 'H', Blocks.TRIPWIRE_HOOK, 'P', pipeEStorage, 'F', machineFrame});
		addShapelessRecipe(ModBlocks.hddArray, new Object[]{ModBlocks.hddInterface, new ItemStack(ModBlocks.crystalChest, 1, CrystalChestType.DARKIRON.ordinal())});

		addShapedOreRecipe(ModBlocks.externalInterface, new Object[]{"#S#", "#H#", "#P#", '#', dIronPlate, 'S', Blocks.STICKY_PISTON, 'H', "chest", 'P', pipeEStorage});

		addShapedOreRecipe(new ItemStack(ModBlocks.storagePanel, 1, PanelType.STORAGE.getMeta()), new Object[]{"###", "#G#", "#P#", '#', dIronPlate, 'G', "paneGlassBlack", 'P', pipeEStorage});
		addShapelessOreRecipe(new ItemStack(ModBlocks.storagePanel, 1, PanelType.CRAFTING.getMeta()), new Object[]{new ItemStack(ModBlocks.storagePanel), "workbench"});
		addShapelessRecipe(new ItemStack(ModBlocks.storagePanel, 1, PanelType.DISPLAY.getMeta()), new Object[]{new ItemStack(ModBlocks.storagePanel), Items.COMPARATOR});
		addShapelessRecipe(new ItemStack(ModBlocks.storagePanel, 1, PanelType.MONITOR.getMeta()), new Object[]{ModBlocks.storagePanel, ModItems.craftingPattern});

		addShapelessRecipe(ModBlocks.wirelessPanel, new Object[]{new ItemStack(ModBlocks.storagePanel), new ItemStack(ModBlocks.wirelessPipe)});
		addShapedOreRecipe(ModItems.wirelessPanel, new Object[]{"E  ", "SP ", 'E', "endereye", 'P', new ItemStack(ModBlocks.storagePanel), 'S', crystalRod});

		addShapedOreRecipe(ModBlocks.wirelessPipe, new Object[]{"#I#", "ICI", "#P#", '#', Items.ENDER_EYE, 'C', new ItemStack(ModBlocks.enderBuffer), 'I', pureIngot, 'P', pipeEStorage});
		addShapedOreRecipe(ModBlocks.craftingController, new Object[]{"#W#", "WPW", "#W#", '#', dIronPlate, 'W', "workbench", 'P', pipeEStorage});
		addShapedOreRecipe(ModBlocks.crafter, new Object[]{"#F#", "SMD", "#W#", '#', dIronPlate, 'W', "workbench", 'M', machineFrame, 'F', Blocks.FURNACE, 'S', pipeEStorage, 'D', Blocks.DROPPER});
		addShapedOreRecipe(ModBlocks.powerCore, new Object[]{"###", "#B#", "#P#", '#', dIronPlate, 'B', new ItemStack(ModBlocks.battery, 1, BatteryType.BLUE.getMeta()), 'P', pipeEStorage});
		addShapedOreRecipe(new ItemStack(ModBlocks.encoder, 1, EncoderType.NORMAL.getMeta()), new Object[]{"#P#", "IWI", "#I#", '#', dIronPlate, 'W', "workbench", 'I', dIronPlate, 'P', ModItems.craftingPattern});
		addShapelessRecipe(new ItemStack(ModBlocks.encoder, 1, EncoderType.PROCESSING.getMeta()), new Object[]{new ItemStack(ModBlocks.encoder, 1, EncoderType.NORMAL.getMeta())});
		addShapelessRecipe(new ItemStack(ModBlocks.securityEncoder), new Object[]{new ItemStack(ModBlocks.encoder, 1, EncoderType.NORMAL.getMeta()), ModItems.securityCard});
		addShapelessRecipe(new ItemStack(ModBlocks.securityController), new Object[]{ModBlocks.powerCore, ModItems.securityCard});

		ItemStack attachment_import = ItemPipeAttachment.getAttachmentStack(AttachmentUtil.eStorage_Import.getID());
		ItemStack attachment_export = ItemPipeAttachment.getAttachmentStack(AttachmentUtil.eStorage_Export.getID());
		addShapedOreRecipe(ModBlocks.stocker, new Object[]{"#C#", "EMR", "#P#", '#', dIronPlate, 'R', Items.COMPARATOR, 'M', machineFrame, 'E', attachment_export, 'C', ModItems.craftingPattern, 'P', pipeEStorage});

		addShapedOreRecipe(ItemHDD.getFromMeta(0), new Object[]{"#R#", "CIC", "#T#", '#', dIronIngot, 'R', "dustRedstone", 'I', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, 'C', blueIngot, 'T', Blocks.TRIPWIRE_HOOK});
		List<String> copyList = Lists.newArrayList();
		copyList.add(ItemHDD.NBT_ITEM_LIST);
		
		ItemStack hddPlate = new ItemStack(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
		
		ItemStack redHDD = ItemHDD.getFromMeta(1);
		addNBTRecipe(redHDD, copyList, new Object[]{"CCC", "IHI", "CCC", 'I', hddPlate, 'C', redIngot, 'H', ItemHDD.getFromMeta(0)});
		ItemStack greenHDD = ItemHDD.getFromMeta(2);
		addNBTRecipe(greenHDD, copyList, new Object[]{"CCC", "IHI", "CCC", 'I', hddPlate, 'C', greenIngot, 'H', ItemHDD.getFromMeta(1)});
		ItemStack darkHDD = ItemHDD.getFromMeta(3);
		addNBTRecipe(darkHDD, copyList, new Object[]{"CCC", "IHI", "CCC", 'I', hddPlate, 'C', darkIngot, 'H', ItemHDD.getFromMeta(2)});
		ItemStack pureHDD = ItemHDD.getFromMeta(4);
		addNBTRecipe(pureHDD, copyList, new Object[]{"CCC", "IHI", "CCC", 'I', hddPlate, 'C', pureIngot, 'H', ItemHDD.getFromMeta(3)});
		addShapedOreRecipe(new ItemStack(ModItems.craftingPattern), new Object[]{" # ", "#P#", " # ", '#', dIronNugget, 'P', "paper"});
		addShapelessRecipe(new ItemStack(ModItems.securityCard), new Object[]{ModItems.lock, ModItems.craftingPattern});

		addShapedRecipe(attachment_import, new Object[]{"DHD", " E ", 'D', dIronPlate, 'E', pipeEStorage, 'H', Blocks.HOPPER});
		addShapelessRecipe(attachment_export, new Object[]{ItemPipeAttachment.getAttachmentStack(AttachmentUtil.eStorage_Import.getID()), Blocks.PISTON});
		addShapedRecipe(ItemPipeAttachment.getAttachmentStack(AttachmentUtil.eStorage_Sensor.getID()), new Object[]{"DCD", " E ", 'D', dIronPlate, 'E', pipeEStorage, 'C', Items.COMPARATOR});

		addShapedOreRecipe(ModBlocks.cubePortal, new Object[]{"#P#", "PEP", "#P#", '#', dIronPlate, 'E', "endereye", 'P', "enderpearl"});

		String goldOrPlate = getBestOreID("plateGold", "ingotGold");
		addShapedOreRecipe(new ItemStack(ModBlocks.elevator, 4), new Object[]{"#C#", "SGP", "#C#", '#', dIronPlate, 'G', goldOrPlate, 'P', "piston", 'S', "pistonSticky", 'C', "plateCrystal"});
		addShapedOreRecipe(ModBlocks.elevatorFloor, new Object[]{"#C#", "GSG", "#C#", '#', dIronPlate, 'S', "pistonSticky", 'C', "plateCrystal", 'G', goldOrPlate});
		addShapedOreRecipe(ModBlocks.elevatorCaller, new Object[]{" E ", "BFB", " E ", 'E', "enderpearl", 'B', Blocks.STONE_BUTTON, 'F', ModBlocks.elevatorFloor});

		addShapedOreRecipe(ModBlocks.cauldron, new Object[]{"NCN", "NMN", "NCN", 'M', Items.CAULDRON, 'C', "gemCrystal", 'N', "shardCrystal"});

		addShapelessOreRecipe(new ItemStack(ModItems.miscCard, 1, ItemMiscCard.CardType.EPORTAL.getMetadata()), new Object[]{dIronPlate, "paper", "enderpearl"});

		addShapelessOreRecipe(new ItemStack(ModItems.miscCard, 1, ItemMiscCard.CardType.CUBE.getMetadata()), new Object[]{dIronPlate, "paper", Items.NAME_TAG});

		addShapelessRecipe(new ItemStack(Items.DYE, 2, EnumDyeColor.WHITE.getDyeDamage()), new Object[]{new ItemStack(ModBlocks.roseBush, 1, RoseType.WHITE.getMeta())});
		addShapelessRecipe(new ItemStack(Items.DYE, 2, EnumDyeColor.ORANGE.getDyeDamage()), new Object[]{new ItemStack(ModBlocks.roseBush, 1, RoseType.ORANGE.getMeta())});
		addShapelessRecipe(new ItemStack(Items.DYE, 2, EnumDyeColor.MAGENTA.getDyeDamage()), new Object[]{new ItemStack(ModBlocks.roseBush, 1, RoseType.MAGENTA.getMeta())});
		addShapelessRecipe(new ItemStack(Items.DYE, 2, EnumDyeColor.YELLOW.getDyeDamage()), new Object[]{new ItemStack(ModBlocks.roseBush, 1, RoseType.YELLOW.getMeta())});
		addShapelessRecipe(new ItemStack(Items.DYE, 2, EnumDyeColor.PINK.getDyeDamage()), new Object[]{new ItemStack(ModBlocks.roseBush, 1, RoseType.PINK.getMeta())});
		addShapelessRecipe(new ItemStack(Items.DYE, 2, EnumDyeColor.CYAN.getDyeDamage()), new Object[]{new ItemStack(ModBlocks.roseBush, 1, RoseType.CYAN.getMeta())});
		addShapelessRecipe(new ItemStack(Items.DYE, 2, EnumDyeColor.PURPLE.getDyeDamage()), new Object[]{new ItemStack(ModBlocks.roseBush, 1, RoseType.PURPLE.getMeta())});

		
		addShapelessRecipe(Items.SLIME_BALL, new Object[]{Items.WATER_BUCKET, Items.MILK_BUCKET});
		addShapedOreRecipe(Items.NAME_TAG, new Object[]{" PP", "SBI", " PP", 'S', "string", 'I', "ingotIron", 'P', "paper", 'B', "slimeball"});
		
		BackpackUtil.addRecipes();

		addShapedOreRecipe(new ItemStack(ModBlocks.darkIronRail, 16), new Object[] {"X X", "X#X", "X X", 'X', dIronIngot, '#', "stickWood"});

		addShapedOreRecipe(new ItemStack(ModBlocks.pedistal, 4), new Object[]{"XXX", " S ", "XXX", 'X', new ItemStack(Blocks.STONE_SLAB, 1, 0), 'S', "stone"});
		addShapedOreRecipe(new ItemStack(ModBlocks.fusionPedistal, 1), new Object[]{"XXX", " S ", "XXX", 'X', blueIngot, 'S', new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.BLUE.getMeta())});

		ItemStack engineTier0 = new ItemStack(ModBlocks.engine, 1, EngineType.FURNACE.getMeta()); ItemNBTHelper.setInteger(engineTier0, "Tier", 0);
		ItemStack engineTier1 = new ItemStack(ModBlocks.engine, 1, EngineType.FURNACE.getMeta()); ItemNBTHelper.setInteger(engineTier1, "Tier", 1);
		ItemStack engineTier2 = new ItemStack(ModBlocks.engine, 1, EngineType.FURNACE.getMeta()); ItemNBTHelper.setInteger(engineTier2, "Tier", 2);

		addShapedOreRecipeNBT(engineTier0, new Object[]{"CCC", "IFI", "IPI", 'I', dIronIngot, 'F', Blocks.FURNACE, 'P', ItemUtil.copy(tier0CU, 1), 'C', "cobblestone"});
		addShapedRecipe(engineTier1, new Object[]{"EEE", "EPE", "EEE", 'E', engineTier0, 'P', ItemUtil.copy(tier2CU, 1)});
		addShapedRecipe(engineTier2, new Object[]{"EEE", "EPE", "EEE", 'E', engineTier1, 'P', ItemUtil.copy(tier3CU, 1)});

		ItemStack lavaEngineTier0 = new ItemStack(ModBlocks.engine, 1, EngineType.LAVA.getMeta()); ItemNBTHelper.setInteger(lavaEngineTier0, "Tier", 0);
		ItemStack lavaEngineTier1 = new ItemStack(ModBlocks.engine, 1, EngineType.LAVA.getMeta()); ItemNBTHelper.setInteger(lavaEngineTier1, "Tier", 1);
		ItemStack lavaEngineTier2 = new ItemStack(ModBlocks.engine, 1, EngineType.LAVA.getMeta()); ItemNBTHelper.setInteger(lavaEngineTier2, "Tier", 2);

		addShapedRecipe(lavaEngineTier0, new Object[]{"MCM", "LFL", "IPI", 'I', dIronPlate, 'L', Items.LAVA_BUCKET, 'P', ItemUtil.copy(tier0CU, 1), 'C', Blocks.NETHER_BRICK, 'F', machineFrame, 'M', Blocks.MAGMA/*Magma*/});
		addShapedRecipe(lavaEngineTier1, new Object[]{"EEE", "EPE", "EEE", 'E', lavaEngineTier0, 'P', ItemUtil.copy(tier2CU, 1)});
		addShapedRecipe(lavaEngineTier2, new Object[]{"EEE", "EPE", "EEE", 'E', lavaEngineTier1, 'P', ItemUtil.copy(tier3CU, 1)});
		
		ItemStack vampireEngineTier0 = new ItemStack(ModBlocks.engine, 1, EngineType.VAMPIRE.getMeta()); ItemNBTHelper.setInteger(vampireEngineTier0, "Tier", 0);
		ItemStack vampireEngineTier1 = new ItemStack(ModBlocks.engine, 1, EngineType.VAMPIRE.getMeta()); ItemNBTHelper.setInteger(vampireEngineTier1, "Tier", 1);
		ItemStack vampireEngineTier2 = new ItemStack(ModBlocks.engine, 1, EngineType.VAMPIRE.getMeta()); ItemNBTHelper.setInteger(vampireEngineTier2, "Tier", 2);

		addShapedRecipe(vampireEngineTier0, new Object[]{"CCC", "LFL", "IPI", 'I', dIronPlate, 'L', new ItemStack(Items.SKULL, 1, 1), 'P', ItemUtil.copy(tier0CU, 1), 'C', Blocks.NETHER_BRICK, 'F', machineFrame});
		addShapedRecipe(vampireEngineTier1, new Object[]{"EEE", "EPE", "EEE", 'E', vampireEngineTier0, 'P', ItemUtil.copy(tier2CU, 1)});
		addShapedRecipe(vampireEngineTier2, new Object[]{"EEE", "EPE", "EEE", 'E', vampireEngineTier1, 'P', ItemUtil.copy(tier3CU, 1)});
		
		addShapedRecipe(new ItemStack(ModBlocks.crystalMachine, 1, MachineType.FURNACE.getMeta()), new Object[]{"III", "IFI", "IPI", 'I', dIronPlate, 'F', Blocks.FURNACE, 'P', ItemUtil.copy(tier0CU, 1)});
		addShapedRecipe(new ItemStack(ModBlocks.crystalMachine, 1, MachineType.PRESS.getMeta()), new Object[]{"IPI", "ICI", "III", 'I', dIronIngot, 'P', Blocks.PISTON, 'C', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE});
		addShapedRecipe(new ItemStack(ModBlocks.crystalMachine, 1, MachineType.LIQUIDIZER.getMeta()), new Object[]{"III", "PFB", "ICI", 'I', dIronPlate, 'P', Blocks.PISTON, 'B', Items.BUCKET, 'C', ItemUtil.copy(tier0CU, 1), 'F', machineFrame});
		addShapedRecipe(new ItemStack(ModBlocks.crystalMachine, 1, MachineType.INFUSER.getMeta()), new Object[]{"ICI", "IFI", "IPI", 'I', dIronPlate, 'C', ModBlocks.cauldron, 'P', ItemUtil.copy(tier0CU, 1), 'F', machineFrame});
		addShapedRecipe(new ItemStack(ModBlocks.crystalMachine, 1, MachineType.GRINDER.getMeta()), new Object[]{"III", "GFG", "IPI", 'I', dIronPlate, 'G', Items.FLINT, 'P', ItemUtil.copy(tier0CU, 1), 'F', machineFrame});
		addShapedRecipe(new ItemStack(ModBlocks.sapExtractor), new Object[]{"#M#", "DFD", "#P#", 'D', dIronPlate, '#', purePlate, 'M', new ItemStack(ModBlocks.crystalMachine, 1, MachineType.LIQUIDIZER.getMeta()), 'P', ItemUtil.copy(fluidPipe, 1), 'F', machineFrame});

		addShapedRecipe(new ItemStack(ModBlocks.dnaMachine), new Object[]{"III", "GFS", "IPI", 'I', dIronPlate, 'G', new ItemStack(ModItems.dnaItems, 1, DNAItemType.EMPTY_SYRINGE.getMetadata()), 'S', new ItemStack(ModItems.dnaItems, 1, DNAItemType.SAMPLE_EMPTY.getMetadata()), 'P', ItemUtil.copy(tier0CU, 1), 'F', machineFrame});
		addShapedOreRecipe(new ItemStack(ModItems.dnaItems, 4, DNAItemType.EMPTY_SYRINGE.getMetadata()), "IG ", "GSG", " GS", 'I', dIronIngot, 'G', "blockGlassColorless", 'S', "stone");
		addShapedOreRecipe(new ItemStack(ModItems.dnaItems, 8, DNAItemType.SAMPLE_EMPTY.getMetadata()), " GI", "G G", "GG ", 'I', dIronIngot, 'G', "blockGlassColorless");
		
		addShapedOreRecipe(new ItemStack(ModBlocks.advDispenser), new Object[]{"PPP", "PMP", "PDP", 'P', dIronPlate, 'M', "skull", 'D', Blocks.DISPENSER});
		addShapedOreRecipe(new ItemStack(ModBlocks.muffler), new Object[]{"WWW", "NMN", "WWW", 'W', "wool", 'M', machineFrame, 'N', Blocks.NOTEBLOCK});
		addShapedOreRecipe(new ItemStack(ModBlocks.obsidianDispenser), new Object[]{"OOO", "ODO", "OGO", 'O', "obsidian", 'D', Blocks.DISPENSER, 'G', "gemDiamond"});
		addShapedOreRecipe(new ItemStack(ModBlocks.epst), new Object[]{"XEX", "EDE", "XEX", 'X', ModFluids.bucketList.get(ModFluids.fluidXpJuice), 'E', ModFluids.bucketList.get(ModFluids.fluidEnder), 'D', ModBlocks.obsidianDispenser});

		addShapedOreRecipe(new ItemStack(ModBlocks.remover, 1, RemoverType.REDSTONE.getMeta()), new Object[]{"CRC", "RTR", "CRC", 'R', "dustRedstone", 'T', Blocks.TNT, 'C', redPlate});
		addShapedOreRecipe(new ItemStack(ModBlocks.remover, 1, RemoverType.WATER.getMeta()), new Object[]{"CWC", " T ", "CWC", 'W', Items.WATER_BUCKET, 'T', Blocks.TNT, 'C', bluePlate});
		addShapedOreRecipe(new ItemStack(ModBlocks.remover, 1, RemoverType.XP.getMeta()), new Object[]{"CXC", "XTX", "CXC", 'X', Items.EXPERIENCE_BOTTLE, 'T', Blocks.TNT, 'C', greenPlate});
		
		addShapedOreRecipe(new ItemStack(ModBlocks.particleThrower), new Object[]{" E ", "BTB", " C ", 'E', "enderpearl", 'T', Blocks.TNT, 'C', Items.END_CRYSTAL, 'B', ModFluids.bucketList.get(ModFluids.fluidEnder)});
		
		ItemStack darkCluster = BlockCrystalCluster.createCluster(new ClusterData(22, 1), TimeUtil.MINECRAFT_DAY_TICKS);
		darkCluster.setItemDamage(EnumClusterType.DARK.getMeta());		
		addShapedOreRecipe(new ItemStack(ModBlocks.darkInfection), new Object[]{"BSB", "SCS", "BNB", 'S', new ItemStack(Items.SKULL, 1, 1), 'B', new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.DARK.getMeta()), 'C', darkCluster, 'N', Items.NETHER_STAR});
		
		addShapedRecipe(new ItemStack(ModBlocks.denseDarkness, 16, 0), new Object[]{"PCP", "CIC", "PCP", 'P', dIronPlate, 'C', darkCrystal, 'I', new ItemStack(ModBlocks.darkInfection, 1, InfectedBlockType.CASING.getMeta())});
		
		addShapedRecipe(new ItemStack(ModBlocks.oppositeFuser, 1, 0), new Object[]{"DSP", "#T#", "PWD", '#', dIronPlate, 'D', darkCrystal, 'P', pureCrystal, 'S', new ItemStack(Items.SKULL, 1, 0), 'W', new ItemStack(Items.SKULL, 1, 1), 'T', Blocks.TNT});
		addShapedRecipe(new ItemStack(ModBlocks.oppositeFuser, 1, 1), new Object[]{"DTP", "SFW", "PND", 'F', new ItemStack(ModBlocks.oppositeFuser, 1, 0), 'D', darkIngot, 'P', pureIngot, 'S', new ItemStack(Items.SKULL, 1, 0), 'W', new ItemStack(Items.SKULL, 1, 1), 'T', Blocks.TNT, 'N', Items.NETHER_STAR});
		addShapedRecipe(new ItemStack(ModBlocks.oppositeFuser, 1, 2), new Object[]{"DTP", "NFS", "PTD", 'F', new ItemStack(ModBlocks.oppositeFuser, 1, 1), 'D', darkBlock, 'P', pureBlock, 'S', new ItemStack(Items.SKULL, 1, 5), 'T', Blocks.TNT, 'N', Items.NETHER_STAR});

		addShapedOreRecipe(new ItemStack(ModBlocks.advancedLamp, 1, LampType.PURE.getMeta()), new Object[]{"CGC", "PLP", "CGC", 'C', pureCrystal, 'G', Items.PRISMARINE_CRYSTALS, 'P', purePlate, 'L', Blocks.SEA_LANTERN});
		addShapedOreRecipe(new ItemStack(ModBlocks.advancedLamp, 1, LampType.DARK.getMeta()), new Object[]{"CSC", "PLP", "CSC", 'C', darkCrystal, 'S', new ItemStack(Items.SKULL, 1, 1), 'P', darkPlate, 'L', Blocks.SOUL_SAND});

		addShapedRecipe(new ItemStack(ModBlocks.converter, 1, ConverterType.CU.getMeta()), new Object[]{"III", "RFC", "III", 'I', dIronPlate, 'C', ItemUtil.copy(tier0CU, 1), 'R', ItemUtil.copy(tier0RF, 1), 'F', machineFrame});
		addShapedRecipe(new ItemStack(ModBlocks.converter, 1, ConverterType.RF.getMeta()), new Object[]{"III", "CFR", "III", 'I', dIronPlate, 'C', ItemUtil.copy(tier0CU, 1), 'R', ItemUtil.copy(tier0RF, 1), 'F', machineFrame});
		
		addShapedOreRecipe(new ItemStack(ModBlocks.entityHopper), new Object[]{"PBP", "IHI", "III", 'I', dIronPlate, 'H', Blocks.HOPPER, 'P', Blocks.STONE_PRESSURE_PLATE, 'B', Blocks.IRON_BARS});

		//EnderIO before default enderpearl
		String ender = getBestOreID("ingotPulsatingIron", "enderpearl");
		CrystalChestType.registerBlocksAndRecipes(ModBlocks.crystalChest);
		WoodenCrystalChestType.registerBlocksAndRecipes(ModBlocks.crystalWoodenChest);
		for(int i = 0; i < 16; i++){
			int code = WirelessChestHelper.getDefaultCode(EnumDyeColor.byMetadata(i));
			ItemStack wChest = new ItemStack(ModBlocks.wirelessChest);
			ItemNBTHelper.setInteger(wChest, WirelessChestHelper.NBT_CODE, code);
			ItemStack wMinecart = new ItemStack(ModItems.wirelessChestMinecart);
			ItemNBTHelper.setInteger(wMinecart, WirelessChestHelper.NBT_CODE, code);
			ItemStack chestStack = new ItemStack(ModBlocks.crystalChest, 1, CrystalChestType.DARKIRON.ordinal());
			
			addShapedOreRecipe(wChest, new Object[] {"W", "C", "E", 'W', "wool"+dyeOreNames[15-i], 'C', chestStack, 'E', "chestEnder"});
			addShapedNBTRecipe(wMinecart, new Object[] {"A", "B", 'A', wChest, 'B', Items.MINECART});
			
			ItemStack buffer = new ItemStack(ModBlocks.enderBuffer);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("Code", code);
			ItemNBTHelper.getCompound(buffer).setTag(BlockMachine.TILE_NBT_STACK, nbt);			
			GameRegistry.addRecipe(new EnderBufferCustomRecipe(buffer, new Object[]{"ECE", "RFP", "ETE", 'E', ender, 'P', ItemUtil.copy(tier2CU, 1), 'R', ItemUtil.copy(tier2RF, 1), 'T', new ItemStack(ModBlocks.crystalTank, 1, TankType.GREEN.getMeta()), 'C', wChest, 'F', machineFrameEnder}));
		}
		
		ItemStack leatherWolfArmor = new ItemStack(ModItems.wolfArmor);
		ItemNBTHelper.setString(leatherWolfArmor, "ArmorID", WolfArmor.LEATHER.name().toLowerCase());
		addShapedOreRecipe(leatherWolfArmor, new Object[]{"ICH", "BLB", 'I', "leather", 'C', Items.LEATHER_CHESTPLATE, 'H', Items.LEATHER_HELMET, 'B', Items.LEATHER_BOOTS, 'L', "leather"});
		ItemStack chainWolfArmor = new ItemStack(ModItems.wolfArmor);
		ItemNBTHelper.setString(chainWolfArmor, "ArmorID", WolfArmor.CHAIN.name().toLowerCase());
		addShapedOreRecipe(chainWolfArmor, new Object[]{"ICH", "BLB", 'I', "ingotIron", 'C', Items.CHAINMAIL_CHESTPLATE, 'H', Items.CHAINMAIL_HELMET, 'B', Items.CHAINMAIL_BOOTS, 'L', "leather"});
		ItemStack ironWolfArmor = new ItemStack(ModItems.wolfArmor);
		ItemNBTHelper.setString(ironWolfArmor, "ArmorID", WolfArmor.IRON.name().toLowerCase());
		addShapedOreRecipe(ironWolfArmor, new Object[]{"ICH", "BLB", 'I', "ingotIron", 'C', Items.IRON_CHESTPLATE, 'H', Items.IRON_HELMET, 'B', Items.IRON_BOOTS, 'L', "leather"});
		ItemStack dironWolfArmor = new ItemStack(ModItems.wolfArmor);
		ItemNBTHelper.setString(dironWolfArmor, "ArmorID", WolfArmor.DIRON.name().toLowerCase());
		addShapedOreRecipe(dironWolfArmor, new Object[]{"ICH", "BLB", 'I', "ingotIronDark", 'C', ModItems.darkIronChestplate, 'H', ModItems.darkIronHelmet, 'B', ModItems.darkIronBoots, 'L', "leather"});
		ItemStack diamondWolfArmor = new ItemStack(ModItems.wolfArmor);
		ItemNBTHelper.setString(diamondWolfArmor, "ArmorID", WolfArmor.DIAMOND.name().toLowerCase());
		addShapedOreRecipe(diamondWolfArmor, new Object[]{"ICH", "BLB", 'I', "gemDiamond", 'C', Items.DIAMOND_CHESTPLATE, 'H', Items.DIAMOND_HELMET, 'B', Items.DIAMOND_BOOTS, 'L', "leather"});
		ItemStack goldWolfArmor = new ItemStack(ModItems.wolfArmor);
		ItemNBTHelper.setString(goldWolfArmor, "ArmorID", WolfArmor.GOLD.name().toLowerCase());
		addShapedOreRecipe(goldWolfArmor, new Object[]{"ICH", "BLB", 'I', "ingotGold", 'C', Items.GOLDEN_CHESTPLATE, 'H', Items.GOLDEN_HELMET, 'B', Items.GOLDEN_BOOTS, 'L', "leather"});
		
		addShapedRecipe(ModItems.horseShoes, new Object[]{"N N", "N N", " P ", 'N', dIronNugget, 'P', dIronPlate});
		
		
		addShapedRecipe(ModItems.enderChestMinecart, new Object[] {"A", "B", 'A', Blocks.ENDER_CHEST, 'B', Items.MINECART});
		
		addShapedOreRecipe(ModItems.minionStaff, new Object[]{" NE", " GN", "G  ", 'N', greenNugget, 'E', "gemEmerald", 'G', "ingotGold"});
		addShapedOreRecipe(ItemMinion.createMinion(MinionType.BASIC), new Object[]{" S ", "LBL", "B B", 'S', new ItemStack(Items.SKULL, 1, 0), 'B', "bone", 'L', "leather"});
		addShapelessRecipe(ItemMinion.createMinion(MinionType.WORKER), new Object[]{ItemMinion.createMinion(MinionType.BASIC), Items.IRON_PICKAXE});
		addShapelessRecipe(ItemMinion.createMinion(MinionType.WARRIOR), new Object[]{ItemMinion.createMinion(MinionType.BASIC), Items.IRON_SWORD});

		addShapedRecipe(new ItemStack(ModBlocks.worksite, 1, WorksiteType.ANIMAL_FARM.getMeta()), new Object[]{"PSP", "BFB", "PPP", 'P', dIronPlate, 'S', new ItemStack(Items.SKULL, 1, 0), 'B', Items.BEEF, 'F', machineFrame});
		addShapedOreRecipe(new ItemStack(ModBlocks.worksite, 1, WorksiteType.CROP_FARM.getMeta()), new Object[]{"PSP", "WFW", "PPP", 'P', dIronPlate, 'S', new ItemStack(Items.SKULL, 1, 0), 'W', "cropWheat", 'F', machineFrame});
		addShapedOreRecipe(new ItemStack(ModBlocks.worksite, 1, WorksiteType.TREE_FARM.getMeta()), new Object[]{"PSP", "TFT", "PPP", 'P', dIronPlate, 'S', new ItemStack(Items.SKULL, 1, 0), 'T', "treeSapling", 'F', machineFrame});
		
		addShapedRecipe(new ItemStack(ModItems.worksiteUpgrade, 1, WorksiteUpgrade.SIZE_MEDIUM.ordinal()), new Object[]{" F ", "FPF", " F ", 'P', dIronPlate, 'F', Blocks.OAK_FENCE});
		addShapedRecipe(new ItemStack(ModItems.worksiteUpgrade, 1, WorksiteUpgrade.SIZE_LARGE.ordinal()), new Object[]{" F ", "FPF", " F ", 'P', new ItemStack(ModItems.worksiteUpgrade, 1, WorksiteUpgrade.SIZE_MEDIUM.ordinal()), 'F', Blocks.OAK_FENCE});
		addShapelessRecipe(new ItemStack(ModItems.worksiteUpgrade, 1, WorksiteUpgrade.QUARRY_MEDIUM.ordinal()), new Object[]{Items.IRON_PICKAXE, new ItemStack(ModItems.worksiteUpgrade, 1, WorksiteUpgrade.SIZE_MEDIUM.ordinal())});
		addShapelessRecipe(new ItemStack(ModItems.worksiteUpgrade, 1, WorksiteUpgrade.QUARRY_LARGE.ordinal()), new Object[]{Items.IRON_PICKAXE, new ItemStack(ModItems.worksiteUpgrade, 1, WorksiteUpgrade.SIZE_LARGE.ordinal())});
		addShapedRecipe(new ItemStack(ModItems.worksiteUpgrade, 1, WorksiteUpgrade.ENCHANTED_TOOLS_1.ordinal()), new Object[]{" B ", "NPN", " N ", 'P', dIronPlate, 'B', Items.ENCHANTED_BOOK.getEnchantedItemStack(new EnchantmentData(Enchantments.FORTUNE, 1)), 'N', dIronNugget});
		addShapedRecipe(new ItemStack(ModItems.worksiteUpgrade, 1, WorksiteUpgrade.ENCHANTED_TOOLS_2.ordinal()), new Object[]{" B ", "NPN", " N ", 'P', dIronPlate, 'B', Items.ENCHANTED_BOOK.getEnchantedItemStack(new EnchantmentData(Enchantments.FORTUNE, 2)), 'N', dIronNugget});
		addShapedRecipe(new ItemStack(ModItems.worksiteUpgrade, 1, WorksiteUpgrade.ENCHANTED_TOOLS_3.ordinal()), new Object[]{" B ", "NPN", " N ", 'P', dIronPlate, 'B', Items.ENCHANTED_BOOK.getEnchantedItemStack(new EnchantmentData(Enchantments.FORTUNE, 3)), 'N', dIronNugget});
		addShapedOreRecipe(new ItemStack(ModItems.worksiteUpgrade, 1, WorksiteUpgrade.TOOL_QUALITY_1.ordinal()), new Object[]{" P ", "SDA", " I ", 'P', Items.IRON_PICKAXE, 'S', Items.IRON_SHOVEL, 'D', dIronPlate, 'A', Items.IRON_AXE, 'I', "ingotIron"});
		addShapedOreRecipe(new ItemStack(ModItems.worksiteUpgrade, 1, WorksiteUpgrade.TOOL_QUALITY_2.ordinal()), new Object[]{" P ", "SDA", " I ", 'P', Items.DIAMOND_PICKAXE, 'S', Items.DIAMOND_SHOVEL, 'D', dIronPlate, 'A', Items.DIAMOND_AXE, 'I', "gemDiamond"});
		addShapedOreRecipe(new ItemStack(ModItems.worksiteUpgrade, 1, WorksiteUpgrade.BASIC_CHUNK_LOADER.ordinal()), new Object[]{" N ", "EPE", " N ", 'P', dIronPlate, 'E', ender, 'N', dIronNugget});
		addShapedOreRecipe(new ItemStack(ModItems.worksiteUpgrade, 1, WorksiteUpgrade.QUARRY_CHUNK_LOADER.ordinal()), new Object[]{" N ", "EPE", " N ", 'P', new ItemStack(ModItems.worksiteUpgrade, 1, WorksiteUpgrade.BASIC_CHUNK_LOADER.ordinal()), 'E', "endereye", 'N', pureNugget});
		addShapedRecipe(new ItemStack(ModItems.worksiteUpgrade, 1, WorksiteUpgrade.QUARRY_SILK_TOUCH.ordinal()), new Object[]{" B ", "NPN", " N ", 'P', dIronPlate, 'B', Items.ENCHANTED_BOOK.getEnchantedItemStack(new EnchantmentData(Enchantments.SILK_TOUCH, 1)), 'N', dIronNugget});
		addShapedOreRecipe(new ItemStack(ModItems.worksiteUpgrade, 1, WorksiteUpgrade.QUARRY_ONLY_ORE.ordinal()), new Object[]{" O ", "NPN", " N ", 'P', dIronPlate, 'O', "oreIron", 'N', dIronNugget});

		addShapedOreRecipe(new ItemStack(ModItems.congealedRedstone, 2), new Object[]{"RSR", "SRS", "RSR", 'R', "dustRedstone", 'S', new ItemStack(ModItems.crystalSap, 1, SapType.RED.getMetadata())});
		addShapedOreRecipe(new ItemStack(ModBlocks.redstoneCore), new Object[]{"RSR", "SBS", "RSR", 'R', Items.BLAZE_ROD, 'S', new ItemStack(ModItems.crystalSap, 1, SapType.RED.getMetadata()), 'B', "blockRedstone"});
		addShapedOreRecipe(new ItemStack(ModBlocks.redstoneReactor), new Object[]{"DPD", "GCG", "DPD", 'C', ModBlocks.redstoneCore, 'D', new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.DARKIRON.getMeta()), 'G', "gemDiamond", 'P', darkPlate});

		addShapedOreRecipe(new ItemStack(ModItems.reactorUpgrade, 1, UpgradeType.BASIC.getMetadata()), new Object[]{" C ", "RBT", "DLE", 'C', darkCluster, 'R', Items.REPEATER, 'B', new ItemStack(ModBlocks.battery, 1, BatteryType.DARK.getMeta()), 'T', new ItemStack(ModBlocks.crystalTank, 1, TankType.GREEN.getMeta()), 'D', "gemDiamond", 'L', Items.BLAZE_ROD, 'E', "gemEmerald"});
		addShapedOreRecipe(new ItemStack(ModItems.reactorUpgrade, 1, UpgradeType.ADVANCED.getMetadata()), new Object[]{"BCI", "RUT", "DLE", 'C', darkCluster, 'R', Items.COMPARATOR, 'B', new ItemStack(ModBlocks.battery, 1, BatteryType.DARK.getMeta()), 'T', new ItemStack(ModBlocks.crystalTank, 1, TankType.GREEN.getMeta()), 'D', "gemDiamond", 'L', Items.BLAZE_ROD, 'E', "gemEmerald", 'I', new ItemStack(ModBlocks.crystalMachine, 1, MachineType.INFUSER.getMeta()), 'U', new ItemStack(ModItems.reactorUpgrade, 1, UpgradeType.BASIC.getMetadata())});
		addShapedOreRecipe(new ItemStack(ModItems.reactorUpgrade, 1, UpgradeType.SUPER.getMetadata()), new Object[]{"BCI", "RUT", "DLE", 'C', darkCluster, 'R', Blocks.DAYLIGHT_DETECTOR, 'B', new ItemStack(ModBlocks.battery, 1, BatteryType.DARK.getMeta()), 'T', new ItemStack(ModBlocks.crystalTank, 1, TankType.GREEN.getMeta()), 'D', "gemDiamond", 'L', Items.BLAZE_ROD, 'E', "gemEmerald", 'I', ModBlocks.sapExtractor, 'U', new ItemStack(ModItems.reactorUpgrade, 1, UpgradeType.ADVANCED.getMetadata())});

		addShapedOreRecipe(new ItemStack(ModBlocks.decorativeBlock, 4, DecorativeBlockType.REDSTONE_BRICKS.getMeta()), new Object[]{"RBR", "BRB", "RBR", 'R', new ItemStack(ModItems.congealedRedstone), 'B', "ingotBrickNether"});
		addShapedRecipe(new ItemStack(ModBlocks.redstoneSponge), new Object[]{"MRM", "RSR", "MRM", 'R', new ItemStack(ModItems.congealedRedstone), 'M', Blocks.MAGMA, 'S', Blocks.SPONGE});

		addShapedRecipe(new ItemStack(ModItems.engineCore, 1, EngineCoreType.FINITE.getMetadata()), new Object[]{"IDF", "DCP", "FPI", 'I', itemPipe, 'F', fluidPipe, 'D', darkCrystal, 'P', pureCrystal, 'C', Blocks.ICE});
		addShapedRecipe(new ItemStack(ModItems.engineCore, 1, EngineCoreType.INFINITE.getMetadata()), new Object[]{"IPF", "PCD", "FDI", 'I', itemPipe, 'F', fluidPipe, 'D', darkCrystal, 'P', pureCrystal, 'C', Blocks.MAGMA});

		addShapedRecipe(new ItemStack(ModBlocks.specialengine, 1, SpecialEngineType.FINITE.getMeta()), new Object[]{"PCP", "DMD", "PDP", 'P', dIronPlate, 'C', new ItemStack(ModItems.engineCore, 1, EngineCoreType.FINITE.getMetadata()), 'D', darkBlock, 'M', machineFrameEnder});
		addShapedRecipe(new ItemStack(ModBlocks.specialengine, 1, SpecialEngineType.INFINITE.getMeta()), new Object[]{"PCP", "DMD", "PDP", 'P', dIronPlate, 'C', new ItemStack(ModItems.engineCore, 1, EngineCoreType.INFINITE.getMetadata()), 'D', darkBlock, 'M', machineFrameEnder});
		
		//Crystex Recipes
		addShapedRecipe(new ItemStack(ModItems.crystexItems, 6, CrystexItemType.CRYSTEXUS.getMetadata()), new Object[]{"GEG", "CLC", "GEG", 'G', Blocks.GLOWSTONE, 'E', Items.END_CRYSTAL, 'C', Items.PRISMARINE_CRYSTALS, 'L', new ItemStack(ModBlocks.advancedLamp, 1, LampType.PURE.getMeta())});
		ItemStack crystexus = new ItemStack(ModItems.crystexItems, 1, CrystexItemType.CRYSTEXUS.getMetadata());
		ItemStack crystexiumEssence = new ItemStack(ModItems.crystexItems, 1, CrystexItemType.CRYSTEXIUM_ESSENCE.getMetadata());
		ItemStack crystexiumEssenceBlue = new ItemStack(ModItems.crystexItems, 1, CrystexItemType.CRYSTEXIUM_ESSENCE_BLUE.getMetadata());
		ItemStack crystexiumEssenceRed = new ItemStack(ModItems.crystexItems, 1, CrystexItemType.CRYSTEXIUM_ESSENCE_RED.getMetadata());
		ItemStack crystexiumEssenceGreen = new ItemStack(ModItems.crystexItems, 1, CrystexItemType.CRYSTEXIUM_ESSENCE_GREEN.getMetadata());
		ItemStack crystexiumEssenceDark = new ItemStack(ModItems.crystexItems, 1, CrystexItemType.CRYSTEXIUM_ESSENCE_DARK.getMetadata());
		ItemStack crystexiumEssencePure = new ItemStack(ModItems.crystexItems, 1, CrystexItemType.CRYSTEXIUM_ESSENCE_PURE.getMetadata());
		ItemStack crystexiumBrick = new ItemStack(ModItems.crystexItems, 1, CrystexItemType.CRYSTEXIUM_BRICK.getMetadata());
		ItemStack crystexiumBrickBlue = new ItemStack(ModItems.crystexItems, 1, CrystexItemType.CRYSTEXIUM_BRICK_BLUE.getMetadata());
		ItemStack crystexiumBrickRed = new ItemStack(ModItems.crystexItems, 1, CrystexItemType.CRYSTEXIUM_BRICK_RED.getMetadata());
		ItemStack crystexiumBrickGreen = new ItemStack(ModItems.crystexItems, 1, CrystexItemType.CRYSTEXIUM_BRICK_GREEN.getMetadata());
		ItemStack crystexiumBrickDark = new ItemStack(ModItems.crystexItems, 1, CrystexItemType.CRYSTEXIUM_BRICK_DARK.getMetadata());
		ItemStack crystexiumBrickPure = new ItemStack(ModItems.crystexItems, 1, CrystexItemType.CRYSTEXIUM_BRICK_PURE.getMetadata());

		ItemStack crystexiumBlock = new ItemStack(ModBlocks.crystexiumBlock, 1, CrystexiumBlockType.NORMAL.getMeta());
		ItemStack crystexiumBlockBlue = new ItemStack(ModBlocks.blueCrystexiumBlock, 1, CrystexiumBlockType.NORMAL.getMeta());
		ItemStack crystexiumBlockRed = new ItemStack(ModBlocks.redCrystexiumBlock, 1, CrystexiumBlockType.NORMAL.getMeta());
		ItemStack crystexiumBlockGreen = new ItemStack(ModBlocks.greenCrystexiumBlock, 1, CrystexiumBlockType.NORMAL.getMeta());
		ItemStack crystexiumBlockDark = new ItemStack(ModBlocks.darkCrystexiumBlock, 1, CrystexiumBlockType.NORMAL.getMeta());
		ItemStack crystexiumBlockPure = new ItemStack(ModBlocks.pureCrystexiumBlock, 1, CrystexiumBlockType.NORMAL.getMeta());

		addShapedRecipe(new ItemStack(ModBlocks.crystexiumBlock, 4, CrystexiumBlockType.NORMAL.getMeta()), new Object[]{"SBS", "RCG", "SDS", 'S', Blocks.GLOWSTONE, 'B', blueCrystal, 'R', redCrystal, 'G', greenCrystal, 'D', darkCrystal, 'C', crystexus});
		addShapedRecipe(new ItemStack(ModBlocks.blueCrystexiumBlock, 4, CrystexiumBlockType.NORMAL.getMeta()), new Object[]{"GXG", "XCX", "GXG", 'G', Blocks.GLOWSTONE, 'X', blueCrystal, 'C', crystexus});
		addShapedRecipe(new ItemStack(ModBlocks.redCrystexiumBlock, 4, CrystexiumBlockType.NORMAL.getMeta()), new Object[]{"GXG", "XCX", "GXG", 'G', Blocks.GLOWSTONE, 'X', redCrystal, 'C', crystexus});
		addShapedRecipe(new ItemStack(ModBlocks.greenCrystexiumBlock, 4, CrystexiumBlockType.NORMAL.getMeta()), new Object[]{"GXG", "XCX", "GXG", 'G', Blocks.GLOWSTONE, 'X', greenCrystal, 'C', crystexus});
		addShapedRecipe(new ItemStack(ModBlocks.darkCrystexiumBlock, 4, CrystexiumBlockType.NORMAL.getMeta()), new Object[]{"GXG", "XCX", "GXG", 'G', Blocks.GLOWSTONE, 'X', darkCrystal, 'C', crystexus});
		addShapedRecipe(new ItemStack(ModBlocks.pureCrystexiumBlock, 4, CrystexiumBlockType.NORMAL.getMeta()), new Object[]{"GXG", "XCX", "GXG", 'G', Blocks.GLOWSTONE, 'X', pureCrystal, 'C', crystexus});

		addShapelessRecipe(new ItemStack(ModItems.crystexItems, 2, CrystexItemType.CRYSTEXIUM_BRICK.getMetadata()), new Object[]{crystexiumEssence, crystexiumEssence});
		addShapelessRecipe(new ItemStack(ModItems.crystexItems, 2, CrystexItemType.CRYSTEXIUM_BRICK_BLUE.getMetadata()), new Object[]{crystexiumEssenceBlue, crystexiumEssenceBlue});
		addShapelessRecipe(new ItemStack(ModItems.crystexItems, 2, CrystexItemType.CRYSTEXIUM_BRICK_RED.getMetadata()), new Object[]{crystexiumEssenceRed, crystexiumEssenceRed});
		addShapelessRecipe(new ItemStack(ModItems.crystexItems, 2, CrystexItemType.CRYSTEXIUM_BRICK_GREEN.getMetadata()), new Object[]{crystexiumEssenceGreen, crystexiumEssenceGreen});
		addShapelessRecipe(new ItemStack(ModItems.crystexItems, 2, CrystexItemType.CRYSTEXIUM_BRICK_DARK.getMetadata()), new Object[]{crystexiumEssenceDark, crystexiumEssenceDark});
		addShapelessRecipe(new ItemStack(ModItems.crystexItems, 2, CrystexItemType.CRYSTEXIUM_BRICK_PURE.getMetadata()), new Object[]{crystexiumEssencePure, crystexiumEssencePure});
		
		addShapedRecipe(new ItemStack(ModBlocks.crystexiumBlock, 1, CrystexiumBlockType.BRICK.getMeta()), new Object[]{"XX", "XX", 'X', crystexiumBrick});
		addShapedRecipe(new ItemStack(ModBlocks.blueCrystexiumBlock, 1, CrystexiumBlockType.BRICK.getMeta()), new Object[]{"XX", "XX", 'X', crystexiumBrickBlue});
		addShapedRecipe(new ItemStack(ModBlocks.redCrystexiumBlock, 1, CrystexiumBlockType.BRICK.getMeta()), new Object[]{"XX", "XX", 'X', crystexiumBrickRed});
		addShapedRecipe(new ItemStack(ModBlocks.greenCrystexiumBlock, 1, CrystexiumBlockType.BRICK.getMeta()), new Object[]{"XX", "XX", 'X', crystexiumBrickGreen});
		addShapedRecipe(new ItemStack(ModBlocks.darkCrystexiumBlock, 1, CrystexiumBlockType.BRICK.getMeta()), new Object[]{"XX", "XX", 'X', crystexiumBrickDark});
		addShapedRecipe(new ItemStack(ModBlocks.pureCrystexiumBlock, 1, CrystexiumBlockType.BRICK.getMeta()), new Object[]{"XX", "XX", 'X', crystexiumBrickPure});

		addShapedRecipe(new ItemStack(ModBlocks.crystexiumBlock, 4, CrystexiumBlockType.SQUARE_BRICK.getMeta()), new Object[]{"XX", "XX", 'X', crystexiumBlock});
		addShapedRecipe(new ItemStack(ModBlocks.blueCrystexiumBlock, 4, CrystexiumBlockType.SQUARE_BRICK.getMeta()), new Object[]{"XX", "XX", 'X', crystexiumBlockBlue});
		addShapedRecipe(new ItemStack(ModBlocks.redCrystexiumBlock, 4, CrystexiumBlockType.SQUARE_BRICK.getMeta()), new Object[]{"XX", "XX", 'X', crystexiumBlockRed});
		addShapedRecipe(new ItemStack(ModBlocks.greenCrystexiumBlock, 4, CrystexiumBlockType.SQUARE_BRICK.getMeta()), new Object[]{"XX", "XX", 'X', crystexiumBlockGreen});
		addShapedRecipe(new ItemStack(ModBlocks.darkCrystexiumBlock, 4, CrystexiumBlockType.SQUARE_BRICK.getMeta()), new Object[]{"XX", "XX", 'X', crystexiumBlockDark});
		addShapedRecipe(new ItemStack(ModBlocks.pureCrystexiumBlock, 4, CrystexiumBlockType.SQUARE_BRICK.getMeta()), new Object[]{"XX", "XX", 'X', crystexiumBlockPure});
		
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic, 4, BasicTileType.STONE_STONE.getMeta()), new Object[]{"XX", "XX", 'X', new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.STONE.getMetadata())});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic, 4, BasicTileType.STONE_CARVED.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(Blocks.STONEBRICK, 1, 3)});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic, 4, BasicTileType.STONE_GRANITE.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.GRANITE_SMOOTH.getMetadata())});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic, 4, BasicTileType.STONE_DIORITE.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.DIORITE_SMOOTH.getMetadata())});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic, 4, BasicTileType.STONE_ANDESITE.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.ANDESITE_SMOOTH.getMetadata())});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic, 4, BasicTileType.COMPRESSED_REDSTONE.getMeta()), new Object[]{"XX", "XX", 'X',  Blocks.REDSTONE_BLOCK});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic, 4, BasicTileType.COMPRESSED_LAPIS.getMeta()), new Object[]{"XX", "XX", 'X',  Blocks.LAPIS_BLOCK});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic, 4, BasicTileType.COMPRESSED_QUARTZ.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(Blocks.QUARTZ_BLOCK, 1, 0)});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic, 4, BasicTileType.COMPRESSED_FLINT.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(ModBlocks.compressed, 1, CompressedBlockType.FLINT.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic, 4, BasicTileType.METAL_IRON.getMeta()), new Object[]{"XX", "XX", 'X',  Blocks.IRON_BLOCK});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic, 4, BasicTileType.METAL_GOLD.getMeta()), new Object[]{"XX", "XX", 'X',  Blocks.GOLD_BLOCK});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic, 4, BasicTileType.METAL_DIAMOND.getMeta()), new Object[]{"XX", "XX", 'X',  Blocks.DIAMOND_BLOCK});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic, 4, BasicTileType.METAL_EMERALD.getMeta()), new Object[]{"XX", "XX", 'X',  Blocks.EMERALD_BLOCK});

		addShapedRecipe(new ItemStack(ModBlocks.tileBasic2, 4, BasicTileType2.LOG_OAK.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.OAK.getMetadata())});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic2, 4, BasicTileType2.LOG_BIRCH.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.BIRCH.getMetadata())});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic2, 4, BasicTileType2.LOG_SPRUCE.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.SPRUCE.getMetadata())});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic2, 4, BasicTileType2.LOG_JUNGLE.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.JUNGLE.getMetadata())});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic2, 4, BasicTileType2.LOG_ACACIA.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(Blocks.LOG2, 1, 0)});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic2, 4, BasicTileType2.LOG_DARK_OAK.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(Blocks.LOG2, 1, 1)});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic2, 4, BasicTileType2.NOTEBLOCK.getMeta()), new Object[]{"XX", "XX", 'X',  Blocks.NOTEBLOCK});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic2, 4, BasicTileType2.PISTON.getMeta()), new Object[]{"XX", "XX", 'X',  Blocks.PISTON});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic2, 4, BasicTileType2.PISTON_STICKY.getMeta()), new Object[]{"XX", "XX", 'X',  Blocks.STICKY_PISTON});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic2, 4, BasicTileType2.SEALANTERN.getMeta()), new Object[]{"XX", "XX", 'X',  Blocks.SEA_LANTERN});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic2, 4, BasicTileType2.FURNACE.getMeta()), new Object[]{"XX", "XX", 'X',  Blocks.FURNACE});
		addShapedRecipe(new ItemStack(ModBlocks.tileBasic2, 4, BasicTileType2.LAMP_OFF.getMeta()), new Object[]{"XX", "XX", 'X',  Blocks.REDSTONE_LAMP});

		addShapedRecipe(new ItemStack(ModBlocks.tileCrystal, 4, CrystalTileType.INGOT_BLUE.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.BLUE.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.tileCrystal, 4, CrystalTileType.INGOT_RED.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.RED.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.tileCrystal, 4, CrystalTileType.INGOT_GREEN.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.GREEN.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.tileCrystal, 4, CrystalTileType.INGOT_DARK.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.DARK.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.tileCrystal, 4, CrystalTileType.INGOT_PURE.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.PURE.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.tileCrystal, 4, CrystalTileType.INGOT_DIRON.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.DARKIRON.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.tileCrystal, 4, CrystalTileType.LOG_BLUE.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(ModBlocks.crystalLog, 1, WoodType.BLUE.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.tileCrystal, 4, CrystalTileType.LOG_RED.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(ModBlocks.crystalLog, 1, WoodType.RED.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.tileCrystal, 4, CrystalTileType.LOG_GREEN.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(ModBlocks.crystalLog, 1, WoodType.GREEN.getMeta())});
		addShapedRecipe(new ItemStack(ModBlocks.tileCrystal, 4, CrystalTileType.LOG_DARK.getMeta()), new Object[]{"XX", "XX", 'X',  new ItemStack(ModBlocks.crystalLog, 1, WoodType.DARK.getMeta())});

		CauldronRecipeManager.initRecipes();
		
		CrystalFurnaceManager.initRecipes();
		PressRecipeManager.initRecipes();
		GrinderManager.initRecipes();
		LiquidizerRecipeManager.initRecipes();
		CrystalInfusionManager.initRecipes();
		ModFusionRecipes.initRecipes();
		ModCrops.recipes();
	}
	
	public static void postInit(){
		addSlabToBlocks();
		GrinderManager.oreSearch();
		PressRecipeManager.oreSearch();
	}
	
	
	public static class EnderBufferCustomRecipe extends ShapedOreRecipe {

		static {
			RecipeSorter.register(CrystalMod.resource("enderbuffer"),  EnderBufferCustomRecipe.class,  SHAPED,  "after:minecraft:shaped");
		}
		
		public EnderBufferCustomRecipe(ItemStack result, Object[] recipe) {
			super(result, recipe);
		}
		
		@Override
		public ItemStack getCraftingResult(InventoryCrafting inv)
	    {
	        ItemStack itemstack = this.getRecipeOutput().copy();
	        for (int i = 0; i < inv.getSizeInventory(); ++i)
	        {
	            ItemStack itemstack1 = inv.getStackInSlot(i);

	            if (ItemStackTools.isValid(itemstack1))
	            {
	            	if(itemstack1.getItem() == Item.getItemFromBlock(ModBlocks.wirelessChest)){
	            		int code = ItemNBTHelper.getInteger(itemstack1, WirelessChestHelper.NBT_CODE, 0);
	            		NBTTagCompound nbt = new NBTTagCompound();
	        			nbt.setInteger("Code", code);
	        			ItemNBTHelper.getCompound(itemstack).setTag(BlockMachine.TILE_NBT_STACK, nbt);
	            	}
	            }
	        }

	        return itemstack;
	    }
		
	}
	public static void addShapedRecipe(Item result, Object... recipe){ addShapedRecipe(new ItemStack(result), recipe); }
	public static void addShapedRecipe(Block result, Object... recipe){ addShapedRecipe(new ItemStack(result), recipe); }
	public static void addShapedRecipe(ItemStack output, Object... params){
		GameRegistry.addShapedRecipe(output, params);
	}
	
	public static void addShapedNBTRecipe(Item result, Object... recipe){ addShapedNBTRecipe(new ItemStack(result), recipe); }
	public static void addShapedNBTRecipe(Block result, Object... recipe){ addShapedNBTRecipe(new ItemStack(result), recipe); }
	public static void addShapedNBTRecipe(ItemStack stack, Object... recipeComponents){
		String s = "";
        int i = 0;
        int j = 0;
        int k = 0;

        if (recipeComponents[i] instanceof String[])
        {
            String[] astring = ((String[])recipeComponents[i++]);

            for (String s2 : astring)
            {
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

        for (int l = 0; l < j * k; ++l)
        {
            char c0 = s.charAt(l);

            if (map.containsKey(Character.valueOf(c0)))
            {
                aitemstack[l] = map.get(Character.valueOf(c0)).copy();
            }
            else
            {
                aitemstack[l] = null;
            }
        }

        ShapedRecipeNBT shapedrecipes = new ShapedRecipeNBT(j, k, aitemstack, stack);
        GameRegistry.addRecipe(shapedrecipes);
	}
	
	public static void addShapelessRecipe(Item result, Object... recipe){ addShapelessRecipe(new ItemStack(result), recipe); }
	public static void addShapelessRecipe(Block result, Object... recipe){ addShapelessRecipe(new ItemStack(result), recipe); }
	public static void addShapelessRecipe(ItemStack output, Object... params){
		GameRegistry.addShapelessRecipe(output, params);
	}
	
	public static void addShapelessNBTRecipe(Item result, Object... recipe){ addShapelessNBTRecipe(new ItemStack(result), recipe); }
	public static void addShapelessNBTRecipe(Block result, Object... recipe){ addShapelessNBTRecipe(new ItemStack(result), recipe); }
	public static void addShapelessNBTRecipe(ItemStack output, Object... params){
		List<ItemStack> list = Lists.<ItemStack>newArrayList();

        for (Object object : params)
        {
            if (object instanceof ItemStack)
            {
                list.add(((ItemStack)object).copy());
            }
            else if (object instanceof Item)
            {
                list.add(new ItemStack((Item)object));
            }
            else
            {
                if (!(object instanceof Block))
                {
                    throw new IllegalArgumentException("Invalid shapeless recipe: unknown type " + object.getClass().getName() + "!");
                }

                list.add(new ItemStack((Block)object));
            }
        }

        GameRegistry.addRecipe(new ShapelessRecipeNBT(output, list));
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
	
	public static void addShapedOreRecipeNBT(Item result, Object... recipe){ addShapedOreRecipeNBT(new ItemStack(result), recipe); }
	public static void addShapedOreRecipeNBT(Block result, Object... recipe){ addShapedOreRecipeNBT(new ItemStack(result), recipe); }
	public static void addShapedOreRecipeNBT(ItemStack result, Object... recipe){
		GameRegistry.addRecipe(new ShapedOreRecipeNBT(result, recipe));
	}
	
	public static void create9x9Recipe(ItemStack output, ItemStack input, int reverseOut){
		addShapedRecipe(output, new Object[]{"###", "###", "###", '#', input});
		ItemStack copy = ItemStackTools.safeCopy(input);
		ItemStackTools.setStackSize(copy, reverseOut);
		addShapelessRecipe(copy, new Object[] { output });
	}
	
	public static String[] dyeOreNames = {
        "Black",
        "Red",
        "Green",
        "Brown",
        "Blue",
        "Purple",
        "Cyan",
        "LightGray",
        "Gray",
        "Pink",
        "Lime",
        "Yellow",
        "LightBlue",
        "Magenta",
        "Orange",
        "White"
    };
	
	public static void initOreDic(){
		oredict(Blocks.CRAFTING_TABLE, "workbench");

		oredict(new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE), "wool");
		
        for(int i = 0; i < 16; i++)
        {
        	EnumDyeColor color = EnumDyeColor.byMetadata(i);
        	ItemStack wool = new ItemStack(Blocks.WOOL, 1, color.getMetadata());
        	oredict(wool, "wool"+dyeOreNames[color.getDyeDamage()]);
        }
		
		for(CrystalChestType chest : CrystalChestType.values()){
			ItemStack cStack = new ItemStack(ModBlocks.crystalChest, 1, chest.ordinal());
			oredict(cStack, "chest");
			oredict(cStack, "chestCrystal");
		}
		
		for(WoodenCrystalChestType chest : WoodenCrystalChestType.values()){
			ItemStack cStack = new ItemStack(ModBlocks.crystalWoodenChest, 1, chest.ordinal());
			oredict(cStack, "chest", "chestWood", "chestWoodCrystal");
		}
		
		oredict(Items.PAPER, "paper");
		oredict(Items.BUCKET, "bucket");
		oredict(Items.ENDER_PEARL, "enderpearl");
		oredict(Items.ENDER_EYE, "endereye");
		oredict(Items.STRING, "string");
		oredict(Items.LEATHER, "leather");
		oredict(Items.SLIME_BALL, "slimeball");
		for(SapType type : SapType.values()){
			oredict(new ItemStack(ModItems.crystalSap, 1, type.getMetadata()), "slimeball", "sap");
		}
		oredict(Items.BLAZE_ROD, "rodBlaze");
		oredict(new ItemStack(Items.SKULL, 1, OreDictionary.WILDCARD_VALUE), "skull");

		oredict(new ItemStack(ModItems.cursedBone, 1, BoneType.BONE.getMetadata()), "bone");
		oredict(new ItemStack(ModItems.cursedBone, 1, BoneType.BONEMEAL.getMetadata()), "dyeBlack");

		oredict(Blocks.PISTON, "piston");
		oredict(Blocks.STICKY_PISTON, "pistonSticky");
		
		oredict(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.BLUE.getMeta()),  "oreCrystalBlue");
		oredict(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.BLUE_NETHER.getMeta()),  "oreCrystalBlue");
		oredict(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.BLUE_END.getMeta()),  "oreCrystalBlue");
		
		oredict(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.RED.getMeta()),  "oreCrystalRed");
		oredict(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.RED_NETHER.getMeta()),  "oreCrystalRed");
		oredict(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.RED_END.getMeta()),  "oreCrystalRed");
		
		oredict(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.GREEN.getMeta()),  "oreCrystalGreen");
		oredict(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.GREEN_NETHER.getMeta()),  "oreCrystalGreen");
		oredict(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.GREEN_END.getMeta()),  "oreCrystalGreen");
		
		oredict(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.DARK.getMeta()),  "oreCrystalDark");
		oredict(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.DARK_NETHER.getMeta()),  "oreCrystalDark");
		oredict(new ItemStack(ModBlocks.crystalOre, 1, CrystalOreType.DARK_END.getMeta()),  "oreCrystalDark");
		
		for(WoodType type : WoodType.values()){
			oredict(new ItemStack(ModBlocks.crystalSapling, 1, type.getMeta()), "treeSapling");
			oredict(new ItemStack(ModBlocks.crystalLeaves, 1, type.getMeta()), "treeLeaves");
			oredict(new ItemStack(ModBlocks.crystalLog, 1, type.getMeta()), "logCrystal", "logWood");
			oredict(new ItemStack(ModBlocks.crystalPlanks, 1, type.getMeta()), "plankCrystal", "plankWood");
		}
		oredict(new ItemStack(ModBlocks.normalSapling, 1, SaplingType.BAMBOO.getMeta()), "treeSapling");
		oredict(new ItemStack(ModBlocks.bambooLeaves), "treeLeaves");
		oredict(new ItemStack(ModBlocks.bamboo), "logBamboo", "logWood");
		oredict(new ItemStack(ModBlocks.bambooPlanks), "plankBamboo", "plankWood");
		
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
		
		oredict(new ItemStack(ModBlocks.compressed, 1, CompressedBlockType.FLINT.getMeta()), "blockFlint");
		oredict(new ItemStack(ModBlocks.compressed, 1, CompressedBlockType.CHARCOAL.getMeta()), "blockCharcoal");
		oredict(new ItemStack(ModBlocks.fallingCompressed, 1, FallingCompressedBlockType.GUNPOWDER.getMeta()), "blockGunpowder");
		oredict(new ItemStack(ModBlocks.fallingCompressed, 1, FallingCompressedBlockType.SUGAR.getMeta()), "blockSugar");
		oredict(new ItemStack(ModBlocks.blazeRodBlock), "blockBlazeRod");
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

	public static String getBestOreID(String...names){
		for(String ore : names){
			NonNullList<ItemStack> ores = OreDictionary.getOres(ore);
			if(!ores.isEmpty()){
				return ore;
			}
		}
		return names[0];
	}
	
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
            String[] astring = ((String[])recipeComponents[i++]);

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
            ItemStack itemstack = ItemStackTools.getEmptyStack();

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
                aitemstack[i1] = map.get(Character.valueOf(c0)).copy();
            }
            else
            {
                aitemstack[i1] = ItemStackTools.getEmptyStack();
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
				if(output != null && ItemStackTools.getStackSize(output) == 6) {
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
								'B', ItemUtil.copy(output, 1));
					}
				}
			}
		}
	}
	
}
