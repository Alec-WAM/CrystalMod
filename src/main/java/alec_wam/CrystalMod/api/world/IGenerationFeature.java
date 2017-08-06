package alec_wam.CrystalMod.api.world;

import java.util.Random;

import net.minecraft.world.World;

public interface IGenerationFeature {

	/**
	 * 
	 * @param world World
	 * @param random Random	
	 * @param chunkX Chunk XCoord
	 * @param chunkZ Chunk ZCoord
	 * @param newGen If this a first time generation, otherwise this is retro-gen
	 * @return Return if the chunk needs to be marked dirty
	 */
	public boolean generateFeature(World world, Random random, int chunkX, int chunkZ, boolean newGen);
	
	/**
	 * This allows you to decide if you want retro-gen or not
	 * @return
	 */
	public boolean isRetroGenAllowed();
}
