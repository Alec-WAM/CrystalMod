package alec_wam.CrystalMod.world;

import alec_wam.CrystalMod.ModConfig;
import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.world.features.BetterRoseFeature;
import alec_wam.CrystalMod.world.features.MinableRandomConfig;
import alec_wam.CrystalMod.world.features.MineableFeatureRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.MinableConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;

public class CrystalModWorldGenerator{	
	//TODO Look into RetroGen
    public static CrystalModWorldGenerator instance = new CrystalModWorldGenerator();
    
    public static final Feature<MinableRandomConfig> MINABLE_RANDOM = new MineableFeatureRandom();
    public static final Feature<NoFeatureConfig> BETTER_ROSES = new BetterRoseFeature();
	public void setupFeatures() {
		int minVeinSize = 5;
		int maxVeinSize = 8;
		int maxVeinCount = Math.min(ModConfig.WORLDGEN.CrystalOre_Per_Chunk.get(), 32);
		int maxHeight = 40;
		if(maxVeinCount > 0){
			IBlockState[] ores = new IBlockState[EnumCrystalColor.values().length];
	        for(int o = 0; o < EnumCrystalColor.values().length; o++){
	        	ores[o] = ModBlocks.crystalOreGroup.getBlock(EnumCrystalColor.values()[o]).getDefaultState();
	        }
	        MinableRandomConfig config = new MinableRandomConfig(MinableConfig.IS_ROCK, ores, minVeinSize, maxVeinSize);
			for(BiomeManager.BiomeType type : BiomeManager.BiomeType.values()){
				BiomeManager.getBiomes(type).forEach((BiomeManager.BiomeEntry biomeEntry) -> biomeEntry.biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(MINABLE_RANDOM, config, Biome.COUNT_RANGE, new CountRangeConfig(maxVeinCount, 0, 0, maxHeight))));
			}
		}
		//TODO Add Tree and Reed Gen
		BiomeDictionary.getBiomes(BiomeDictionary.Type.FOREST).forEach((Biome biome) -> biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createCompositeFeature(BETTER_ROSES, IFeatureConfig.NO_FEATURE_CONFIG, Biome.SURFACE_PLUS_32, new FrequencyConfig(5))));
	}

}
