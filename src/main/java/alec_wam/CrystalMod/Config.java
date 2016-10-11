package alec_wam.CrystalMod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import alec_wam.CrystalMod.handler.EventHandler.ItemDropType;
import alec_wam.CrystalMod.tiles.machine.power.converter.PowerUnits;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Config {
	public static final String CATEGORY_GENERAL = "general";
	public static final String CATEGORY_ENTITY = "entities";
	public static final String CATEGORY_ENCHANTMENT = "enchantments";
	public static final String CATEGORY_MINIONS = "minions";
	public static final String CATEGORY_MACHINE = "machines";
	
	public static int oreMinimumVeinSize = 5;
    public static int oreMaximumVeinSize = 8;
    public static int oreMaximumVeinCount = 3;
    public static int oreMinimumHeight = 2;
    public static int oreMaximumHeight = 30;
    public static boolean retrogen = true;
    
    private static int[] oregen = new int[] { -1, 1 };
    public static Set<Integer> oregenDimensions = new HashSet<Integer>();

	public static boolean enableAlarmClocks = true;
	
	public static ItemDropType mobHeadType = ItemDropType.KILLED;
	public static int mobHeadDropChance = 200;
	public static ItemDropType playerHeadType = ItemDropType.ALL;
	public static int playerHeadDropChance = 50;
	
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
	
	public static boolean enchantmentMendingEnabled = true;
	public static int enchantmentMendingId = 196;
	public static int enchantmentMendingWeight = 2;
    
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if(event.getModID().equals(CrystalMod.MODID)) {
			ModLogger.info("Updating crystalmod config...");
			CrystalMod.proxy.readMainConfig();
		}
	}
	
    public static void init(Configuration cfg) {
       oreMinimumVeinSize = cfg.get(CATEGORY_GENERAL, "oreMinimumVeinSize", oreMinimumVeinSize,
                                     "Minimum vein size of crystal ores").getInt();
        oreMaximumVeinSize = cfg.get(CATEGORY_GENERAL, "oreMaximumVeinSize", oreMaximumVeinSize,
                                     "Maximum vein size of crystal ores").getInt();
        oreMaximumVeinCount = cfg.get(CATEGORY_GENERAL, "oreMaximumVeinCount", oreMaximumVeinCount,
                                      "Maximum number of veins for crystal ores").getInt();
        oreMinimumHeight = cfg.get(CATEGORY_GENERAL, "oreMinimumHeight", oreMinimumHeight,
                                   "Minimum y level for crystal ores").getInt();
        oreMaximumHeight = cfg.get(CATEGORY_GENERAL, "oreMaximumHeight", oreMaximumHeight,
                                   "Maximum y level for crystal ores").getInt();
        retrogen = cfg.get(CATEGORY_GENERAL, "retrogen", retrogen,
                                   "Set to true to enable retrogen").getBoolean();
        
        oregen = cfg.get(CATEGORY_GENERAL, "oregenDimensions", oregen,
                                                       "Oregen dimensions for crystal ore").getIntList();
        for (int i : oregen) {
            oregenDimensions.add(i);
        }
        
        enableAlarmClocks = cfg.get(CATEGORY_GENERAL, "alarmClocks", enableAlarmClocks,
                "Set to true to enable alarm clocks changing time").getBoolean();
        
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
    	PowerUnits.CU.conversionRation = cfg.get(CATEGORY_MACHINE, "RFtoCU", PowerUnits.CU.conversionRation, "Amount of RF needed to convert to one unit of CU").getInt(PowerUnits.CU.conversionRation);

    	enchantmentMendingEnabled = cfg.get(CATEGORY_ENCHANTMENT, "mendingEnabled", enchantmentMendingEnabled, "Set to false to disable the Mending Enchantment.").getBoolean(enchantmentMendingEnabled);
    	enchantmentMendingId = cfg.get(CATEGORY_ENCHANTMENT, "mendingId", enchantmentMendingId, "ID of the Mending Enchantment. (Set to -1 to enabled auto id)").getInt(enchantmentMendingId);
    	enchantmentMendingWeight = cfg.get(CATEGORY_ENCHANTMENT, "mendingWeight", enchantmentMendingWeight, "Weight of the Mending Enchantment.").getInt(enchantmentMendingWeight);
    }
    
    public static void postInit() {
    	farmHoes.clear();
        for (String s : hoeStrings) {
          ItemStack hoe = getStackForString(s);
          if(hoe != null) {
            farmHoes.add(hoe);
          }
        }
    }
    
    public static ItemStack getStackForString(String s) {
        String[] nameAndMeta = s.split(";");
        int meta = nameAndMeta.length == 1 ? 0 : Integer.parseInt(nameAndMeta[1]);
        ItemStack stack = GameRegistry.makeItemStack(nameAndMeta[0], meta, 1, "");
        if(stack == null) {
          return null;
        }
        stack.setItemDamage(meta);
        return stack;
    }

}
