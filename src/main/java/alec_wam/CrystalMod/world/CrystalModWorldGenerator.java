package alec_wam.CrystalMod.world;

import java.util.ArrayDeque;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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

import org.apache.commons.lang3.tuple.Pair;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.blocks.BlockCrystalOre;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.BlockCrystalLog.WoodType;
import alec_wam.CrystalMod.blocks.BlockCrystalOre.CrystalOreType;

public class CrystalModWorldGenerator implements IWorldGenerator {
    public static final String RETRO_NAME = "CrystalModGen";
    public static CrystalModWorldGenerator instance = new CrystalModWorldGenerator();

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        generateWorld(random, chunkX, chunkZ, world, true);
    }

    public void generateWorld(Random random, int chunkX, int chunkZ, World world, boolean newGen) {
        if (!newGen && !Config.retrogen) {
            return;
        }
        //Set<Integer> oregen = Config.oregenDimensions;

        if(random.nextInt(60) == 3){
        	//TODO Not allow gen in flat worlds if(!world.getWorldInfo().getTerrainType().getWorldTypeName().startsWith("flat"))
        	generateCrystalTree(world, random, chunkX, chunkZ);
        }
        
        if (world.provider.getDimension() == 0) {
            IBlockState base = Blocks.STONE.getDefaultState();
            addOreSpawn(base, world, random, chunkX * 16, chunkZ * 16,
            		Config.oreMinimumVeinSize, Config.oreMaximumVeinSize, 
            		Config.oreMaximumVeinCount,
            		Config.oreMinimumHeight, Config.oreMaximumHeight);
        }

        if (!newGen) {
            world.getChunkFromChunkCoords(chunkX, chunkZ).setChunkModified();
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
    
    @SubscribeEvent
    public void handleChunkSaveEvent(ChunkDataEvent.Save event) {
        NBTTagCompound genTag = event.getData().getCompoundTag(RETRO_NAME);
        if (!genTag.hasKey("generated")) {
            // If we did not have this key then this is a new chunk and we will have proper ores generated.
            // Otherwise we are saving a chunk for which ores are not yet generated.
            genTag.setBoolean("generated", true);
        }
        event.getData().setTag(RETRO_NAME, genTag);
    }

    @SubscribeEvent
    public void handleChunkLoadEvent(ChunkDataEvent.Load event) {
        int dim = event.getWorld().provider.getDimension();

        boolean regen = false;
        NBTTagCompound tag = (NBTTagCompound) event.getData().getTag(RETRO_NAME);
        NBTTagList list = null;
        Pair<Integer,Integer> cCoord = Pair.of(event.getChunk().xPosition, event.getChunk().zPosition);

        if (tag != null) {
            boolean generated = Config.retrogen && !tag.hasKey("generated");
            if (generated) {
                //System.out.println("[CrystalMod] Queuing Retrogen for chunk: " + cCoord.toString() + ".");
                regen = true;
            }
        } else {
            regen = Config.retrogen;
        }

        if (regen) {
            ArrayDeque<WorldTickHandler.RetroChunkCoord> chunks = WorldTickHandler.chunksToGen.get(dim);

            if (chunks == null) {
                WorldTickHandler.chunksToGen.put(dim, new ArrayDeque<WorldTickHandler.RetroChunkCoord>(128));
                chunks = WorldTickHandler.chunksToGen.get(dim);
            }
            if (chunks != null) {
                chunks.addLast(new WorldTickHandler.RetroChunkCoord(cCoord, list));
                WorldTickHandler.chunksToGen.put(dim, chunks);
            }
        }
    }

}
