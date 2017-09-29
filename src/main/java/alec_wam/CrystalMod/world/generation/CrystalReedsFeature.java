package alec_wam.CrystalMod.world.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.api.world.IGenerationFeature;
import alec_wam.CrystalMod.world.WorldGenCrystalReeds;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class CrystalReedsFeature implements IGenerationFeature {

	private final WorldGenCrystalReeds reedGen = new WorldGenCrystalReeds();
    public static List<Integer> reedDimBlacklist = new ArrayList<Integer>();
    @Override
	public boolean generateFeature(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
    	if(!reedDimBlacklist.contains(world.provider.getDimension())){
    		BlockPos chunkPos = new BlockPos(chunkX * 16, 0, chunkZ * 16);
    		for (int l4 = 0; l4 < Config.maximumReedsPerChunk; ++l4)
    		{
    			int j9 = random.nextInt(16) + 8;
    			int i13 = random.nextInt(16) + 8;
    			BlockPos pos = world.getHeight(chunkPos.add(j9, 0, i13));
    			int j16 = pos.getY() * 2;

    			Biome biome = world.getBiome(pos);
    			if(biome.theBiomeDecorator.reedsPerChunk >= 0){
    				if (j16 > 0)
    				{
    					int i19 = random.nextInt(j16);
    					BlockPos genPos = chunkPos.add(j9, i19, i13);
    					if(net.minecraftforge.event.terraingen.TerrainGen.decorate(world, random, genPos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.REED))
    			        {
    						return reedGen.generate(world, random, genPos) && !newGen;
    			        }
    				}
    			}
    		}
    	}
		return false;
	}

	@Override
	public boolean isRetroGenAllowed() {
		return Config.retrogenReeds;
	}

}
