package com.alec_wam.CrystalMod.items.guide;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Loader;

import com.alec_wam.CrystalMod.Config;
import com.alec_wam.CrystalMod.blocks.BlockCrystal.CrystalBlockType;
import com.alec_wam.CrystalMod.blocks.BlockCrystalIngot.CrystalIngotBlockType;
import com.alec_wam.CrystalMod.blocks.BlockCrystalOre.CrystalOreType;
import com.alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;
import com.alec_wam.CrystalMod.blocks.ModBlocks;
import com.alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass.GlassType;
import com.alec_wam.CrystalMod.client.util.comp.BaseComponent;
import com.alec_wam.CrystalMod.client.util.comp.GuiComponentBasicItemPage;
import com.alec_wam.CrystalMod.client.util.comp.GuiComponentBook;
import com.alec_wam.CrystalMod.client.util.comp.GuiComponentStandardRecipePage;
import com.alec_wam.CrystalMod.items.IEnumMetaItem;
import com.alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import com.alec_wam.CrystalMod.items.ItemIngot.IngotType;
import com.alec_wam.CrystalMod.items.ItemMetalPlate.PlateType;
import com.alec_wam.CrystalMod.items.ModItems;
import com.alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import com.alec_wam.CrystalMod.tiles.machine.crafting.BlockCrystalMachine.MachineType;
import com.alec_wam.CrystalMod.tiles.machine.power.engine.BlockEngine.EngineType;
import com.alec_wam.CrystalMod.tiles.pipes.BlockPipe.PipeType;
import com.alec_wam.CrystalMod.tiles.pipes.covers.ItemPipeCover;
import com.alec_wam.CrystalMod.tiles.pipes.item.filters.ItemPipeFilter.FilterType;
import com.alec_wam.CrystalMod.tiles.spawner.ItemMobEssence;
import com.alec_wam.CrystalMod.tiles.tank.BlockTank;
import com.alec_wam.CrystalMod.tiles.tank.BlockTank.TankType;
import com.alec_wam.CrystalMod.tiles.workbench.BlockCrystalWorkbench.WorkbenchType;
import com.alec_wam.CrystalMod.util.ItemNBTHelper;
import com.alec_wam.CrystalMod.util.Lang;
import com.google.common.collect.Lists;

public class GuidePages {

	public static final List<BaseComponent> blockData = new ArrayList<BaseComponent>();
	public static final List<BaseComponent> itemData = new ArrayList<BaseComponent>();
	public static final List<BaseComponent> entityData = new ArrayList<BaseComponent>();
	public static final List<BaseComponent> workbenchData = new ArrayList<BaseComponent>();
	
	public static final List<BaseComponent> eStorageBlockData = new ArrayList<BaseComponent>();
	public static final List<BaseComponent> eStorageItemData = new ArrayList<BaseComponent>();
	
