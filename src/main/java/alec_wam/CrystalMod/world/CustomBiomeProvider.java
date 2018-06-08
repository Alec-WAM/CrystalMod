package alec_wam.CrystalMod.world;

import java.util.List;

import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.gen.layer.GenLayerZoom;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class CustomBiomeProvider extends BiomeProvider {
    
	public CustomBiomeProvider(World world, List<Biome> spawnBiomes, List<Biome> normalBiomes, List<Biome> rareBiomes) {
        super();
        getBiomesToSpawnIn().clear();
        getBiomesToSpawnIn().addAll(spawnBiomes);
        makeLayers(world.getSeed(), normalBiomes, rareBiomes);
    }

	private void makeLayers(long seed, List<Biome> normalBiomes, List<Biome> rareBiomes) {
		GenLayer biomes = new GenLayerCustomList(1L, normalBiomes, rareBiomes, 16);

		biomes = new GenLayerZoom(1000L, biomes);
		biomes = new GenLayerZoom(1001, biomes);
		//biomes = new GenLayerTFBiomeStabilize(700L, biomes);
		biomes = new GenLayerZoom(1002, biomes);
		biomes = new GenLayerZoom(1003, biomes);
		biomes = new GenLayerZoom(1004, biomes);
		biomes = new GenLayerZoom(1005, biomes);

		//GenLayer riverLayer = /*new GenLayerTFStream(1L, biomes);
		//riverLayer =*/ new GenLayerSmooth(7000L, biomes);
		//biomes = new GenLayerTFRiverMix(100L, biomes, riverLayer);

		// do "voronoi" zoom
		GenLayer genlayervoronoizoom = new GenLayerVoronoiZoom(10L, biomes);

		biomes.initWorldGenSeed(seed);
		genlayervoronoizoom.initWorldGenSeed(seed);

		//genBiomes = biomes;
		ReflectionHelper.setPrivateValue(BiomeProvider.class, this, biomes, 1);
		//biomeIndexLayer = genlayervoronoizoom;
		ReflectionHelper.setPrivateValue(BiomeProvider.class, this, genlayervoronoizoom, 2);
	}
}
