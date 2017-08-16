package alec_wam.CrystalMod.world.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.api.world.IGenerationFeature;
import alec_wam.CrystalMod.blocks.BlockCrystalOre.CrystalOreType;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.world.WorldGenMinableRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrystalOreFeature implements IGenerationFeature {

	public static List<Integer> oreDimBlacklist = new ArrayList<Integer>();
    
    @Override
	public boolean generateFeature(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
		int dimension = world.provider.getDimension();
    	if(dimension == -1 && Config.generateOreNether){
			//Nether
			IBlockState base = Blocks.NETHERRACK.getDefaultState();
			addNetherOreSpawn(base, world, random, chunkX * 16, chunkZ * 16,
					Config.oreNetherMinimumVeinSize, Config.oreNetherMaximumVeinSize, 
					Config.oreNetherMaximumVeinCount,
					Config.oreNetherMinimumHeight, Config.oreNetherMaximumHeight);
	        return !newGen;
		}
		else if(dimension == 1 && Config.generateOreEnd){
			//End
			IBlockState base = Blocks.END_STONE.getDefaultState();
			addEndOreSpawn(base, world, random, chunkX * 16, chunkZ * 16,
					Config.oreEndMinimumVeinSize, Config.oreEndMaximumVeinSize, 
					Config.oreEndMaximumVeinCount,
					Config.oreEndMinimumHeight, Config.oreEndMaximumHeight);
	        return !newGen;
		}
		else if(dimension == 0 && Config.generateOreOverworld){
			//Overworld
			IBlockState base = Blocks.STONE.getDefaultState();
			addOverworldOreSpawn(base, world, random, chunkX * 16, chunkZ * 16,
					Config.oreMinimumVeinSize, Config.oreMaximumVeinSize, 
					Config.oreMaximumVeinCount,
					Config.oreMinimumHeight, Config.oreMaximumHeight);
	        return !newGen;
		}
		else if(Config.generateOreOther && !oreDimBlacklist.contains(dimension)){
			//Other Dims
			IBlockState base = Blocks.STONE.getDefaultState();
			addOverworldOreSpawn(base, world, random, chunkX * 16, chunkZ * 16,
					Config.oreOtherMinimumVeinSize, Config.oreOtherMaximumVeinSize, 
					Config.oreOtherMaximumVeinCount,
					Config.oreOtherMinimumHeight, Config.oreOtherMaximumHeight);
	        return !newGen;
		}
        return false;
	}


    public void addOverworldOreSpawn(IBlockState targetBlock, World world, Random random, int blockXPos, int blockZPos, int minVeinSize, int maxVeinSize, int chancesToSpawn, int minY, int maxY) {
    	for (int i = 0 ; i < chancesToSpawn ; i++) {
        	int posX = blockXPos + random.nextInt(16);
            int posY = minY + random.nextInt(maxY - minY);
            int posZ = blockZPos + random.nextInt(16);
            
            IBlockState[] ores = {ModBlocks.crystalOre.getStateFromMeta(CrystalOreType.BLUE.getMeta()), ModBlocks.crystalOre.getStateFromMeta(CrystalOreType.RED.getMeta()), ModBlocks.crystalOre.getStateFromMeta(CrystalOreType.GREEN.getMeta()), ModBlocks.crystalOre.getStateFromMeta(CrystalOreType.DARK.getMeta())};
            
        	WorldGenMinableRandom minable = new WorldGenMinableRandom(ores, (minVeinSize - random.nextInt(maxVeinSize - minVeinSize)), net.minecraft.block.state.pattern.BlockMatcher.forBlock(targetBlock.getBlock()));
        	minable.generate(world, random, new BlockPos(posX, posY, posZ));
        }
    }
    
    public void addNetherOreSpawn(IBlockState targetBlock, World world, Random random, int blockXPos, int blockZPos, int minVeinSize, int maxVeinSize, int chancesToSpawn, int minY, int maxY) {
    	for (int i = 0 ; i < chancesToSpawn ; i++) {
        	int posX = blockXPos + random.nextInt(16);
            int posY = minY + random.nextInt(maxY - minY);
            int posZ = blockZPos + random.nextInt(16);
            
            IBlockState[] ores = {ModBlocks.crystalOre.getStateFromMeta(CrystalOreType.BLUE_NETHER.getMeta()), ModBlocks.crystalOre.getStateFromMeta(CrystalOreType.RED_NETHER.getMeta()), ModBlocks.crystalOre.getStateFromMeta(CrystalOreType.GREEN_NETHER.getMeta()), ModBlocks.crystalOre.getStateFromMeta(CrystalOreType.DARK_NETHER.getMeta())};
            
        	WorldGenMinableRandom minable = new WorldGenMinableRandom(ores, (minVeinSize - random.nextInt(maxVeinSize - minVeinSize)), net.minecraft.block.state.pattern.BlockMatcher.forBlock(targetBlock.getBlock()));
        	minable.generate(world, random, new BlockPos(posX, posY, posZ));
        }
    }
    
    public void addEndOreSpawn(IBlockState targetBlock, World world, Random random, int blockXPos, int blockZPos, int minVeinSize, int maxVeinSize, int chancesToSpawn, int minY, int maxY) {
    	for (int i = 0 ; i < chancesToSpawn ; i++) {
        	int posX = blockXPos + random.nextInt(16);
            int posY = minY + random.nextInt(maxY - minY);
            int posZ = blockZPos + random.nextInt(16);
            
            IBlockState[] ores = {ModBlocks.crystalOre.getStateFromMeta(CrystalOreType.BLUE_END.getMeta()), ModBlocks.crystalOre.getStateFromMeta(CrystalOreType.RED_END.getMeta()), ModBlocks.crystalOre.getStateFromMeta(CrystalOreType.GREEN_END.getMeta()), ModBlocks.crystalOre.getStateFromMeta(CrystalOreType.DARK_END.getMeta())};
            
        	WorldGenMinableRandom minable = new WorldGenMinableRandom(ores, (minVeinSize - random.nextInt(maxVeinSize - minVeinSize)), net.minecraft.block.state.pattern.BlockMatcher.forBlock(targetBlock.getBlock()));
        	minable.generate(world, random, new BlockPos(posX, posY, posZ));
        }
    }

	@Override
	public boolean isRetroGenAllowed() {
		return Config.retrogenOres;
	}

}
