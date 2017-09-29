package alec_wam.CrystalMod.world.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.api.world.IGenerationFeature;
import alec_wam.CrystalMod.blocks.BlockCrystalLog.WoodType;
import alec_wam.CrystalMod.world.WorldGenCrystalTree;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeForest;
import net.minecraftforge.common.BiomeDictionary;

public class CrystalTreeFeature implements IGenerationFeature {

    public static List<Integer> treeDimBlacklist = new ArrayList<Integer>();
    
	@Override
	public boolean generateFeature(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
		if(!treeDimBlacklist.contains(world.provider.getDimension())){
			if(random.nextInt(60) == 3){
				if(!world.getWorldInfo().getTerrainType().getName().startsWith("flat")){
					return generateCrystalTree(world, random, chunkX, chunkZ) && !newGen;
				}
			}
    	}
		return false;
	}
    
    public static boolean generateCrystalTree(final World world, final Random random, final int chunkX, final int chunkZ) {
        final int x = chunkX * 16 + random.nextInt(16);
        final int z = chunkZ * 16 + random.nextInt(16);
        final BlockPos bp = world.getHeight(new BlockPos(x, 0, z));
        Biome biome = world.getBiome(bp);
        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.FOREST) || biome instanceof BiomeForest) {
        	if(net.minecraftforge.event.terraingen.TerrainGen.decorate(world, random, bp, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.TREE))
            {
        		WoodType type = WoodType.BLUE;
        		try{
        			type = WoodType.byMetadata(MathHelper.getInt(random, 0, WoodType.values().length-1));
        		} catch(Exception e){}
        		int size = MathHelper.getInt(random, 4, 6);
        		final boolean t = new WorldGenCrystalTree(false, size, type, random.nextInt(2) == 0).generate(world, random, bp);
        		return t;
            }
        }
        return false;
    }

	@Override
	public boolean isRetroGenAllowed() {
		return Config.retrogenTrees;
	}

}
