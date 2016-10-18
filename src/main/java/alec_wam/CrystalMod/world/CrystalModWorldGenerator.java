package alec_wam.CrystalMod.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeForest;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.BlockCrystalOre;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.BlockCrystalLog.WoodType;
import alec_wam.CrystalMod.blocks.BlockCrystalOre.CrystalOreType;
import alec_wam.CrystalMod.util.ModLogger;

public class CrystalModWorldGenerator implements IWorldGenerator {
    public static CrystalModWorldGenerator instance = new CrystalModWorldGenerator();

    public static List<Integer> oreDimBlacklist = new ArrayList<Integer>();
    public static List<Integer> treeDimBlacklist = new ArrayList<Integer>();
    
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        generateWorld(random, chunkX, chunkZ, world, true);
    }

    public void generateWorld(Random random, int chunkX, int chunkZ, World world, boolean newGen) {
        generateOres(random, chunkX, chunkZ, world, newGen);
        generateTrees(random, chunkX, chunkZ, world, newGen);
    	

        if (!newGen) {
            world.getChunkFromChunkCoords(chunkX, chunkZ).setChunkModified();
        }
    }
    
    public void generateOres(Random random, int chunkX, int chunkZ, World world, boolean newGen){
    	if(!oreDimBlacklist.contains(world.provider.getDimension())){
			if(newGen || Config.retrogenOres){
				IBlockState base = Blocks.STONE.getDefaultState();
	            addOreSpawn(base, world, random, chunkX * 16, chunkZ * 16,
	            		Config.oreMinimumVeinSize, Config.oreMaximumVeinSize, 
	            		Config.oreMaximumVeinCount,
	            		Config.oreMinimumHeight, Config.oreMaximumHeight);


	            if (!newGen) {
	                world.getChunkFromChunkCoords(chunkX, chunkZ).setChunkModified();
	            }
			}
    	}
    }


    public void addOreSpawn(IBlockState targetBlock, World world, Random random, int blockXPos, int blockZPos, int minVeinSize, int maxVeinSize, int chancesToSpawn, int minY, int maxY) {
    	for (int i = 0 ; i < chancesToSpawn ; i++) {
        	int posX = blockXPos + random.nextInt(16);
            int posY = minY + random.nextInt(maxY - minY);
            int posZ = blockZPos + random.nextInt(16);
            int type = MathHelper.getRandomIntegerInRange(random, 0, CrystalOreType.values().length-1);
            
        	WorldGenMinableRandom minable = new WorldGenMinableRandom(ModBlocks.crystalOre.getDefaultState().withProperty(BlockCrystalOre.TYPE, CrystalOreType.values()[type]), (minVeinSize - random.nextInt(maxVeinSize - minVeinSize)), net.minecraft.block.state.pattern.BlockMatcher.forBlock(targetBlock.getBlock()));
        	minable.generate(world, random, new BlockPos(posX, posY, posZ));
        }
    }

    public void generateTrees(Random random, int chunkX, int chunkZ, World world, boolean newGen){
    	if(!treeDimBlacklist.contains(world.provider.getDimension())){
			if(newGen || Config.retrogenTrees){
				if(random.nextInt(60) == 3){
		        	if(!world.getWorldInfo().getTerrainType().getWorldTypeName().startsWith("flat")){
			        	generateCrystalTree(world, random, chunkX, chunkZ);
			            if (!newGen) {
			                world.getChunkFromChunkCoords(chunkX, chunkZ).setChunkModified();
			            }
		            }
		        }
			}
    	}
    }
    
    public static boolean generateCrystalTree(final World world, final Random random, final int chunkX, final int chunkZ) {
        final int x = chunkX * 16 + random.nextInt(16);
        final int z = chunkZ * 16 + random.nextInt(16);
        final BlockPos bp = world.getHeight(new BlockPos(x, 0, z));
        Biome biome = world.getBiomeGenForCoords(bp);
        if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.MAGICAL) || biome instanceof BiomeForest) {
        	WoodType type = WoodType.BLUE;
        	try{
        		type = WoodType.byMetadata(MathHelper.getRandomIntegerInRange(random, 0, WoodType.values().length-1));
        	} catch(Exception e){}
        	int size = MathHelper.getRandomIntegerInRange(random, 4, 6);
            final boolean t = new WorldGenCrystalTree(false, size, type, random.nextInt(2) == 0).generate(world, random, bp);
            return t;
        }
        return false;
    }
    
    public static String NBT_RETRO = "CrystalModGen";
    
    //Credit: https://github.com/BluSunrize/ImmersiveEngineering/blob/master/src/main/java/blusunrize/immersiveengineering/common/world/IEWorldGen.java
    
    @SubscribeEvent
    public void handleChunkSaveEvent(ChunkDataEvent.Save event) {
    	NBTTagCompound nbt = new NBTTagCompound();
		event.getData().setTag(NBT_RETRO, nbt);
		nbt.setBoolean(Config.retrogenID, true);
    }

    @SubscribeEvent
    public void handleChunkLoadEvent(ChunkDataEvent.Load event) {
        
    	int dimension = event.getWorld().provider.getDimension();
		if((!event.getData().getCompoundTag(NBT_RETRO).hasKey(Config.retrogenID)) && (Config.retrogenOres || Config.retrogenTrees))
		{
			if(Config.retrogenInfo)
				ModLogger.info("Chunk "+event.getChunk().getChunkCoordIntPair()+" has been flagged for RetroGen by CM.");
			WorldTickHandler.retrogenChunks.put(dimension, event.getChunk().getChunkCoordIntPair());
		}
    }

}
