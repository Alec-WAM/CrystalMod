package alec_wam.CrystalMod.world.biomes;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBiomes {

	public static BiomeBambooForest BAMBOO_FOREST;
	
	public static void init(){
		float height = ((float)68 - 65.0F) / 17.0F;
		float heightVar = Math.abs((((float)10 - 7.0F) / (20.0F * 4.0F) + ((float)5 - 4.0F) / 20.0F) / 2.0F);
		Biome.BiomeProperties bambooProperties = new Biome.BiomeProperties("Bamboo Forest").setBaseHeight(height).setHeightVariation(heightVar).setTemperature(0.8F).setRainfall(0.4F);
		BAMBOO_FOREST = (BiomeBambooForest) new BiomeBambooForest(bambooProperties).setRegistryName(CrystalMod.resourceL("bambooforest"));
		GameRegistry.register(BAMBOO_FOREST);
		BiomeManager.addBiome(BiomeType.WARM, new BiomeEntry(BAMBOO_FOREST, 10));
		BiomeDictionary.addTypes(BAMBOO_FOREST, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.WET);
		BiomeManager.addSpawnBiome(BAMBOO_FOREST);
		ModLogger.info("Added "+BAMBOO_FOREST.getBiomeName()+" id = "+Biome.getIdForBiome(BAMBOO_FOREST));
	}
	
}
