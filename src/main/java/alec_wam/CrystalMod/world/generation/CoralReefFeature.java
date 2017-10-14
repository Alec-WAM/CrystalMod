package alec_wam.CrystalMod.world.generation;

import java.util.Random;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.api.world.IGenerationFeature;
import alec_wam.CrystalMod.blocks.underwater.BlockCoral;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraftforge.common.BiomeDictionary;

public class CoralReefFeature implements IGenerationFeature {

	@Override
	public boolean generateFeature(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
		BlockPos chunkPos = new BlockPos(chunkX * 16, 0, chunkZ * 16);
		if (Config.coralChance > 0 && random.nextInt(Config.coralChance) == 0)
        {
            int j7 = random.nextInt(16) + 8;
            int i11 = random.nextInt(16) + 8;
            BlockPos center = chunkPos.add(j7, 0, i11);
            int k14 = world.getHeight(center).getY() * 2;

            Biome biome = world.getBiome(chunkPos);
			if(BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN) || biome instanceof BiomeOcean){
	            if (k14 > 0)
	            {
	                int l17 = random.nextInt(k14);
	                BlockPos position = chunkPos.add(j7, l17, i11);
	                
	                for (IBlockState iblockstate = world.getBlockState(position); (iblockstate.getBlock().isAir(iblockstate, world, position) || iblockstate.getMaterial() == Material.WATER) && position.getY() > 0; iblockstate = world.getBlockState(position))
	                {
	                    position = position.down();
	                }
	
	                BlockPos blockpos = position.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));

	                if (world.getBlockState(blockpos).getBlock() == Blocks.WATER && world.getBlockState(blockpos.down()).getMaterial() == Material.SAND)
	                {
	                	BlockCoral.generateCoralCluster(world, blockpos, MathHelper.getInt(random, 20, 40), false, false);
	                }
	
	                return !newGen;
	            }
			}
        }
		return false;
	}

	@Override
	public boolean isRetroGenAllowed() {
		return Config.retrogenCoral;
	}

}
