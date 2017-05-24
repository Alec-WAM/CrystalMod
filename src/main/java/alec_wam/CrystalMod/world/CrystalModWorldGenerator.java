package alec_wam.CrystalMod.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeForest;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.tiles.cluster.BlockCrystalCluster.EnumClusterType;
import alec_wam.CrystalMod.tiles.cluster.TileCrystalCluster;
import alec_wam.CrystalMod.blocks.BlockCrystalLog.WoodType;
import alec_wam.CrystalMod.blocks.BlockCrystalOre.CrystalOreType;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.world.structures.CrystalWell;
import alec_wam.CrystalMod.world.structures.MapGenFusionTemple;

public class CrystalModWorldGenerator implements IWorldGenerator {
    public static CrystalModWorldGenerator instance = new CrystalModWorldGenerator();

    public static List<Integer> oreDimBlacklist = new ArrayList<Integer>();
    public static List<Integer> treeDimBlacklist = new ArrayList<Integer>();
    public static List<Integer> reedDimBlacklist = new ArrayList<Integer>();
    
    public static MapGenFusionTemple fusionTempleGen = new MapGenFusionTemple();
    
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        generateWorld(random, chunkX, chunkZ, world, true);
        if(world.provider.getDimension() == 0 && Config.generateFusionTemple){
        	fusionTempleGen.generate(world, chunkX, chunkZ, null);
        	fusionTempleGen.generateStructure(world, random, new ChunkPos(chunkX, chunkZ));
        }
        
        if(world.provider.getDimensionType() == DimensionType.NETHER && Config.generateNetherWell){
        	if(Config.netherWellChance > 0 && random.nextInt(Config.netherWellChance) == 0){
        		int i = random.nextInt(16) + 8;
        		int j = random.nextInt(16) + 8;
        		BlockPos pos = new BlockPos((chunkX * 16) + i, 0, (chunkZ * 16) + j);
        		BlockPos blockpos = null;
        		
        		int y = 11;
        		while(blockpos == null && y < 120){
        			BlockPos testPos = pos.up(y);
        			List<BlockPos> posList = BlockUtil.getBlocksInBB(testPos.north().west(), 7, 1, 7);
        			int rackNeeded = posList.size();
        			for(BlockPos pos2 : posList){
        				if(world.getBlockState(pos2) == Blocks.NETHERRACK.getDefaultState()){
        					rackNeeded--;
        				}
        			}
        			if(rackNeeded <=0){
        				int airNeeded = 75;
        				for(int x = 0; x < 5; x++){
        					for(int y2 = 0; y2 < 3; y2++){
        						for(int z = 0; z < 5; z++){
        							if(world.isAirBlock(testPos.up().add(x, y2, z))){
        								airNeeded--;
        							}
        						}
        					}
        				}
        				if(airNeeded <= 0){
        					blockpos = testPos;
        				} else {
        					y++;
        				}
        			}else {
        				y++;
        			}
        		}
        		if(blockpos !=null){
        			if(Config.retrogenInfo)ModLogger.info("Nether Well: "+blockpos);
                	CrystalWell.generateNetherWell(world, blockpos, random);
        		}
        	}
        }
        
