package alec_wam.CrystalMod.world;

import alec_wam.CrystalMod.ModConfig;
import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.world.features.BetterRoseFeature;
import alec_wam.CrystalMod.world.features.CrystalBushFeature;
import alec_wam.CrystalMod.world.features.MinableRandomConfig;
import alec_wam.CrystalMod.world.features.MineableFeatureRandom;
import alec_wam.CrystalMod.world.features.RandomCrystalTreeFeature;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;

public class CrystalModWorldGenerator{	
	//TODO Look into RetroGen
    public static CrystalModWorldGenerator instance = new CrystalModWorldGenerator();
    
    public static final Feature<MinableRandomConfig> MINABLE_RANDOM = new MineableFeatureRandom(MinableRandomConfig::load);
    public static final Feature<NoFeatureConfig> BETTER_ROSES = new BetterRoseFeature(NoFeatureConfig::func_214639_a);
    public static final Feature<NoFeatureConfig> CRYSTAL_BUSH = new CrystalBushFeature(NoFeatureConfig::func_214639_a);
    public static final Feature<NoFeatureConfig> CRYSTAL_TREE = new RandomCrystalTreeFeature(NoFeatureConfig::func_214639_a);
	public void setupFeatures() {
		int minVeinSize = 5;
		int maxVeinSize = 8;
		int maxVeinCount = Math.min(ModConfig.WORLDGEN.CrystalOre_Per_Chunk.get(), 32);
		int maxHeight = 40;
		if(maxVeinCount > 0){
			BlockState[] ores = new BlockState[EnumCrystalColor.values().length];
	        for(int o = 0; o < EnumCrystalColor.values().length; o++){
	        	ores[o] = ModBlocks.crystalOreGroup.getBlock(EnumCrystalColor.values()[o]).getDefaultState();
	        }
	        MinableRandomConfig config = new MinableRandomConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, ores, minVeinSize, maxVeinSize);
			for(BiomeManager.BiomeType type : BiomeManager.BiomeType.values()){
				BiomeManager.getBiomes(type).forEach((BiomeManager.BiomeEntry biomeEntry) -> biomeEntry.biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.func_222280_a(MINABLE_RANDOM, config, Placement.field_215028_n, new CountRangeConfig(maxVeinCount, 0, 0, maxHeight))));
			}
		}
		//TODO Reed Gen
		BiomeDictionary.getBiomes(BiomeDictionary.Type.FOREST).forEach((Biome biome) -> addForestDecorations(biome));
	}
	
	public void addForestDecorations(Biome biome){
		//TODO Make Config Options for each decoration
		biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.func_222280_a(BETTER_ROSES, IFeatureConfig.NO_FEATURE_CONFIG, Placement.field_215017_c, new FrequencyConfig(5)));
		biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.func_222280_a(CRYSTAL_BUSH, IFeatureConfig.NO_FEATURE_CONFIG, Placement.field_215018_d, new FrequencyConfig(5)));
		
		biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.func_222280_a(CRYSTAL_TREE, IFeatureConfig.NO_FEATURE_CONFIG, Placement.field_215024_j, new ChanceConfig(60)));
	}

}
