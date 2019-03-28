package alec_wam.CrystalMod.world;

import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.world.features.MinableRandomConfig;
import alec_wam.CrystalMod.world.features.MineableFeatureRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.MinableConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraftforge.common.BiomeManager;

public class CrystalModWorldGenerator{	
	//TODO Look into RetroGen
    public static CrystalModWorldGenerator instance = new CrystalModWorldGenerator();
    
    public static final Feature<MinableRandomConfig> MINABLE_RANDOM = new MineableFeatureRandom();
	public void setupFeatures() {
		int minVeinSize = 5;
		int maxVeinSize = 8;
		int maxVeinCount = 2;
		int minHeight = 0;
		int maxHeight = 20;
		IBlockState[] ores = new IBlockState[EnumCrystalColor.values().length];
        for(int o = 0; o < EnumCrystalColor.values().length; o++){
        	ores[o] = ModBlocks.crystalOreGroup.getBlock(EnumCrystalColor.values()[o]).getDefaultState();
        }
        MinableRandomConfig config = new MinableRandomConfig(MinableConfig.IS_ROCK, ores, minVeinSize, maxVeinSize);
		for(BiomeManager.BiomeType type : BiomeManager.BiomeType.values()){
			BiomeManager.getBiomes(type).forEach((BiomeManager.BiomeEntry biomeEntry) -> biomeEntry.biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(MINABLE_RANDOM, config, Biome.COUNT_RANGE, new CountRangeConfig(maxVeinCount, minHeight, minHeight, maxHeight))));
		}
	}

}
