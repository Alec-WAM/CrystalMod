package alec_wam.CrystalMod;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.handler.EventHandler.ItemDropType;
import alec_wam.CrystalMod.tiles.machine.power.converter.PowerUnits;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.StringUtils;
import alec_wam.CrystalMod.world.generation.CrystalOreFeature;
import alec_wam.CrystalMod.world.generation.CrystalReedsFeature;
import alec_wam.CrystalMod.world.generation.CrystalTreeFeature;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Config {
	public static final String CATEGORY_GENERAL = "general";
	public static final String CATEGORY_CLIENT = "client";
	public static final String CATEGORY_WORLD = "world";
	public static final String CATEGORY_ITEM = "items";
	public static final String CATEGORY_BLOCKS = "blocks";
	public static final String CATEGORY_ENTITY = "entities";
	public static final String CATEGORY_MINIONS = "minions";
	public static final String CATEGORY_MACHINE = "machines";
	
	//CLIENT
	public static boolean vanillaMinecarts3d = true;
	public static boolean vanillaBoats3d = true;
	public static boolean useRemoteManualFile = true;
	
	//WORLD
	//Disabled by default until things get fixed
	public static boolean enableBambooForest = false;
	public static boolean generateOreOverworld = true;
	public static int oreMinimumVeinSize = 5;
    public static int oreMaximumVeinSize = 8;
    public static int oreMaximumVeinCount = 2;
    public static int oreMinimumHeight = 0;
    public static int oreMaximumHeight = 20;
    
    public static boolean generateOreNether = true;
    public static int oreNetherMinimumVeinSize = 5;
    public static int oreNetherMaximumVeinSize = 8;
    public static int oreNetherMaximumVeinCount = 4;
    public static int oreNetherMinimumHeight = 20;
    public static int oreNetherMaximumHeight = 120;
    
    public static boolean generateOreEnd = true;
    public static int oreEndMinimumVeinSize = 5;
    public static int oreEndMaximumVeinSize = 8;
    public static int oreEndMaximumVeinCount = 4;
    public static int oreEndMinimumHeight = 0;
    public static int oreEndMaximumHeight = 70;
    
    public static boolean generateOreOther = true;
    public static int oreOtherMinimumVeinSize = 5;
    public static int oreOtherMaximumVeinSize = 8;
    public static int oreOtherMaximumVeinCount = 2;
    public static int oreOtherMinimumHeight = 0;
    public static int oreOtherMaximumHeight = 20;
    
    public static boolean generateFusionTemple = true;
    
    public static boolean generateOverworldWell = true;
    public static int overworldWellChance = 250;
    public static boolean generateNetherWell = true;
    public static int netherWellChance = 500;
    public static boolean generateEndWell = true;
    public static int endWellChance = 100;    

    public static boolean generateNetherCrysineMushrooms = true;
    public static int giantCrysineMushroomChance = 15;
    
    public static boolean retrogenInfo = false;
    public static String retrogenID = "generated";
    public static boolean retrogenOres = false;
    public static boolean retrogenTrees = false;
    public static boolean retrogenReeds = false;
    public static int maximumReedsPerChunk = 5;
    public static int reedPlacementTrys = 10;
    public static boolean retrogenClusters = false;
    public static int clusterSpawnChance = 24;
    public static int clusterSpawnTries = 8;

    public static boolean generateSeaweed = true;
    public static boolean retrogenSeaweed = false;
    public static boolean generateKelp = true;
    public static boolean retrogenKelp = false;
    public static int coralChance = 20;
    public static boolean retrogenCoral = false;

    public static boolean generateRoses = true;
    public static boolean retrogenRoses = false;
    
    public static final ResourceLocation[] defaultEnhancementBookLootList = new ResourceLocation[]{
    		LootTableList.CHESTS_ABANDONED_MINESHAFT,
    		LootTableList.CHESTS_IGLOO_CHEST,
    		LootTableList.CHESTS_DESERT_PYRAMID,
    		LootTableList.CHESTS_JUNGLE_TEMPLE,
    		LootTableList.CHESTS_NETHER_BRIDGE,
    		LootTableList.CHESTS_SIMPLE_DUNGEON,
    		LootTableList.CHESTS_VILLAGE_BLACKSMITH
    };
    public static List<ResourceLocation> enhancementBookLootLocationList = Lists.newArrayList();
	public static int enhancementBookRarity = 10;
	public static int whiteFishRarity = 10;
	
	public static int playerCubePlayerLimit = 16;
    
	
	//ENTITY
	public static ItemDropType mobHeadType = ItemDropType.KILLED;
	public static int mobHeadDropChance = 200;
	public static ItemDropType playerHeadType = ItemDropType.ALL;
	public static int playerHeadDropChance = 50;
	public static boolean dragonWingsDrop = true;
	public static boolean darkBonesDrop = true;
	public static int batWingDropChance = 5;
	
	//ITEMS
	public static boolean backpackDeathUpgradeConsume = false;
	public static int superTorchMaxCount = 128;
	public static int tool_pureDamageAddition = 1000;
	
	public static String[] hoeStrings = new String[] {
		"minecraft:wooden_hoe", "minecraft:stone_hoe", "minecraft:iron_hoe", "minecraft:diamond_hoe", "minecraft:golden_hoe",
		"MekanismTools:ObsidianHoe", "MekanismTools:LapisLazuliHoe", "MekanismTools:OsmiumHoe", "MekanismTools:BronzeHoe", "MekanismTools:GlowstoneHoe",
		"MekanismTools:SteelHoe",
		"actuallyadditions:itemHoeEmerald", "actuallyadditions:itemHoeObsidian", "actuallyadditions:itemHoeQuartz", 
		"actuallyadditions:itemHoeCrystalRed", "actuallyadditions:itemHoeCrystalBlue", "actuallyadditions:itemHoeCrystalLightBlue", "actuallyadditions:itemHoeCrystalBlack", "actuallyadditions:itemHoeCrystalGreen", "actuallyadditions:itemHoeCrystalWhite",
		"Steamcraft:hoeBrass", "Steamcraft:hoeGildedGold",
		"Railcraft:tool.steel.hoe",
		"TConstruct:mattock",
		"CrystalMod:crystalhoe",
		"appliedenergistics2:item.ToolCertusQuartzHoe", "appliedenergistics2:item.ToolNetherQuartzHoe",
		"ProjRed|Exploration:projectred.exploration.hoeruby", "ProjRed|Exploration:projectred.exploration.hoesapphire",
		"ProjRed|Exploration:projectred.exploration.hoeperidot",
		"magicalcrops:magicalcrops_AccioHoe", "magicalcrops:magicalcrops_CrucioHoe", "magicalcrops:magicalcrops_ImperioHoe",
		// disabled as it is currently not unbreaking as advertised "magicalcrops:magicalcrops_ZivicioHoe",
		"BiomesOPlenty:hoeAmethyst", "BiomesOPlenty:hoeMud",
		"Eln:Eln.Copper Hoe",
		"Thaumcraft:ItemHoeThaumium", "Thaumcraft:ItemHoeElemental", "Thaumcraft:ItemHoeVoid",
		"ThermalFoundation:tool.hoeInvar"
	};
	public static List<ItemStack> farmHoes = new ArrayList<ItemStack>();
	
	//MACHINES
	public static int powerConduitTierOneCU = 640;
	public static int powerConduitTierTwoCU = 5120;
	public static int powerConduitTierThreeCU = 20480;
	public static int powerConduitTierFourCU = 40960;	
	public static int powerConduitTierOneRF = 640;
	public static int powerConduitTierTwoRF = 5120;
	public static int powerConduitTierThreeRF = 20480;
	public static int powerConduitTierFourRF = 40960;
	
	public static int engine_vampire_maxattack = 16;
	public static int advDispenser_cooldown = 40; //2 seconds
	
	public static int xpFountain_powerNeeded = 3000;
	
	//BLOCKS
	public static boolean crates_leaveOneItem = true;
	public static boolean crates_useAllSides = false;
	public static boolean crates_3dItem = true;
	public static boolean crates_3dBlock = true;
	public static boolean hardmode_MaterialCrops = false;
	public static enum RegenType {
		IDLE, EMPTY, NEVER;
	}	
	public static RegenType crystalClusterRegenType = RegenType.IDLE;
	public static int infectionRange = 30;
	public static int infectionEncasingRange = 30;
	public static int infectionEncasingSize = 5;
	public static boolean dyeFromCoral = true;

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if(event.getModID().equals(CrystalMod.MODID)) {
			ModLogger.info("Updating crystalmod config...");
			CrystalMod.proxy.readMainConfig();
		}
	}
	
    public static void init(Configuration cfg) {
    	//WORLD
    	enableBambooForest = cfg.get(CATEGORY_WORLD, "enableBambooForest", enableBambooForest, "Enable or disable Bamboo Forest").getBoolean();
    	generateOreOverworld = cfg.get(CATEGORY_WORLD, "generateOreOverworld", generateOreOverworld, "Enable or disable Crystal Ore in the Overworld").getBoolean();
    	oreMinimumVeinSize = cfg.get(CATEGORY_WORLD, "oreMinimumVeinSize", oreMinimumVeinSize,
                                     "Minimum vein size of crystal ores").getInt();
        oreMaximumVeinSize = cfg.get(CATEGORY_WORLD, "oreMaximumVeinSize", oreMaximumVeinSize,
                                     "Maximum vein size of crystal ores").getInt();
        oreMaximumVeinCount = cfg.get(CATEGORY_WORLD, "oreMaximumVeinCount", oreMaximumVeinCount,
                                      "Maximum number of veins for crystal ores").getInt();
        oreMinimumHeight = cfg.get(CATEGORY_WORLD, "oreMinimumHeight", oreMinimumHeight,
                                   "Minimum y level for crystal ores").getInt();
        oreMaximumHeight = cfg.get(CATEGORY_WORLD, "oreMaximumHeight", oreMaximumHeight,
                                   "Maximum y level for crystal ores").getInt();
        
        generateOreNether = cfg.get(CATEGORY_WORLD, "generateOreNether", generateOreNether, "Enable or disable Crystal Ore in the Nether").getBoolean();
    	oreNetherMinimumVeinSize = cfg.get(CATEGORY_WORLD, "oreNetherMinimumVeinSize", oreNetherMinimumVeinSize,
        		"Minimum vein size of crystal ores in the Nether.").getInt();
        oreNetherMaximumVeinSize = cfg.get(CATEGORY_WORLD, "oreNetherMaximumVeinSize", oreNetherMaximumVeinSize,
        		"Maximum vein size of crystal ores in the Nether.").getInt();
        oreNetherMaximumVeinCount = cfg.get(CATEGORY_WORLD, "oreNetherMaximumVeinCount", oreNetherMaximumVeinCount,
        		"Maximum number of veins for crystal ores in the Nether.").getInt();
        oreNetherMinimumHeight = cfg.get(CATEGORY_WORLD, "oreNetherMinimumHeight", oreNetherMinimumHeight,
        		"Minimum y level for crystal ores in the Nether.").getInt();
        oreNetherMaximumHeight = cfg.get(CATEGORY_WORLD, "oreNetherMaximumHeight", oreNetherMaximumHeight,
        		"Maximum y level for crystal ores in the Nether.").getInt();
        
        generateOreEnd = cfg.get(CATEGORY_WORLD, "generateOreEnd", generateOreEnd, "Enable or disable Crystal Ore in the End").getBoolean();
    	oreEndMinimumVeinSize = cfg.get(CATEGORY_WORLD, "oreEndMinimumVeinSize", oreEndMinimumVeinSize,
        		"Minimum vein size of crystal ores in the End.").getInt();
        oreEndMaximumVeinSize = cfg.get(CATEGORY_WORLD, "oreEndMaximumVeinSize", oreEndMaximumVeinSize,
        		"Maximum vein size of crystal ores in the End.").getInt();
        oreEndMaximumVeinCount = cfg.get(CATEGORY_WORLD, "oreEndMaximumVeinCount", oreEndMaximumVeinCount,
        		"Maximum number of veins for crystal ores in the End.").getInt();
        oreEndMinimumHeight = cfg.get(CATEGORY_WORLD, "oreEndMinimumHeight", oreEndMinimumHeight,
        		"Minimum y level for crystal ores in the End.").getInt();
        oreEndMaximumHeight = cfg.get(CATEGORY_WORLD, "oreEndMaximumHeight", oreEndMaximumHeight,
        		"Maximum y level for crystal ores in the End.").getInt();
        
        generateOreOther = cfg.get(CATEGORY_WORLD, "generateOreOther", generateOreOther, "Enable or disable Crystal Ore in modded dimensions").getBoolean();
    	oreOtherMinimumVeinSize = cfg.get(CATEGORY_WORLD, "oreOtherMinimumVeinSize", oreOtherMinimumVeinSize,
        		"Minimum vein size of crystal ores in modded dimensions.").getInt();
        oreOtherMaximumVeinSize = cfg.get(CATEGORY_WORLD, "oreOtherMaximumVeinSize", oreOtherMaximumVeinSize,
        		"Maximum vein size of crystal ores in modded dimensions.").getInt();
        oreOtherMaximumVeinCount = cfg.get(CATEGORY_WORLD, "oreOtherMaximumVeinCount", oreOtherMaximumVeinCount,
        		"Maximum number of veins for crystal ores in modded dimensions.").getInt();
        oreOtherMinimumHeight = cfg.get(CATEGORY_WORLD, "oreOtherMinimumHeight", oreOtherMinimumHeight,
        		"Minimum y level for crystal ores in modded dimensions.").getInt();
        oreOtherMaximumHeight = cfg.get(CATEGORY_WORLD, "oreOtherMaximumHeight", oreOtherMaximumHeight,
        		"Maximum y level for crystal ores in modded dimensions.").getInt();
        
        generateFusionTemple = cfg.get(CATEGORY_WORLD, "generateFusionTemple", generateFusionTemple, "Enable or disable Fusion Temple Generation in the Overworld.").getBoolean();
        
        generateOverworldWell = cfg.get(CATEGORY_WORLD, "generateOverworldWell", generateOverworldWell, "Enable or disable Crystal Well Generation in the Overworld.").getBoolean();
        overworldWellChance = cfg.get(CATEGORY_WORLD, "overworldWellChance", overworldWellChance, "Chance of the Overworld Well generating. The higher the number the lower the chance. If the number is less than 0 it will not generate.").getInt(250);
        generateNetherWell = cfg.get(CATEGORY_WORLD, "generateNetherWell", generateNetherWell, "Enable or disable Nether Well Generation in the Nether.").getBoolean();
        netherWellChance = cfg.get(CATEGORY_WORLD, "netherWellChance", netherWellChance, "Chance of the Nether Well generating. The higher the number the lower the chance. If the number is less than 0 it will not generate.").getInt(500);
        generateEndWell = cfg.get(CATEGORY_WORLD, "generateEndWell", generateEndWell, "Enable or disable Ender Well Generation in the End.").getBoolean();
        endWellChance = cfg.get(CATEGORY_WORLD, "endWellChance", endWellChance, "Chance of the End Well generating. The higher the number the lower the chance. If the number is less than 0 it will not generate.").getInt(100);
        
        generateNetherCrysineMushrooms = cfg.get(CATEGORY_WORLD, "generateNetherCrysineMushrooms", generateNetherCrysineMushrooms, "Enable or disable Crysine Mushroom Generation in the Nether.").getBoolean();
        giantCrysineMushroomChance = cfg.get(CATEGORY_WORLD, "giantCrysineMushroomChance", giantCrysineMushroomChance, "Chance of giant Crysine Mushrooms generating in the Nether. The higher the number the lower the chance. If the number is less than 0 it will not generate.").getInt(15);
       
        retrogenInfo = cfg.get(CATEGORY_WORLD, "retrogenInfo", retrogenInfo,
                "Set to true if you want retro gen chunks logged.").getBoolean();
        retrogenID = cfg.get(CATEGORY_WORLD, "retrogenID", retrogenID,
                "Change this id to regen in a previously retrogened chunk.").getString();
        retrogenOres = cfg.get(CATEGORY_WORLD, "retrogenOres", retrogenOres,
                                   "Set to true to enable retrogen of crystal ore").getBoolean();
        retrogenTrees = cfg.get(CATEGORY_WORLD, "retrogenTrees", retrogenTrees,
                "Set to true to enable retrogen of crystal trees").getBoolean();
        retrogenReeds = cfg.get(CATEGORY_WORLD, "retrogenReeds", retrogenReeds,
                "Set to true to enable retrogen of crystal reeds").getBoolean();
        maximumReedsPerChunk = cfg.get(CATEGORY_WORLD, "maximumReedsPerChunk", maximumReedsPerChunk,
                "Maximum number of reeds per chunk").getInt();
        reedPlacementTrys = cfg.get(CATEGORY_WORLD, "reedPlacementTrys", reedPlacementTrys,
                "Amount of reed stacks allowed to gen if able to be placed").getInt();
        
        int[] oreBlacklist = cfg.get(CATEGORY_WORLD, "oreDimensionBlacklist", new int[] { },
                                                       "Crystal ore dimension blacklist").getIntList();
        for (int i : oreBlacklist) {
            CrystalOreFeature.oreDimBlacklist.add(i);
        }
        
        int[] treeBlacklist = cfg.get(CATEGORY_WORLD, "treeDimensionBlacklist", new int[] { -1, 1 },
                "Crystal Tree dimension blacklist").getIntList();
        for (int i : treeBlacklist) {
        	CrystalTreeFeature.treeDimBlacklist.add(i);
        }
        
        int[] reedBlacklist = cfg.get(CATEGORY_WORLD, "reedDimensionBlacklist", new int[] { -1, 1 },
                "Crystal Reeds dimension blacklist").getIntList();
        for (int i : reedBlacklist) {
        	CrystalReedsFeature.reedDimBlacklist.add(i);
        }
        
        generateSeaweed = cfg.get(CATEGORY_WORLD, "generateSeaweed", generateSeaweed, "Enable or disable Seaweed generation in oceans.").getBoolean();
        retrogenSeaweed = cfg.get(CATEGORY_WORLD, "retrogenSeaweed", retrogenSeaweed,
                "Set to true to enable retrogen of seaweed").getBoolean();
        generateKelp = cfg.get(CATEGORY_WORLD, "generateKelp", generateKelp, "Enable or disable Kelp generation in oceans.").getBoolean();
        retrogenKelp = cfg.get(CATEGORY_WORLD, "retrogenKelp", retrogenKelp,
                "Set to true to enable retrogen of kelp").getBoolean();
        coralChance = cfg.get(CATEGORY_WORLD, "coralGenChance", coralChance, "How rare are Coral Reefs in oceans? Set to 0 to disable them").getInt(20);
        retrogenCoral = cfg.get(CATEGORY_WORLD, "retrogenCoral", retrogenCoral,
                "Set to true to enable retrogen of Coral Reefs").getBoolean();
        generateRoses = cfg.get(CATEGORY_WORLD, "generateRoses", generateRoses, "Enable or disable More Colors of Roses in Forests.").getBoolean();
        retrogenRoses = cfg.get(CATEGORY_WORLD, "retrogenRoses", retrogenRoses,
                "Set to true to enable retrogen of more colored roses").getBoolean();
        
        //Loot
    	String[] defaultLocations = StringUtils.makeStringArray(defaultEnhancementBookLootList);
    	String[] configLocations = cfg.getStringList("enhancementBookLocations", CATEGORY_WORLD, defaultLocations, "Loot Tables that CrystalMod is allowed to place the Enhancement Books in");
    	ResourceLocation[] bookLocations = new ResourceLocation[configLocations.length];
    	for(int i = 0; i < bookLocations.length; i++){
    		bookLocations[i] = new ResourceLocation(configLocations[i]);
    	}
    	enhancementBookLootLocationList = Lists.newArrayList(bookLocations);
    	enhancementBookRarity = cfg.get(CATEGORY_WORLD, "enhancementBookRarity", enhancementBookRarity,
                "Rarity of Enhancement Books in chest loot").setRequiresMcRestart(true).getInt(10);
    	whiteFishRarity = cfg.get(CATEGORY_WORLD, "whiteFishRarity", whiteFishRarity,
                "Chance of Broad Whitefish replacing normal fish in cold biomes when caught with a fishing pole. Higher the number the lower the chance. Zero or less means never.").getInt(10);
        
    	playerCubePlayerLimit = cfg.get(CATEGORY_WORLD, "PlayerCubePlayerLimit", playerCubePlayerLimit, "The maximum amount of player cubes each player is allowed to create. (If this equals zero no cubes are allowed)").getInt(16);
        if(playerCubePlayerLimit < 0){
        	playerCubePlayerLimit = 0;
        }
        
       
    	//ITEMS
    	superTorchMaxCount = cfg.get(CATEGORY_ITEM, "superTorchCapacity", superTorchMaxCount, "Maximum amount of torches allowed to be stored in a Super Torch.").getInt();
        
        if(superTorchMaxCount < 0){
        	superTorchMaxCount = 0;
        }
        
        tool_pureDamageAddition = cfg.get(CATEGORY_ITEM, "pureToolDurabilityAdditon", tool_pureDamageAddition, "Durability bonus for pure tools").getInt();
        
        backpackDeathUpgradeConsume = cfg.get(CATEGORY_ITEM, "DeathUpgradeConsumed", backpackDeathUpgradeConsume, "Set to true if death upgrades are consumed on death.").getBoolean();
        
        hardmode_MaterialCrops = cfg.get(CATEGORY_ITEM, "HardmodeMaterialCrops", hardmode_MaterialCrops, "Set to true if seeds must be crafted with fusion instead of normal crafting.").getBoolean();

        
        //ENTITY
        int headtype = cfg.get(CATEGORY_ENTITY, "mobHeadDrop", mobHeadType.ordinal(), "0 = Never Drop; 1 = Drop when killed; 2 = Drop only when killed by player;").getInt(mobHeadType.ordinal());
    	if(headtype < 0)headtype = 0;
    	if(headtype > 2)headtype = 2;
    	mobHeadType = ItemDropType.values()[headtype];
    	mobHeadDropChance = cfg.get(CATEGORY_ENTITY, "mobHeadDropChance", mobHeadDropChance, "1 in this chance of dropping  (-1 = no heads)").getInt(mobHeadDropChance);
    	
    	int pheadtype = cfg.get(CATEGORY_ENTITY, "playerHeadDrop", playerHeadType.ordinal(), "0 = Never Drop Player Head; 1 = Drop when killed; 2 = Drop only when killed by other player;").getInt(playerHeadType.ordinal());
    	if(pheadtype < 0)pheadtype = 0;
    	if(pheadtype > 2)pheadtype = 2;
    	playerHeadType = ItemDropType.values()[pheadtype];
    	playerHeadDropChance = cfg.get(CATEGORY_ENTITY, "playerHeadDropChance", playerHeadDropChance, "1 in this chance of dropping  (-1 = no heads)").getInt(playerHeadDropChance);
        	 
    	dragonWingsDrop = cfg.get(CATEGORY_ENTITY, "dragonWingsDrop", dragonWingsDrop, "Can dragon wings drop from an Ender Dragon?").getBoolean(true);
    	darkBonesDrop = cfg.get(CATEGORY_ENTITY, "darkBonesDrop", darkBonesDrop, "Can dark bones drop from Wither Skeletons?").getBoolean(true);
    	batWingDropChance = cfg.get(CATEGORY_ENTITY, "batWingDropChance", batWingDropChance, "1 in X chance of dropping  (0 = no drop)").getInt(5);

    	hoeStrings = cfg.get(CATEGORY_MINIONS, "Hoes", hoeStrings, "Use this to specify items that are hoes. Use the registry name (eg. modid:name).").getStringList();
    
    	//MACHINE
    	PowerUnits.RF.conversionRation = cfg.get(CATEGORY_MACHINE, "RFValue", 5, "Amount of RF needed to convert to one unit of CU").getInt(5);
    	engine_vampire_maxattack = cfg.get(CATEGORY_MACHINE, "Engine_Vampire_AttackAmt", engine_vampire_maxattack, "Amount of entites the vampire engine can attack at once").getInt();
    	advDispenser_cooldown = cfg.get(CATEGORY_MACHINE, "advDispenser_cooldown", advDispenser_cooldown, "Amount of ticks inbetween each click on the Advanced Dispenser").getInt();
    	xpFountain_powerNeeded = cfg.get(CATEGORY_MACHINE, "xpFountain_powerNeeded", xpFountain_powerNeeded, "Amount of power needed to activate the XP Fountain").getInt(3000);

    	//BLOCK
    	crates_leaveOneItem = cfg.get(CATEGORY_BLOCKS, "crates_leaveOneItem", crates_leaveOneItem, "Set to true to leave one item in a crate when it is clicked.").getBoolean();
    	crates_useAllSides = cfg.get(CATEGORY_BLOCKS, "crates_useAllSides", crates_useAllSides, "Set to true to allow the player to insert and remove items from a crate on all sides, not just the front.").getBoolean();
    	crates_3dItem = cfg.get(CATEGORY_BLOCKS, "crates_3dItem", crates_3dItem, "Set to false to render items in 2D.").getBoolean();
    	crates_3dBlock = cfg.get(CATEGORY_BLOCKS, "crates_3dBlock", crates_3dBlock, "Set to false to render blocks in 2D.").getBoolean();
    	int regenIndex = cfg.get(CATEGORY_BLOCKS, "crystalClusterRegenType", crystalClusterRegenType.ordinal(), "0 = Idle (Cluster Regens when not in use) 1 = Empty (Once the cluster is completly drained it will regen until power is extracted again) 2 = Never (Clusters will never regen)").getInt(0);
    	crystalClusterRegenType = RegenType.values()[regenIndex % RegenType.values().length];
    	
    	infectionRange = cfg.get(CATEGORY_BLOCKS, "darkInfectionRange", infectionRange, "Maximum Radius allowed for the Dark Infection to spread. Set to 0 for infinite").getInt(30);
    	infectionEncasingRange = cfg.get(CATEGORY_BLOCKS, "darkInfectionEncasingRange", infectionEncasingRange, "When does the Dark Infection begin encasing itself. Set to 0 to disable").getInt(30);
    	infectionEncasingSize = cfg.get(CATEGORY_BLOCKS, "darkInfectionEncasingSize", infectionEncasingSize, "Size of the orb that encases the Dark Infection Source.").getInt(5);
    	dyeFromCoral = cfg.get(CATEGORY_BLOCKS, "dyeFromCoral", dyeFromCoral, "Can Coral be smelted down into their dye color?").setRequiresMcRestart(true).getBoolean(true);

    	//CLIENT
    	vanillaMinecarts3d = cfg.get(CATEGORY_CLIENT, "3dMinecartItems", vanillaMinecarts3d, "Override Minecart Item Render to 3d items.").getBoolean();
    	vanillaBoats3d = cfg.get(CATEGORY_CLIENT, "3dBoatItems", vanillaBoats3d, "Override Boat Item Render to 3d items.").getBoolean();
    	useRemoteManualFile = cfg.get(CATEGORY_CLIENT, "useRemoteManualFile", useRemoteManualFile, "Disable to only use local manual file.").getBoolean();
    }
    
    public static void postInit() {
    	farmHoes.clear();
        for (String s : hoeStrings) {
          ItemStack hoe = getStackForString(s);
          if(!ItemStackTools.isNullStack(hoe)) {
            farmHoes.add(hoe);
          }
        }
    }
    
    public static ItemStack getStackForString(String s) {
        String[] nameAndMeta = s.split(";");
        int meta = nameAndMeta.length == 1 ? 0 : Integer.parseInt(nameAndMeta[1]);
        ItemStack stack = GameRegistry.makeItemStack(nameAndMeta[0], meta, 1, "");
        if(ItemStackTools.isNullStack(stack)) {
          return ItemStackTools.getEmptyStack();
        }
        stack.setItemDamage(meta);
        return stack;
    }
}
