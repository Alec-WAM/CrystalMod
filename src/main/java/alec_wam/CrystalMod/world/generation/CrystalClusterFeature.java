package alec_wam.CrystalMod.world.generation;

import java.util.Random;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.api.world.IGenerationFeature;
import alec_wam.CrystalMod.tiles.cluster.BlockCrystalCluster.EnumClusterType;
import alec_wam.CrystalMod.tiles.cluster.TileCrystalCluster;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CrystalClusterFeature implements IGenerationFeature {

	@Override
	public boolean generateFeature(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
		boolean debug = false;
		int spawnChance = debug ? 24 : Config.clusterSpawnChance;
		if(random.nextInt(spawnChance) == 0){
			int tries = debug ? 8 : Config.clusterSpawnTries;
			for(int i = 0; i < tries; i++){
				final int x = chunkX * 16 + random.nextInt(16);
		        final int z = chunkZ * 16 + random.nextInt(16);
		        int y = world.getTopSolidOrLiquidBlock(new BlockPos(x, world.getActualHeight(), z)).getY()-1;
		        boolean empty = false;
		        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);
	            while (y > 1 && !empty) {
	                if (world.isAirBlock(pos)) {
	                	empty = true;
	                }
	                y--;
	                pos.setPos(x, y, z);
	            }
	            if (empty) {
	                while (y > 1 && empty) {
	                    if (!world.isAirBlock(pos)) {
	                    	empty = false;
	                    } else {
	                        y--;
	                        pos.setPos(x, y, z);
	                    }
	                }
	                if (!empty) {
	                    if (canPlaceCluster(world, pos)) {
	                        if (debug || Config.retrogenInfo) {
	                            ModLogger.info("Spawned a Crystal Cluster at: " + x + "," + y + "," + z);
	                        }
	                        int typeIndex = MathHelper.getInt(random, 0, EnumClusterType.values().length-1);
	                        EnumClusterType type = EnumClusterType.values()[typeIndex];
	                        TileCrystalCluster.createRandomCluster(world, random, new BlockPos(pos.setPos(x, y+1, z)), type, 10, 44, 1, 3, true);
	                        return !newGen;
	                    }
	                }
	            }
			}
		}
		return false;
	}
    
    public boolean canPlaceCluster(World world, BlockPos pos){
    	return world.getBlockState(pos).getBlock() == Blocks.STONE;
    }

	@Override
	public boolean isRetroGenAllowed() {
		return Config.retrogenClusters;
	}

}
