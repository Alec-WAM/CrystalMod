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
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.BlockCrystalLog.WoodType;
import alec_wam.CrystalMod.blocks.BlockCrystalOre.CrystalOreType;
import alec_wam.CrystalMod.util.ModLogger;

public class CrystalModWorldGenerator implements IWorldGenerator {
    public static CrystalModWorldGenerator instance = new CrystalModWorldGenerator();

    public static List<Integer> oreDimBlacklist = new ArrayList<Integer>();
    public static List<Integer> treeDimBlacklist = new ArrayList<Integer>();
    public static List<Integer> reedDimBlacklist = new ArrayList<Integer>();
    
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        generateWorld(random, chunkX, chunkZ, world, true);
    }

    public void generateWorld(Random random, int chunkX, int chunkZ, World world, boolean newGen) {
        boolean oreDirty = generateOres(random, chunkX, chunkZ, world, newGen);
        boolean treeDirty = generateTrees(random, chunkX, chunkZ, world, newGen);
        boolean reedsDirty = generateReeds(random, chunkX, chunkZ, world, newGen);
    	
        boolean dirty = oreDirty || treeDirty || reedsDirty;
        if (!newGen && dirty) {
            world.getChunkFromChunkCoords(chunkX, chunkZ).setChunkModified();
        }
    }
    
    public boolean generateOres(Random random, int chunkX, int chunkZ, World world, boolean newGen){
    	if(!oreDimBlacklist.contains(world.provider.getDimension())){
			if(newGen || Config.retrogenOres){
				boolean debug = false;
				if(debug){
					IBlockState base = Blocks.AIR.getDefaultState();
		            addOreSpawn(base, world, random, chunkX * 16, chunkZ * 16,
		            		Config.oreMinimumVeinSize, Config.oreMaximumVeinSize, 
		            		2,
		            		0, 20);
				} else {
					IBlockState base = Blocks.STONE.getDefaultState();
		            addOreSpawn(base, world, random, chunkX * 16, chunkZ * 16,
		            		Config.oreMinimumVeinSize, Config.oreMaximumVeinSize, 
		            		Config.oreMaximumVeinCount,
		            		Config.oreMinimumHeight, Config.oreMaximumHeight);
				}
	            return true;
			}
    	}
    	return false;
    }


    public void addOreSpawn(IBlockState targetBlock, World world, Random random, int blockXPos, int blockZPos, int minVeinSize, int maxVeinSize, int chancesToSpawn, int minY, int maxY) {
    	for (int i = 0 ; i < chancesToSpawn ; i++) {
        	int posX = blockXPos + random.nextInt(16);
            int posY = minY + random.nextInt(maxY - minY);
            int posZ = blockZPos + random.nextInt(16);
            
            IBlockState[] ores = {ModBlocks.crystalOre.getStateFromMeta(CrystalOreType.BLUE.getMeta()), ModBlocks.crystalOre.getStateFromMeta(CrystalOreType.RED.getMeta()), ModBlocks.crystalOre.getStateFromMeta(CrystalOreType.GREEN.getMeta()), ModBlocks.crystalOre.getStateFromMeta(CrystalOreType.DARK.getMeta())};
            
        	WorldGenMinableRandom minable = new WorldGenMinableRandom(ores, (minVeinSize - random.nextInt(maxVeinSize - minVeinSize)), net.minecraft.block.state.pattern.BlockMatcher.forBlock(targetBlock.getBlock()));
        	minable.generate(world, random, new BlockPos(posX, posY, posZ));
        }
    }

    public boolean generateTrees(Random random, int chunkX, int chunkZ, World world, boolean newGen){
    	if(!treeDimBlacklist.contains(world.provider.getDimension())){
			if(newGen || Config.retrogenTrees){
				if(random.nextInt(60) == 3){
		        	if(!world.getWorldInfo().getTerrainType().getWorldTypeName().startsWith("flat")){
			        	return generateCrystalTree(world, random, chunkX, chunkZ);
		            }
		        }
			}
    	}
    	return false;
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
    private final WorldGenCrystalReeds reedGen = new WorldGenCrystalReeds();
    public boolean generateReeds(Random random, int chunkX, int chunkZ, World world, boolean newGen){
    	if(!reedDimBlacklist.contains(world.provider.getDimension())){
			if(newGen || Config.retrogenReeds){
				BlockPos chunkPos = new BlockPos(chunkX * 16, 0, chunkZ * 16);
				for (int l4 = 0; l4 < Config.maximumReedsPerChunk; ++l4)
		        {
		            int j9 = random.nextInt(16) + 8;
		            int i13 = random.nextInt(16) + 8;
		            BlockPos pos = world.getHeight(chunkPos.add(j9, 0, i13));
		            int j16 = pos.getY() * 2;
		            
		            Biome biome = world.getBiomeGenForCoords(pos);
		            if(biome.theBiomeDecorator.reedsPerChunk >= 0){
			            if (j16 > 0)
			            {
			                int i19 = random.nextInt(j16);
			                return reedGen.generate(world, random, chunkPos.add(j9, i19, i13));
			            }
		            }
		        }
			}
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
