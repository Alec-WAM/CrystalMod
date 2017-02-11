package alec_wam.CrystalMod;

import java.util.ArrayList;
import java.util.List;

import alec_wam.CrystalMod.handler.EventHandler.ItemDropType;
import alec_wam.CrystalMod.tiles.machine.power.converter.PowerUnits;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.world.CrystalModWorldGenerator;
import net.minecraft.item.ItemStack;
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
	
	public static int oreMinimumVeinSize = 5;
    public static int oreMaximumVeinSize = 8;
    public static int oreMaximumVeinCount = 2;
    public static int oreMinimumHeight = 0;
    public static int oreMaximumHeight = 20;
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
    
	public static ItemDropType mobHeadType = ItemDropType.KILLED;
	public static int mobHeadDropChance = 200;
	public static ItemDropType playerHeadType = ItemDropType.ALL;
	public static int playerHeadDropChance = 50;
	
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
	
	public static int powerConduitTierOneCU = 640;
	public static int powerConduitTierTwoCU = 5120;
	public static int powerConduitTierThreeCU = 20480;
	public static int powerConduitTierFourCU = 40960;
	
	public static int powerConduitTierOneRF = 640;
	public static int powerConduitTierTwoRF = 5120;
	public static int powerConduitTierThreeRF = 20480;
	public static int powerConduitTierFourRF = 40960;
    
	public static boolean vanillaMinecarts3d = true;
	
	public static int engine_vampire_maxattack = 16;
	public static int advDispenser_cooldown = 40; //2 seconds
	public static boolean crates_leaveOneItem = true;
	
	public static boolean backpackDeathUpgradeConsume = false;
	public static boolean hardmode_MaterialCrops = false;
	
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if(event.getModID().equals(CrystalMod.MODID)) {
			ModLogger.info("Updating crystalmod config...");
			CrystalMod.proxy.readMainConfig();
		}
	}
	
    public static void init(Configuration cfg) {
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
        
        int[] oreBlacklist = cfg.get(CATEGORY_WORLD, "oreDimensionBlacklist", new int[] { -1, 1 },
                                                       "Crystal ore dimension blacklist").getIntList();
        for (int i : oreBlacklist) {
            CrystalModWorldGenerator.oreDimBlacklist.add(i);
        }
        
        int[] treeBlacklist = cfg.get(CATEGORY_WORLD, "treeDimensionBlacklist", new int[] { -1, 1 },
                "Crystal Tree dimension blacklist").getIntList();
        for (int i : treeBlacklist) {
        	CrystalModWorldGenerator.treeDimBlacklist.add(i);
        }
        
        superTorchMaxCount = cfg.get(CATEGORY_ITEM, "superTorchCapacity", superTorchMaxCount, "Maximum amount of torches allowed to be stored in a Super Torch.").getInt();
        
        if(superTorchMaxCount < 0){
        	superTorchMaxCount = 0;
        }
        
        tool_pureDamageAddition = cfg.get(CATEGORY_ITEM, "pureToolDurabilityAdditon", tool_pureDamageAddition, "Durability bonus for pure tools").getInt();
        
        backpackDeathUpgradeConsume = cfg.get(CATEGORY_ITEM, "DeathUpgradeConsumed", backpackDeathUpgradeConsume, "Set to true if death upgrades are consumed on death.").getBoolean();
        
        hardmode_MaterialCrops = cfg.get(CATEGORY_ITEM, "HardmodeMaterialCrops", hardmode_MaterialCrops, "Set to true if seeds must be crafted with fusion instead of normal crafting.").getBoolean();

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
    
    	hoeStrings = cfg.get(CATEGORY_MINIONS, "Hoes", hoeStrings, "Use this to specify items that are hoes. Use the registry name (eg. modid:name).").getStringList();
    
    	//Machines
    	PowerUnits.RF.conversionRation = cfg.get(CATEGORY_MACHINE, "RFValue", 5, "Amount of RF needed to convert to one unit of CU").getInt(5);
    	engine_vampire_maxattack = cfg.get(CATEGORY_MACHINE, "Engine_Vampire_AttackAmt", engine_vampire_maxattack, "Amount of entites the vampire engine can attack at once").getInt();
    	advDispenser_cooldown = cfg.get(CATEGORY_MACHINE, "advDispenser_cooldown", advDispenser_cooldown, "Amount of ticks inbetween each click on the Advanced Dispenser").getInt();
    	crates_leaveOneItem = cfg.get(CATEGORY_BLOCKS, "crates_leaveOneItem", crates_leaveOneItem, "Set to true to leave one item in a crate when it is clicked.").getBoolean();

    	//Client
    	vanillaMinecarts3d = cfg.get(CATEGORY_CLIENT, "3dMinecartItems", vanillaMinecarts3d, "Override Minecart Item Render to 3d items.").getBoolean();
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
