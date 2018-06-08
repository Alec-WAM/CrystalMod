package alec_wam.CrystalMod.world;

import java.util.List;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;


public class GenLayerCustomList extends GenLayer {

	public List<Biome> commonBiomes, rareBiomes;
	public int rareChance;
	
	public GenLayerCustomList(long l, GenLayer genlayer, List<Biome> commonBiomes, List<Biome> rareBiomes, int rareChance) {
		super(l);
		parent = genlayer;
		this.commonBiomes = commonBiomes;
		this.rareBiomes = rareBiomes;
		this.rareChance = rareChance;
	}

	public GenLayerCustomList(long l, List<Biome> commonBiomes, List<Biome> rareBiomes, int rareChance) {
		super(l);
		this.commonBiomes = commonBiomes;
		this.rareBiomes = rareBiomes;
		this.rareChance = rareChance;
	}

	@Override
	public int[] getInts(int x, int z, int width, int depth) {
		int dest[] = IntCache.getIntCache(width * depth);
		for (int dz = 0; dz < depth; dz++) {
			for (int dx = 0; dx < width; dx++) {
				initChunkSeed(dx + x, dz + z);
				if (!rareBiomes.isEmpty() && nextInt(rareChance) == 0) {
					// make rare biome
					dest[dx + dz * width] = Biome.getIdForBiome(rareBiomes.get(nextInt(rareBiomes.size())));
				} else {
					// make common biome
					dest[dx + dz * width] = Biome.getIdForBiome(commonBiomes.get(nextInt(commonBiomes.size())));
				}
			}

		}
		return dest;
	}
}
