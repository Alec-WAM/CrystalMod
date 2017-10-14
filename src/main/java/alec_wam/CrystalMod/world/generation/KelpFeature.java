package alec_wam.CrystalMod.world.generation;

import java.util.Random;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.api.world.IGenerationFeature;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.underwater.BlockKelp;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraftforge.common.BiomeDictionary;

public class KelpFeature implements IGenerationFeature {

	@Override
	public boolean generateFeature(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
		if(!Config.generateKelp)return false;
		
		BlockPos chunkPos = new BlockPos(chunkX * 16, 0, chunkZ * 16);
		if(random.nextInt(5) == 0)
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
	                int count = MathHelper.getInt(random, 8, 14);
	                boolean placed = false;
	                for (int i = 0; i < count; ++i)
	                {
	                    BlockPos blockpos = position.add(random.nextInt(4) - random.nextInt(4), random.nextInt(4) - random.nextInt(4), random.nextInt(4) - random.nextInt(4));
	
	                    if (world.getBlockState(blockpos).getBlock() == Blocks.WATER && ModBlocks.kelp.canBlockStay(world, blockpos, ModBlocks.kelp.getDefaultState()))
	                    {
	                        if(generateKelp(world, blockpos, random)){
	                        	placed = true;
	                        }
	                    }
	                }
	
	                return !newGen && placed;
	            }
			}
        }
		return false;
	}

	private boolean generateKelp(World world, BlockPos blockpos, Random random) {
		int height = MathHelper.getInt(random, 2, 5);
		boolean isYellow = random.nextInt(4) == 0;
		IBlockState kelpState = ModBlocks.kelp.getDefaultState().withProperty(BlockKelp.ISYELLOW, isYellow);
		boolean hasPlaced = false;
		for(int i = 0; i < height; i++){
			BlockPos pos = blockpos.add(0, i, 0);
			if (world.getBlockState(pos).getBlock() == Blocks.WATER && ModBlocks.kelp.canBlockStay(world, pos, kelpState))
            {
				world.setBlockState(pos, kelpState, 2);
				hasPlaced = true;
            }
		}
		return hasPlaced;
	}

	@Override
	public boolean isRetroGenAllowed() {
		return Config.retrogenKelp;
	}

}
