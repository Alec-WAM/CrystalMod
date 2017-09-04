package alec_wam.CrystalMod.world.generation;

import java.util.Random;

import alec_wam.CrystalMod.api.world.IGenerationFeature;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalBerryBush;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalPlant.PlantType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeForest;
import net.minecraftforge.common.BiomeDictionary;

public class CrystalBushFeature implements IGenerationFeature {

	@Override
	public boolean generateFeature(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
		BlockPos chunkPos = new BlockPos(chunkX * 16, 0, chunkZ * 16);
		if (random.nextInt(30) == 0)
		{
			int j9 = random.nextInt(16) + 8;
			int i13 = random.nextInt(16) + 8;
			BlockPos center = world.getHeight(chunkPos.add(j9, 0, i13));
			Biome biome = world.getBiome(center);
			if(BiomeDictionary.hasType(biome, BiomeDictionary.Type.FOREST) || biome instanceof BiomeForest){
				boolean placed = false;
				for (int i = 0; i < 24; ++i)
				{
					int range = 4;
					BlockPos bushPos = center.add(MathHelper.getInt(random, -range, range), MathHelper.getInt(random, -4, 4), MathHelper.getInt(random, -range, range));
					if (world.isAirBlock(bushPos.down()) && ModBlocks.crystalBush.canPlaceBlockAt(world, bushPos.down()))
					{
						int type = MathHelper.getInt(random, 0, PlantType.values().length-1);
						int age = MathHelper.getInt(random, 0, 3);
						IBlockState state = ModBlocks.crystalBush.getDefaultState().withProperty(BlockCrystalBerryBush.AGE, age).withProperty(BlockCrystalBerryBush.TYPE, PlantType.values()[type]);
						world.setBlockState(bushPos.down(), state);
						placed = true;
					}
				}
				return placed && !newGen;
			}
			/*BlockPos pos = world.getHeight(chunkPos.add(j9, 0, i13));
			pos = pos.up();
			Biome biome = world.getBiome(pos);
			if(BiomeDictionary.hasType(biome, BiomeDictionary.Type.MAGICAL) || biome instanceof BiomeForest){
				for (int i = 0; i < 10; ++i)
				{
					BlockPos blockpos = pos.add(MathHelper.getInt(random, -4, 4), 0, MathHelper.getInt(random, -4, 4));

					if (world.isAirBlock(blockpos))
					{
						BlockPos blockpos1 = blockpos.down();
						int type = MathHelper.getInt(random, 0, PlantType.values().length-1);
						int age = MathHelper.getInt(random, 0, 3);
						Block block = ModBlocks.crystalBushBlue;
						if(type == PlantType.RED.getMeta()){
							block = ModBlocks.crystalBushRed;
						}
						if(type == PlantType.GREEN.getMeta()){
							block = ModBlocks.crystalBushGreen;
						}
						if(type == PlantType.DARK.getMeta()){
							block = ModBlocks.crystalBushDark;
						}
						IBlockState state = block.getDefaultState().withProperty(BlockCrystalBerryBush.AGE, age);
						if(block.canPlaceBlockAt(world, blockpos1)){
							world.setBlockState(blockpos1, state);
						}
					}
				}
				return !newGen;
			}*/
		}
		return false;
	}

	@Override
	public boolean isRetroGenAllowed() {
		return false;
	}

}