	public static void createPages(){
		blockData.clear();
		itemData.clear();
		entityData.clear();
		workbenchData.clear();
		
		GuiComponentBasicItemPage pageOre = getBasicPage("crystalOre", getEnumBlocks(ModBlocks.crystalOre, CrystalOreType.values()));
		pageOre.setDescription(String.format(pageOre.getDescription(), ""+Config.oreMinimumHeight, ""+Config.oreMaximumHeight), true);
		blockData.add(pageOre);
		
		CrystalBlockType[] normalArray = new CrystalBlockType[]{CrystalBlockType.BLUE, CrystalBlockType.RED, CrystalBlockType.GREEN, CrystalBlockType.DARK, CrystalBlockType.PURE};
		blockData.add(getRecipePage("crystalBlock", getEnumBlocks(ModBlocks.crystal, normalArray))); 
		
		CrystalBlockType[] chisledArray = new CrystalBlockType[]{CrystalBlockType.BLUE_CHISELED, CrystalBlockType.RED_CHISELED, CrystalBlockType.GREEN_CHISELED, CrystalBlockType.DARK_CHISELED, CrystalBlockType.PURE_CHISELED};
		blockData.add(getRecipePage("crystalBlockChisled", getEnumBlocks(ModBlocks.crystal, chisledArray))); 
		
		CrystalBlockType[] brickArray = new CrystalBlockType[]{CrystalBlockType.BLUE_BRICK, CrystalBlockType.RED_BRICK, CrystalBlockType.GREEN_BRICK, CrystalBlockType.DARK_BRICK, CrystalBlockType.PURE_BRICK};
		blockData.add(getRecipePage("crystalBlockBrick", getEnumBlocks(ModBlocks.crystal, brickArray))); 
		
		blockData.add(getRecipePage("crystalIngotBlock", getEnumBlocks(ModBlocks.crystalIngot, CrystalIngotBlockType.values()))); 
		blockData.add(getRecipePage("crystalGlass", getEnumBlocks(ModBlocks.crystalGlass, GlassType.values()))); 

		blockData.add(getRecipePage("crystalWorkbench", getEnumBlocks(ModBlocks.crystalWorkbench, WorkbenchType.values()))); 
		CrystalChestType[] chestArray = new CrystalChestType[]{CrystalChestType.DARKIRON, CrystalChestType.BLUE, CrystalChestType.RED, CrystalChestType.GREEN, CrystalChestType.DARK, CrystalChestType.PURE};
		blockData.add(getRecipePage("crystalChest", getEnumSpecial(ModBlocks.crystalChest, chestArray))); 

		blockData.add(getRecipePage("pipeItem", new ItemStack(ModBlocks.crystalPipe, 1, PipeType.ITEM.getMeta()))); 
		blockData.add(getRecipePage("pipeFluid", new ItemStack(ModBlocks.crystalPipe, 1, PipeType.FLUID.getMeta()))); 
		List<ItemStack> powerPipesCU = Lists.newArrayList();
		ItemStack pipeCUStack = new ItemStack(ModBlocks.crystalPipe, 1, PipeType.POWERCU.getMeta());
		List<ItemStack> powerPipesRF = Lists.newArrayList();
		ItemStack pipeRFStack = new ItemStack(ModBlocks.crystalPipe, 1, PipeType.POWERRF.getMeta());
		for(int i = 0; i < 4; i++){
			ItemStack pipe = pipeCUStack.copy();
			ItemNBTHelper.setInteger(pipe, "Tier", i);
			powerPipesCU.add(pipe);
			ItemStack pipe2 = pipeRFStack.copy();
			ItemNBTHelper.setInteger(pipe2, "Tier", i);
			powerPipesRF.add(pipe2);
		}
		GuiComponentStandardRecipePage pagePipePowerCU = getRecipePage("pipePowerCU", powerPipesCU);
		pagePipePowerCU.setDescription(String.format(pagePipePowerCU.getDescription(), ""+Config.powerConduitTierOneCU, ""+Config.powerConduitTierTwoCU, ""+Config.powerConduitTierThreeCU, ""+Config.powerConduitTierFourCU), true);
		blockData.add(pagePipePowerCU); 
		GuiComponentStandardRecipePage pagePipePowerRF = getRecipePage("pipePowerRF", powerPipesRF);
		pagePipePowerRF.setDescription(String.format(pagePipePowerRF.getDescription(), ""+Config.powerConduitTierOneRF, ""+Config.powerConduitTierTwoRF, ""+Config.powerConduitTierThreeRF, ""+Config.powerConduitTierFourRF), true);
		blockData.add(pagePipePowerRF); 
		GuiComponentStandardRecipePage pageTank = getRecipePage("tank", getEnumBlocks(ModBlocks.crystalTank, TankType.values()));
		pageTank.setDescription(String.format(pageTank.getDescription(), (BlockTank.tankCaps[0]*Fluid.BUCKET_VOLUME)+"mB", (BlockTank.tankCaps[1]*Fluid.BUCKET_VOLUME)+"mB", (BlockTank.tankCaps[2]*Fluid.BUCKET_VOLUME)+"mB", (BlockTank.tankCaps[3]*Fluid.BUCKET_VOLUME)+"mB", (BlockTank.tankCaps[4]*Fluid.BUCKET_VOLUME)+"mB"), true);
		blockData.add(pageTank); 
		blockData.add(getRecipePage("engineFurnace", new ItemStack(ModBlocks.engine, 1, EngineType.FURNACE.getMeta()))); 
		blockData.add(getRecipePage("engineLava", new ItemStack(ModBlocks.engine, 1, EngineType.LAVA.getMeta()))); 
		blockData.add(getRecipePage("weather", ModBlocks.weather)); 
		blockData.add(getRecipePage("cauldron", ModBlocks.cauldron)); 
		blockData.add(getRecipePage("spawner", ModBlocks.customSpawner));
		blockData.add(getRecipePage("reinforcedRail", ModBlocks.darkIronRail));
		blockData.add(getRecipePage("pipeEStorage", new ItemStack(ModBlocks.crystalPipe, 1, PipeType.ESTORAGE.getMeta())));
		
		blockData.add(getRecipePage("converter", ModBlocks.converter)); 
		blockData.add(getRecipePage("invCharger", ModBlocks.invCharger)); 
		blockData.add(getRecipePage("enderBuffer", ModBlocks.enderBuffer)); 
		blockData.add(getRecipePage("battery", ModBlocks.battery)); 
		blockData.add(getRecipePage("elevator", ModBlocks.elevator)); 
		blockData.add(getRecipePage("elevatorCaller", ModBlocks.elevatorCaller)); 
		blockData.add(getRecipePage("elevatorFloor", ModBlocks.elevatorFloor)); 

		
		blockData.add(getRecipePage("machineFurnace", new ItemStack(ModBlocks.crystalMachine, 1, MachineType.FURNACE.getMeta()))); 
		blockData.add(getRecipePage("machinePress", new ItemStack(ModBlocks.crystalMachine, 1, MachineType.PRESS.getMeta()))); 
		blockData.add(getRecipePage("machineLiquidizer", new ItemStack(ModBlocks.crystalMachine, 1, MachineType.LIQUIDIZER.getMeta()))); 
		blockData.add(getRecipePage("machineInfuser", new ItemStack(ModBlocks.crystalMachine, 1, MachineType.INFUSER.getMeta()))); 
		
		blockData.add(getRecipePage("machineGrinder", ModBlocks.mobGrinder)); 
		
		//ITEMS
		
		//TODO Add Furnace Page
		CrystalType[] crystalArray = new CrystalType[]{CrystalType.BLUE, CrystalType.RED, CrystalType.GREEN, CrystalType.DARK};
		itemData.add(getBasicPage("crystal", getEnumItems(ModItems.crystals, crystalArray)));
		GuiComponentStandardRecipePage pagePureCrystal = getRecipePage("crystalPure", new ItemStack(ModItems.crystals, 1, CrystalType.PURE.getMetadata()));
		if(Loader.isModLoaded("tconstruct"))pagePureCrystal.setDescription(String.format(pagePureCrystal.getDescription(), Lang.localize("guide.page.crystalPure.desc2")), true);
		itemData.add(pagePureCrystal);
		
		IngotType[] ingotArray = new IngotType[]{IngotType.BLUE, IngotType.RED, IngotType.GREEN, IngotType.DARK, IngotType.PURE};
		itemData.add(getBasicPage("crystalIngot", getEnumItems(ModItems.ingots, ingotArray)));
		itemData.add(getBasicPage("darkIronIngot", new ItemStack(ModItems.ingots, 1, IngotType.DARK_IRON.getMetadata())));
		//TODO Add Press Page
		itemData.add(getBasicPage("metalPlate", getEnumItems(ModItems.plates, PlateType.values())));

		itemData.add(getBasicPage("crystalReeds", ModItems.crystalReeds));
		List<ItemStack> seeds = Lists.newArrayList();
		seeds.add(new ItemStack(ModItems.crystalSeedsBlue));
		seeds.add(new ItemStack(ModItems.crystalSeedsRed));
		seeds.add(new ItemStack(ModItems.crystalSeedsGreen));
		seeds.add(new ItemStack(ModItems.crystalSeedsDark));
		itemData.add(getRecipePage("crystalSeeds", seeds));
		
		itemData.add(getRecipePage("wrench", ModItems.wrench));
		
		List<ItemStack> shearList = Lists.newArrayList();
		ItemStack shearStack = new ItemStack(ModItems.shears);
		for(String color : ModItems.shears.getColors()){
			ItemStack shear = shearStack.copy();
			ItemNBTHelper.setString(shear, "Color", color);
			shearList.add(shear);
		}
		itemData.add(getRecipePage("shears", shearList));
		
		itemData.add(getRecipePage("backpack", ModItems.backpack));
		itemData.add(getRecipePage("telePearl", ModItems.telePearl));
		itemData.add(getRecipePage("pipeCover", ItemPipeCover.getCoverForBlock(Blocks.STONE.getDefaultState())));
		itemData.add(getRecipePage("pipeFilter", getEnumSpecial(ModItems.pipeFilter, FilterType.values())));
		
		itemData.add(getBasicPage("wings", ModItems.wings));
		itemData.add(getBasicPage("mobEssence", ItemMobEssence.createStack("Pig")));
		
		initEStorage();
	}
	
