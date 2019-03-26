package alec_wam.CrystalMod.world.biomes;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.util.CrystalColors;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.world.crystex.biomes.BiomeCrystexForest;
import alec_wam.CrystalMod.world.crystex.biomes.BiomeCrystexPlains;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBiomes {

	public static BiomeBambooForest BAMBOO_FOREST;
	
	public static BiomeCrystexPlains BLUE_CRYSTEX_PLAINS;
	public static BiomeCrystexPlains RED_CRYSTEX_PLAINS;
	public static BiomeCrystexPlains GREEN_CRYSTEX_PLAINS;
	public static BiomeCrystexPlains DARK_CRYSTEX_PLAINS;
	public static BiomeCrystexForest BLUE_CRYSTEX_FOREST;
	public static BiomeCrystexForest RED_CRYSTEX_FOREST;
	public static BiomeCrystexForest GREEN_CRYSTEX_FOREST;
	public static BiomeCrystexForest DARK_CRYSTEX_FOREST;
	
	//TODO Make Bamboo forests WAY smaller
	public static void init(){
		
		if(Config.enableBambooForest){
			float height = ((float)68 - 65.0F) / 17.0F;
			float heightVar = Math.abs((((float)10 - 7.0F) / (20.0F * 4.0F) + ((float)5 - 4.0F) / 20.0F) / 2.0F);
			Biome.BiomeProperties bambooProperties = new Biome.BiomeProperties("Bamboo Forest").setBaseHeight(height).setHeightVariation(heightVar).setTemperature(0.8F).setRainfall(0.4F);
			BAMBOO_FOREST = (BiomeBambooForest) new BiomeBambooForest(bambooProperties).setRegistryName(CrystalMod.resourceL("bambooforest"));
			GameRegistry.register(BAMBOO_FOREST);
			BiomeManager.addBiome(BiomeType.WARM, new BiomeEntry(BAMBOO_FOREST, 30));
			BiomeDictionary.addTypes(BAMBOO_FOREST, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.WET);
			ModLogger.info("Added "+BAMBOO_FOREST.getBiomeName()+" id = "+Biome.getIdForBiome(BAMBOO_FOREST));
		}
		
		
		//Crystex Biomes
		BiomeProperties plains_blue = (new Biome.BiomeProperties("Blue Crystex Plains")).setBaseHeight(0.125F).setHeightVariation(0.05F).setTemperature(0.8F).setRainfall(0.4F);
		BiomeProperties plains_red = (new Biome.BiomeProperties("Red Crystex Plains")).setBaseHeight(0.125F).setHeightVariation(0.05F).setTemperature(0.8F).setRainfall(0.4F);
		BiomeProperties plains_green = (new Biome.BiomeProperties("Green Crystex Plains")).setBaseHeight(0.125F).setHeightVariation(0.05F).setTemperature(0.8F).setRainfall(0.4F);
		BiomeProperties plains_dark = (new Biome.BiomeProperties("Dark Crystex Plains")).setBaseHeight(0.125F).setHeightVariation(0.05F).setTemperature(0.8F).setRainfall(0.4F);
		
		BiomeProperties forest_blue = new Biome.BiomeProperties("Blue Crystex Forest").setTemperature(0.7F).setRainfall(0.8F);
		BiomeProperties forest_red = new Biome.BiomeProperties("Red Crystex Forest").setTemperature(0.7F).setRainfall(0.8F);
		BiomeProperties forest_green = new Biome.BiomeProperties("Green Crystex Forest").setTemperature(0.7F).setRainfall(0.8F);
		BiomeProperties forest_dark = new Biome.BiomeProperties("Dark Crystex Forest").setTemperature(0.7F).setRainfall(0.8F);
		BLUE_CRYSTEX_PLAINS = (BiomeCrystexPlains) new BiomeCrystexPlains(CrystalColors.SuperSpecial.BLUE, plains_blue).setRegistryName(CrystalMod.resourceL("crystex_blueplains"));
		GameRegistry.register(BLUE_CRYSTEX_PLAINS);
		BiomeDictionary.addTypes(BLUE_CRYSTEX_PLAINS, BiomeDictionary.Type.PLAINS);
		RED_CRYSTEX_PLAINS = (BiomeCrystexPlains) new BiomeCrystexPlains(CrystalColors.SuperSpecial.RED, plains_red).setRegistryName(CrystalMod.resourceL("crystex_redplains"));
		GameRegistry.register(RED_CRYSTEX_PLAINS);
		BiomeDictionary.addTypes(RED_CRYSTEX_PLAINS, BiomeDictionary.Type.PLAINS);
		GREEN_CRYSTEX_PLAINS = (BiomeCrystexPlains) new BiomeCrystexPlains(CrystalColors.SuperSpecial.GREEN, plains_green).setRegistryName(CrystalMod.resourceL("crystex_greenplains"));
		GameRegistry.register(GREEN_CRYSTEX_PLAINS);
		BiomeDictionary.addTypes(GREEN_CRYSTEX_PLAINS, BiomeDictionary.Type.PLAINS);
		DARK_CRYSTEX_PLAINS = (BiomeCrystexPlains) new BiomeCrystexPlains(CrystalColors.SuperSpecial.DARK, plains_dark).setRegistryName(CrystalMod.resourceL("crystex_darkplains"));
		GameRegistry.register(DARK_CRYSTEX_PLAINS);
		BiomeDictionary.addTypes(DARK_CRYSTEX_PLAINS, BiomeDictionary.Type.PLAINS);
		
		BLUE_CRYSTEX_FOREST = (BiomeCrystexForest) new BiomeCrystexForest(CrystalColors.SuperSpecial.BLUE, forest_blue).setRegistryName(CrystalMod.resourceL("crystex_blueforest"));
		GameRegistry.register(BLUE_CRYSTEX_FOREST);
		BiomeDictionary.addTypes(BLUE_CRYSTEX_FOREST, BiomeDictionary.Type.FOREST);
		RED_CRYSTEX_FOREST = (BiomeCrystexForest) new BiomeCrystexForest(CrystalColors.SuperSpecial.RED, forest_red).setRegistryName(CrystalMod.resourceL("crystex_redforest"));
		GameRegistry.register(RED_CRYSTEX_FOREST);
		BiomeDictionary.addTypes(RED_CRYSTEX_FOREST, BiomeDictionary.Type.FOREST);
		GREEN_CRYSTEX_FOREST = (BiomeCrystexForest) new BiomeCrystexForest(CrystalColors.SuperSpecial.GREEN, forest_green).setRegistryName(CrystalMod.resourceL("crystex_greenforest"));
		GameRegistry.register(GREEN_CRYSTEX_FOREST);
		BiomeDictionary.addTypes(GREEN_CRYSTEX_FOREST, BiomeDictionary.Type.FOREST);
		DARK_CRYSTEX_FOREST = (BiomeCrystexForest) new BiomeCrystexForest(CrystalColors.SuperSpecial.DARK, forest_dark).setRegistryName(CrystalMod.resourceL("crystex_darkforest"));
		GameRegistry.register(DARK_CRYSTEX_FOREST);
		BiomeDictionary.addTypes(DARK_CRYSTEX_FOREST, BiomeDictionary.Type.FOREST);
		
	}
	
}
