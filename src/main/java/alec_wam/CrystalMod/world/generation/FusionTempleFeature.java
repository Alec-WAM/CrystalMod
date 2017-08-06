package alec_wam.CrystalMod.world.generation;

import java.util.Random;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.api.world.IGenerationFeature;
import alec_wam.CrystalMod.world.structures.MapGenFusionTemple;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class FusionTempleFeature implements IGenerationFeature {

	public static MapGenFusionTemple fusionTempleGen = new MapGenFusionTemple();

    @Override
	public boolean generateFeature(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
    	if(world.provider.getDimension() == 0 && Config.generateFusionTemple){
        	fusionTempleGen.generate(world, chunkX, chunkZ, null);
        	fusionTempleGen.generateStructure(world, random, new ChunkPos(chunkX, chunkZ));
        	return !newGen;
        }
    	return false;
	}

	@Override
	public boolean isRetroGenAllowed() {
		return false;
	}

}
