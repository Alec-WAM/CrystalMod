package alec_wam.CrystalMod.items.guide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.api.guide.GuideChapter;
import alec_wam.CrystalMod.api.guide.GuideIndex;
import alec_wam.CrystalMod.api.guide.GuidePage;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.BlockCrystal.CrystalBlockType;
import alec_wam.CrystalMod.blocks.BlockCrystalIngot.CrystalIngotBlockType;
import alec_wam.CrystalMod.blocks.BlockCrystalLog;
import alec_wam.CrystalMod.blocks.BlockCrystalOre.CrystalOreType;
import alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;
import alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass.GlassType;
import alec_wam.CrystalMod.client.util.comp.BaseComponent;
import alec_wam.CrystalMod.client.util.comp.GuiComponentBasicItemPage;
import alec_wam.CrystalMod.client.util.comp.GuiComponentBook;
import alec_wam.CrystalMod.client.util.comp.GuiComponentStandardRecipePage;
import alec_wam.CrystalMod.items.IEnumMetaItem;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ItemIngot.IngotType;
import alec_wam.CrystalMod.items.ItemMetalPlate.PlateType;
import alec_wam.CrystalMod.items.guide.page.PageCrafting;
import alec_wam.CrystalMod.items.guide.page.PageFurnace;
import alec_wam.CrystalMod.items.guide.page.PageIcon;
import alec_wam.CrystalMod.items.guide.page.PagePress;
import alec_wam.CrystalMod.items.guide.page.PageText;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackNormal.CrystalBackpackType;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ItemBackpackUpgrade.BackpackUpgrade;
import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestHelper;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCrystalMachine.MachineType;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.BlockEnderBuffer;
import alec_wam.CrystalMod.tiles.machine.power.engine.BlockEngine.EngineType;
import alec_wam.CrystalMod.tiles.pipes.BlockPipe.PipeType;
import alec_wam.CrystalMod.tiles.pipes.covers.ItemPipeCover;
import alec_wam.CrystalMod.tiles.pipes.item.filters.ItemPipeFilter.FilterType;
import alec_wam.CrystalMod.tiles.spawner.ItemMobEssence;
import alec_wam.CrystalMod.tiles.tank.BlockTank;
import alec_wam.CrystalMod.tiles.tank.BlockTank.TankType;
import alec_wam.CrystalMod.tiles.workbench.BlockCrystalWorkbench.WorkbenchType;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GuidePages {

	public static final List<BaseComponent> blockData = new ArrayList<BaseComponent>();
	public static final List<BaseComponent> itemData = new ArrayList<BaseComponent>();
	public static final List<BaseComponent> entityData = new ArrayList<BaseComponent>();
	public static final List<BaseComponent> workbenchData = new ArrayList<BaseComponent>();
	
	public static final List<BaseComponent> eStorageBlockData = new ArrayList<BaseComponent>();
	public static final List<BaseComponent> eStorageItemData = new ArrayList<BaseComponent>();
	
	public static void createPages(){
		CrystalModAPI.GUIDE_INDEXES.clear();
		CrystalModAPI.BLOCKS = CrystalModAPI.regiterGuideIndex(new GuideIndex("blocks"));
		CrystalModAPI.ITEMS = CrystalModAPI.regiterGuideIndex(new GuideIndex("items"));
		CrystalModAPI.ENTITES = CrystalModAPI.regiterGuideIndex(new GuideIndex("entites"));
		CrystalModAPI.WORKBENCH = CrystalModAPI.regiterGuideIndex(new GuideIndex("workbench"));
		CrystalModAPI.MISC = CrystalModAPI.regiterGuideIndex(new GuideIndex("misc"));
		
		CrystalType[] crystalArray = new CrystalType[]{CrystalType.BLUE, CrystalType.RED, CrystalType.GREEN, CrystalType.DARK};
		List<ItemStack> crystalFullList = getEnumItems(ModItems.crystals, crystalArray);
		
		List<ItemStack> oreList = getEnumBlocks(ModBlocks.crystalOre, CrystalOreType.values());
		Map<String, List<?>> lookUp = Maps.newHashMap();
		lookUp.put("0", oreList);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystalore", new PageIcon("0", oreList), new PageFurnace("smelt", crystalFullList)).setDisplayObject(oreList).setLookUpData(lookUp));

		
		CrystalBlockType[] normalArray = new CrystalBlockType[]{CrystalBlockType.BLUE, CrystalBlockType.RED, CrystalBlockType.GREEN, CrystalBlockType.DARK};
		CrystalBlockType[] chisledArray = new CrystalBlockType[]{CrystalBlockType.BLUE_CHISELED, CrystalBlockType.RED_CHISELED, CrystalBlockType.GREEN_CHISELED, CrystalBlockType.DARK_CHISELED};
		CrystalBlockType[] brickArray = new CrystalBlockType[]{CrystalBlockType.BLUE_BRICK, CrystalBlockType.RED_BRICK, CrystalBlockType.GREEN_BRICK, CrystalBlockType.DARK_BRICK};
		List<ItemStack> normalBlocksDisplayList = getEnumBlocks(ModBlocks.crystal, new CrystalBlockType[]{CrystalBlockType.BLUE, CrystalBlockType.BLUE_CHISELED, CrystalBlockType.BLUE_BRICK});
		GuideChapter chapterCrystalBlock = new GuideChapter("crystalblock", new PageCrafting("normal", getEnumBlocks(ModBlocks.crystal, normalArray)), new PageIcon("chiseled", getEnumBlocks(ModBlocks.crystal, chisledArray)), new PageCrafting("brick", getEnumBlocks(ModBlocks.crystal, brickArray))).setDisplayObject(normalBlocksDisplayList);
		CrystalModAPI.BLOCKS.registerChapter(chapterCrystalBlock);
		
		List<ItemStack> ingotBlockList = getEnumBlocks(ModBlocks.crystalIngot, CrystalIngotBlockType.values());
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystalingotblock", new PageCrafting("main", ingotBlockList)).setDisplayObject(ingotBlockList));
		
		List<ItemStack> glassList = getEnumBlocks(ModBlocks.crystalGlass, new GlassType[]{GlassType.BLUE, GlassType.RED, GlassType.GREEN, GlassType.DARK});
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystalglass", new PageCrafting("main", glassList)).setDisplayObject(glassList));
		
		List<ItemStack> workbenchList = getEnumBlocks(ModBlocks.crystalWorkbench, WorkbenchType.values());
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystalworkbench", new PageCrafting("main", workbenchList)).setDisplayObject(workbenchList));
		
		List<ItemStack> chestList = getEnumSpecial(ModBlocks.crystalChest, new CrystalChestType[]{CrystalChestType.DARKIRON, CrystalChestType.BLUE, CrystalChestType.RED, CrystalChestType.GREEN, CrystalChestType.DARK, CrystalChestType.PURE});
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystalchest", new PageCrafting("main", chestList)).setDisplayObject(chestList));

		List<ItemStack> reedList = Lists.newArrayList();
		reedList.add(new ItemStack(ModItems.crystalReedsBlue));
		reedList.add(new ItemStack(ModItems.crystalReedsRed));
		reedList.add(new ItemStack(ModItems.crystalReedsGreen));
		reedList.add(new ItemStack(ModItems.crystalReedsDark));
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystalreeds", new PageIcon("main", reedList)).setDisplayObject(reedList));
		
		ItemStack lilyPad = new ItemStack(ModBlocks.flowerLilypad);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("flowerlilypad", new PageCrafting("main", Collections.singletonList(lilyPad))).setDisplayObject(lilyPad));
		
		List<ItemStack> plantList = Lists.newArrayList();
		plantList.add(new ItemStack(ModItems.crystalSeedsBlue));
		plantList.add(new ItemStack(ModItems.crystalSeedsRed));
		plantList.add(new ItemStack(ModItems.crystalSeedsGreen));
		plantList.add(new ItemStack(ModItems.crystalSeedsDark));
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystalplant", new PageIcon("main", plantList)).setDisplayObject(plantList));
		
		List<ItemStack> listSaplings = getEnumBlocks(ModBlocks.crystalSapling, BlockCrystalLog.WoodType.values());
		List<ItemStack> listLogs = getEnumBlocks(ModBlocks.crystalLog, BlockCrystalLog.WoodType.values());
		List<ItemStack> treePlantList = Lists.newArrayList();
		treePlantList.add(new ItemStack(ModItems.crystalTreeSeedsBlue));
		treePlantList.add(new ItemStack(ModItems.crystalTreeSeedsRed));
		treePlantList.add(new ItemStack(ModItems.crystalTreeSeedsGreen));
		treePlantList.add(new ItemStack(ModItems.crystalTreeSeedsDark));
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystaltrees", new PageIcon("main", listSaplings), new PageIcon("logs", listLogs), new PageIcon("treeplant", treePlantList)).setDisplayObject(listSaplings));

		List<ItemStack> listTanks = getEnumBlocks(ModBlocks.crystalTank, new TankType[]{TankType.BLUE, TankType.RED, TankType.GREEN, TankType.DARK});
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("crystaltank", new PageCrafting("main", listTanks)).setDisplayObject(listTanks));

		ItemStack weather = new ItemStack(ModBlocks.weather);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("weatherforcaster", new PageCrafting("main", Collections.singletonList(weather))).setDisplayObject(weather));

		ItemStack spawner = new ItemStack(ModBlocks.customSpawner);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("spawner", new PageCrafting("main", Collections.singletonList(spawner)), new PageText("upgrades")).setDisplayObject(spawner));

		ItemStack mobgrinder = new ItemStack(ModBlocks.mobGrinder);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("mobgrinder", new PageCrafting("main", Collections.singletonList(mobgrinder))).setDisplayObject(mobgrinder));

		ItemStack elevator = new ItemStack(ModBlocks.elevator);
		ItemStack elevatorFloor = new ItemStack(ModBlocks.elevatorFloor);
		ItemStack elevatorCaller = new ItemStack(ModBlocks.elevatorCaller);
		List<ItemStack> listElevators = Lists.newArrayList();
		listElevators.add(elevator); listElevators.add(elevatorFloor); listElevators.add(elevatorCaller);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("elevator", new PageCrafting("elevator", Collections.singletonList(elevator)), new PageCrafting("floor", Collections.singletonList(elevatorFloor)), new PageCrafting("caller", Collections.singletonList(elevatorCaller))).setDisplayObject(listElevators));

		List<ItemStack> enderBuffers = Lists.newArrayList();
		for(int i = 0; i < 16; i++){
			int code = WirelessChestHelper.getDefaultCode(EnumDyeColor.byMetadata(i));
			ItemStack enderbuffer = new ItemStack(ModBlocks.enderBuffer);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("Code", code);
			ItemNBTHelper.getCompound(enderbuffer).setTag(BlockEnderBuffer.TILE_NBT_STACK, nbt);
			enderBuffers.add(enderbuffer);
		}
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("enderbuffer", new PageCrafting("main", enderBuffers)).setDisplayObject(enderBuffers));
		
		ItemStack advdispenser = new ItemStack(ModBlocks.advDispenser);
		CrystalModAPI.BLOCKS.registerChapter(new GuideChapter("advdispenser", new PageCrafting("main", Collections.singletonList(advdispenser)), new PageText("modes")).setDisplayObject(advdispenser));

		
		CrystalType[] nuggetArray = new CrystalType[]{CrystalType.BLUE_NUGGET, CrystalType.RED_NUGGET, CrystalType.GREEN_NUGGET, CrystalType.DARK_NUGGET};
		CrystalType[] shardArray = new CrystalType[]{CrystalType.BLUE_SHARD, CrystalType.RED_SHARD, CrystalType.GREEN_SHARD, CrystalType.DARK_SHARD};

		List<ItemStack> shardList = getEnumItems(ModItems.crystals, shardArray);
		List<ItemStack> crystalList = getEnumItems(ModItems.crystals, new CrystalType[] {CrystalType.BLUE, CrystalType.BLUE_NUGGET, CrystalType.BLUE_SHARD});
		List<ItemStack> nuggetList = getEnumItems(ModItems.crystals, nuggetArray);
		GuideChapter chapterCrystals = new GuideChapter("crystals", new PageIcon("shards", shardList), new PageCrafting("craftShard", shardList), new PageIcon("nuggets", nuggetList), new PageFurnace("smeltNugget", nuggetList), new PageIcon("crystal", crystalFullList), new PageFurnace("smeltCrystal", crystalFullList)).setDisplayObject(crystalList);
		CrystalModAPI.ITEMS.registerChapter(chapterCrystals);
		
		IngotType[] ingotArray = new IngotType[]{IngotType.BLUE, IngotType.RED, IngotType.GREEN, IngotType.DARK};
		List<ItemStack> ingotList = getEnumItems(ModItems.ingots, ingotArray);
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("crystalingots", new PageIcon("0", ingotList), new PageFurnace("smelt", ingotList)).setDisplayObject(ingotList));
		
		//TODO Move Plate Page to Press Page
		PlateType[] plateArray = new PlateType[]{PlateType.BLUE, PlateType.RED, PlateType.GREEN, PlateType.DARK};
		List<ItemStack> plateList = getEnumItems(ModItems.plates, plateArray);
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("metalplate", new PageIcon("main", plateList), new PagePress("press", plateList)).setDisplayObject(plateList));

		List<ItemStack> darkArmorList = Lists.newArrayList();
		darkArmorList.add(new ItemStack(ModItems.darkIronHelmet));
		darkArmorList.add(new ItemStack(ModItems.darkIronChestplate));
		darkArmorList.add(new ItemStack(ModItems.darkIronLeggings));
		darkArmorList.add(new ItemStack(ModItems.darkIronBoots));
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("darkarmor", 
				new PageCrafting("main", darkArmorList)).setDisplayObject(darkArmorList));
		
		List<ItemStack> darkToolList = Lists.newArrayList();
		darkToolList.add(new ItemStack(ModItems.darkIronSword));
		darkToolList.add(new ItemStack(ModItems.darkIronPickaxe));
		darkToolList.add(new ItemStack(ModItems.darkIronShovel));
		darkToolList.add(new ItemStack(ModItems.darkIronAxe));
		darkToolList.add(new ItemStack(ModItems.darkIronHoe));
		final List<ItemStack> basicDarkTools = Lists.newArrayList(darkToolList);
		darkToolList.add(new ItemStack(ModItems.darkIronBow));
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("darktools", 
				new PageCrafting("main", basicDarkTools), 
				new PageCrafting("bow", new ItemStack(ModItems.darkIronBow))).setDisplayObject(darkToolList));
		
		List<ItemStack> backpacks = getEnumItems(ModItems.normalBackpack, CrystalBackpackType.values());
		List<ItemStack> backpackUpgrades = getEnumItems(ModItems.backpackupgrade, BackpackUpgrade.values());
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("backpacknormal", new PageCrafting("main", backpacks), new PageCrafting("upgrades", backpackUpgrades)).setDisplayObject(backpacks));

		ItemStack lock = new ItemStack(ModItems.lock);
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("lock", new PageCrafting("main", Collections.singletonList(lock))).setDisplayObject(lock));

		ItemStack telePearl = new ItemStack(ModItems.telePearl);
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("telepearl", new PageIcon("main", Collections.singletonList(telePearl))).setDisplayObject(telePearl));
		
		ItemStack superTorch = new ItemStack(ModItems.superTorch);
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("supertorch", new PageCrafting("main", Collections.singletonList(superTorch))).setDisplayObject(superTorch));
		
		ItemStack wings = new ItemStack(ModItems.wings);
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("wings", new PageIcon("main", Collections.singletonList(wings))).setDisplayObject(wings));
		
		List<ItemStack> essenceList = Lists.newArrayList();
		essenceList.add(ItemMobEssence.createStack("Pig"));
		CrystalModAPI.ITEMS.registerChapter(new GuideChapter("mobessence", new PageIcon("main", essenceList)).setDisplayObject(essenceList));
		
		blockData.clear();
		itemData.clear();
		entityData.clear();
		workbenchData.clear();

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
		itemData.add(getBasicPage("crystal", getEnumItems(ModItems.crystals, crystalArray)));
		GuiComponentStandardRecipePage pagePureCrystal = getRecipePage("crystalPure", new ItemStack(ModItems.crystals, 1, CrystalType.PURE.getMetadata()));
		if(Loader.isModLoaded("tconstruct"))pagePureCrystal.setDescription(String.format(pagePureCrystal.getDescription(), Lang.localize("guide.page.crystalPure.desc2")), true);
		itemData.add(pagePureCrystal);
		
		itemData.add(getBasicPage("crystalIngot", getEnumItems(ModItems.ingots, ingotArray)));
		itemData.add(getBasicPage("darkIronIngot", new ItemStack(ModItems.ingots, 1, IngotType.DARK_IRON.getMetadata())));
		//TODO Add Press Page
		itemData.add(getBasicPage("metalPlate", getEnumItems(ModItems.plates, PlateType.values())));

		itemData.add(getBasicPage("crystalReeds", ModItems.crystalReedsBlue));
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
	
	public static class LookupResult {
		
		private final GuideChapter chapter;
		private final GuidePage page;
		
		public LookupResult(GuideChapter chapter, GuidePage page){
			this.chapter = chapter;
			this.page = page;
		}

		public GuideChapter getChapter() {
			return chapter;
		}

		public GuidePage getPage() {
			return page;
		}
		
	}
	
	public static LookupResult getGuideData(EntityPlayer player, Object object){
		GuideChapter finalChapter = null;
		GuidePage finalPage = null;
		all : for(GuideChapter chapter : CrystalModAPI.GUIDE_CHAPTERS){
			Map<String, List<?>> lookUpData = chapter.getLookUpData();
			for(Entry<String, List<?>> entry : lookUpData.entrySet()){
				GuidePage page = chapter.getPage(entry.getKey());
				if(page !=null){
					List<?> objects = entry.getValue();
					for(Object obj : objects){
						if(obj instanceof ItemStack){
							if(object instanceof ItemStack){
								if(ItemUtil.canCombine((ItemStack)obj, (ItemStack)object)){
									finalChapter = chapter;
									finalPage = page;
									break all;
								}
							}
						}
						if(obj instanceof String){
							if(object instanceof ItemStack){
								if(ItemUtil.itemStackMatchesOredict((ItemStack)object, (String)obj)){
									finalChapter = chapter;
									finalPage = page;
									break all;
								}
							}
						}
					}
				}
			}
		}
		
		return new LookupResult(finalChapter, finalPage);
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
