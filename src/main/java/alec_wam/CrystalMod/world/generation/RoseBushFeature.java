package alec_wam.CrystalMod.world.generation;

import java.util.Random;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.api.world.IGenerationFeature;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.decorative.BlockBetterRoses;
import alec_wam.CrystalMod.blocks.decorative.BlockBetterRoses.RoseType;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class RoseBushFeature implements IGenerationFeature {

	@Override
	public boolean generateFeature(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
		if(!Config.generateRoses) return false;
		BlockPos chunkPos = new BlockPos(chunkX * 16, 0, chunkZ * 16);
		if(random.nextInt(5) == 0)
        {
			int l = random.nextInt(16) + 8;
			int i1 = random.nextInt(16) + 8;
			BlockPos pos = chunkPos.add(l, 0, i1);
			int j1 = random.nextInt(world.getHeight(pos).getY() + 32);
			Biome biome = world.getBiomeForCoordsBody(pos);
			if(biome == Biomes.FOREST || BiomeDictionary.hasType(biome, Type.FOREST)){
				boolean flag = false;
				BlockPos position = new BlockPos(chunkPos.getX() + l, j1, chunkPos.getZ() + i1);
				
				for (int i = 0; i < 64; ++i)
				{
					BlockPos blockpos = position.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
	
					if (world.isAirBlock(blockpos) && (!world.provider.hasNoSky() || blockpos.getY() < 254) && ModBlocks.roseBush.canPlaceBlockAt(world, blockpos))
					{
						RoseType[] normalTypes = {RoseType.ORANGE, RoseType.MAGENTA, RoseType.YELLOW, RoseType.PINK, RoseType.CYAN, RoseType.PURPLE}; //Normal Colors
						RoseType roseType = normalTypes[MathHelper.getInt(random, 0, 5)]; //Choose a random color in the list
						if(random.nextInt(100) <= 12){ //12% chance
						    roseType = RoseType.WHITE; //Yay! White!
						}
						world.setBlockState(blockpos, ModBlocks.roseBush.getDefaultState().withProperty(BlockBetterRoses.COLOR, roseType), 2);
						world.setBlockState(blockpos.up(), ModBlocks.roseBush.getDefaultState().withProperty(BlockBetterRoses.COLOR, roseType).withProperty(BlockBetterRoses.TOP, true), 2);
				    	flag = true;
					}
				}
				return !newGen && flag;
			}
        }
		return false;
	}

	@Override
	public boolean isRetroGenAllowed() {
		return Config.retrogenRoses;
	}

}