	public static void initEStorage(){
		eStorageBlockData.clear();
		eStorageItemData.clear();
	}
	
	public static List<ItemStack> getEnumBlocks(Block obj, IEnumMeta[] array){
		List<ItemStack> list = Lists.newArrayList();
		for(IEnumMeta type : array){
			list.add(new ItemStack(obj, 1, type.getMeta()));
		}
		return list;
	}
	
	public static List<ItemStack> getEnumItems(Item obj, IEnumMetaItem[] array){
		List<ItemStack> list = Lists.newArrayList();
		for(IEnumMetaItem type : array){
			list.add(new ItemStack(obj, 1, type.getMetadata()));
		}
		return list;
	}
	
	public static List<ItemStack> getEnumSpecial(Block obj, Enum<?>[] array){
		List<ItemStack> list = Lists.newArrayList();
		for(Enum<?> type : array){
			list.add(new ItemStack(obj, 1, type.ordinal()));
		}
		return list;
	}
	
	public static List<ItemStack> getEnumSpecial(Item obj, Enum<?>[] array){
		List<ItemStack> list = Lists.newArrayList();
		for(Enum<?> type : array){
			list.add(new ItemStack(obj, 1, type.ordinal()));
		}
		return list;
	}
	
	public static GuiComponentStandardRecipePage getRecipePage(String pageID, Object obj){
		GuiComponentStandardRecipePage recipe = GuiComponentBook.getStandardRecipeComp("guide.page."+pageID+".title", "guide.page."+pageID+".desc", obj);
		return recipe;
	}
	
	public static GuiComponentBasicItemPage getBasicPage(String pageID, Object obj){
		List<ItemStack> list = Lists.newArrayList();
		if (obj instanceof ItemStack) {
			list.add((ItemStack)obj);
		}
		if(obj instanceof List){
			@SuppressWarnings("unchecked")
			List<ItemStack> oList = (List<ItemStack>)obj;
			list.addAll(oList);
		}
		if (obj instanceof Item) {
			list.add(new ItemStack((Item)obj));
		} else if (obj instanceof Block) {
			list.add(new ItemStack((Block)obj));
		}
		GuiComponentBasicItemPage recipe = new GuiComponentBasicItemPage(Lang.localize("guide.page."+pageID+".title"), Lang.localize("guide.page."+pageID+".desc"), list);
		return recipe;
	}
	
}