        if(world.provider.getDimensionType() == DimensionType.THE_END && Config.generateEndWell){
        	//Outside of Main Island Range
        	if((long)chunkX * (long)chunkX + (long)chunkZ * (long)chunkZ > 4096L){
        		if(Config.endWellChance > 0 && random.nextInt(Config.endWellChance) == 0){
        			int i = random.nextInt(16) + 8;
                    int j = random.nextInt(16) + 8;
        			BlockPos pos = new BlockPos((chunkX * 16) + i, 0, (chunkZ * 16) + j);
                    BlockPos blockpos = world.getHeight(pos).up();
                    while (world.isAirBlock(blockpos) && blockpos.getY() > 7)
                    {
                    	blockpos = blockpos.down();
                    }
                    
                    BlockPos bottom = blockpos.down(7);
                    List<BlockPos> posList = BlockUtil.getBlocksInBB(bottom, 7, 1, 7);
                    boolean pass = true;
                    check : for(BlockPos pos2 : posList){
                    	if(world.isAirBlock(pos2)){
                    		pass = false;
                    		break check;
                    	}
                    }
                    if(pass){
                    	if(Config.retrogenInfo)ModLogger.info("End Well: "+blockpos);
                    	CrystalWell.generateEndWell(world, blockpos, random);
                    }
        		}
        	}
        }
    }

    public void generateWorld(Random random, int chunkX, int chunkZ, World world, boolean newGen) {
        boolean oreDirty = generateOres(random, chunkX, chunkZ, world, newGen);
        boolean treeDirty = generateTrees(random, chunkX, chunkZ, world, newGen);
        boolean reedsDirty = generateReeds(random, chunkX, chunkZ, world, newGen);
        boolean clusterDirty = generateClusters(random, chunkX, chunkZ, world, newGen);
    	
        boolean dirty = oreDirty || treeDirty || reedsDirty || clusterDirty;
        if (!newGen && dirty) {
            world.getChunkFromChunkCoords(chunkX, chunkZ).setChunkModified();
        }
    }

	public boolean generateOres(Random random, int chunkX, int chunkZ, World world, boolean newGen){
		int dimension = world.provider.getDimension();
    	//if(!oreDimBlacklist.contains(dimension)){
			if(newGen || Config.retrogenOres){
				if(dimension == -1 && Config.generateOreNether){
					//Nether
					IBlockState base = Blocks.NETHERRACK.getDefaultState();
					addNetherOreSpawn(base, world, random, chunkX * 16, chunkZ * 16,
							Config.oreNetherMinimumVeinSize, Config.oreNetherMaximumVeinSize, 
							Config.oreNetherMaximumVeinCount,
							Config.oreNetherMinimumHeight, Config.oreNetherMaximumHeight);
				}
				else if(dimension == 1 && Config.generateOreEnd){
					//End
					IBlockState base = Blocks.END_STONE.getDefaultState();
					addEndOreSpawn(base, world, random, chunkX * 16, chunkZ * 16,
							Config.oreEndMinimumVeinSize, Config.oreEndMaximumVeinSize, 
							Config.oreEndMaximumVeinCount,
							Config.oreEndMinimumHeight, Config.oreEndMaximumHeight);
				}
				else if(dimension == 0 && Config.generateOreOverworld){
					//Overworld
					IBlockState base = Blocks.STONE.getDefaultState();
					addOverworldOreSpawn(base, world, random, chunkX * 16, chunkZ * 16,
							Config.oreMinimumVeinSize, Config.oreMaximumVeinSize, 
							Config.oreMaximumVeinCount,
							Config.oreMinimumHeight, Config.oreMaximumHeight);
				}
				else if(Config.generateOreOther){
					//Other Dims
					IBlockState base = Blocks.STONE.getDefaultState();
					addOverworldOreSpawn(base, world, random, chunkX * 16, chunkZ * 16,
							Config.oreOtherMinimumVeinSize, Config.oreOtherMaximumVeinSize, 
							Config.oreOtherMaximumVeinCount,
							Config.oreOtherMinimumHeight, Config.oreOtherMaximumHeight);
				}
	            return true;
			}
    	//}
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

    public boolean generateTrees(Random random, int chunkX, int chunkZ, World world, boolean newGen){
    	if(!treeDimBlacklist.contains(world.provider.getDimension())){
			if(newGen || Config.retrogenTrees){
				if(random.nextInt(60) == 3){
		        	if(!world.getWorldInfo().getTerrainType().getName().startsWith("flat")){
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
        Biome biome = world.getBiome(bp);
        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.MAGICAL) || biome instanceof BiomeForest) {
        	WoodType type = WoodType.BLUE;
        	try{
        		type = WoodType.byMetadata(MathHelper.getInt(random, 0, WoodType.values().length-1));
        	} catch(Exception e){}
        	int size = MathHelper.getInt(random, 4, 6);
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
		            
		            Biome biome = world.getBiome(pos);
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
    
    public boolean generateClusters(Random random, int chunkX, int chunkZ, World world, boolean newGen) {
    	if(newGen || Config.retrogenClusters){
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
    	                        return true;
    	                    }
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
		if((!event.getData().getCompoundTag(NBT_RETRO).hasKey(Config.retrogenID)) && (Config.retrogenOres || Config.retrogenTrees || Config.retrogenClusters || Config.retrogenReeds))
		{
			if(Config.retrogenInfo)
				ModLogger.info("Chunk "+event.getChunk().getPos()+" has been flagged for RetroGen by CM.");
			WorldTickHandler.retrogenChunks.put(dimension, event.getChunk().getPos());
		}
    }

}
