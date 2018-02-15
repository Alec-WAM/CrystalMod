package alec_wam.CrystalMod.world;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.api.world.IGenerationFeature;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.world.crystex.MapGenCrystexiumSpike;
import alec_wam.CrystalMod.world.generation.CoralReefFeature;
import alec_wam.CrystalMod.world.generation.CrysineMushroomFeature;
import alec_wam.CrystalMod.world.generation.CrystalBushFeature;
import alec_wam.CrystalMod.world.generation.CrystalClusterFeature;
import alec_wam.CrystalMod.world.generation.CrystalOreFeature;
import alec_wam.CrystalMod.world.generation.CrystalReedsFeature;
import alec_wam.CrystalMod.world.generation.CrystalTreeFeature;
import alec_wam.CrystalMod.world.generation.CrystalWellFeature;
import alec_wam.CrystalMod.world.generation.FusionTempleFeature;
import alec_wam.CrystalMod.world.generation.KelpFeature;
import alec_wam.CrystalMod.world.generation.RoseBushFeature;
import alec_wam.CrystalMod.world.generation.SeaweedFeature;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CrystalModWorldGenerator implements IWorldGenerator {	
    public static CrystalModWorldGenerator instance = new CrystalModWorldGenerator();
	public static List<IGenerationFeature> featureList = Lists.newArrayList();

    public static MapGenCrystexiumSpike crystexiumSpikeGen = new MapGenCrystexiumSpike();
    static {
    	featureList.add(new CrystalOreFeature());
    	featureList.add(new CrystalTreeFeature());
    	featureList.add(new CrystalReedsFeature());
    	featureList.add(new CrystalClusterFeature());  
    	featureList.add(new CrystalBushFeature());    
    	featureList.add(new SeaweedFeature());      	 
    	featureList.add(new KelpFeature());   	 
    	featureList.add(new CoralReefFeature()); 
    	featureList.add(new RoseBushFeature()); 
    	featureList.add(new CrysineMushroomFeature()); 
    	
    	//TODO Generate Crystherium
    	
    	featureList.add(new FusionTempleFeature());
    	featureList.add(new CrystalWellFeature());
    }
    
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        for(IGenerationFeature feature : featureList){
        	if (feature.generateFeature(world, random, chunkX, chunkZ, true)) {
                world.getChunkFromChunkCoords(chunkX, chunkZ).setChunkModified();
            }
        }
        boolean genSpikes = false;
        if(world.provider.getDimension() == 0 && genSpikes){
        	crystexiumSpikeGen.generate(world, chunkX, chunkZ, null);
        	crystexiumSpikeGen.generateStructure(world, random, new ChunkPos(chunkX, chunkZ));
        }        
    }
    
    public static String NBT_RETRO = "CrystalModGen";
    
    //Credit: https://github.com/BluSunrize/ImmersiveEngineering/blob/master/src/main/java/blusunrize/immersiveengineering/common/world/IEWorldGen.java
    
    @SubscribeEvent
    public void handleChunkSaveEvent(ChunkDataEvent.Save event) {
    	if(areAnyRetroGensEnabled()){
	    	NBTTagCompound nbt = new NBTTagCompound();
			event.getData().setTag(NBT_RETRO, nbt);
			nbt.setBoolean(Config.retrogenID, true);
    	}
    }

    public static boolean areAnyRetroGensEnabled(){
    	for(IGenerationFeature feature : featureList){
    		if(feature.isRetroGenAllowed()){
    			return true;
    		}
    	}
    	return false;
    }
    
    @SubscribeEvent
    public void handleChunkLoadEvent(ChunkDataEvent.Load event) {
        
    	int dimension = event.getWorld().provider.getDimension();
		if((!event.getData().getCompoundTag(NBT_RETRO).hasKey(Config.retrogenID)) && (areAnyRetroGensEnabled()))
		{
			if(Config.retrogenInfo)
				ModLogger.info("Chunk "+event.getChunk().getPos()+" has been flagged for RetroGen by CM.");
			WorldTickHandler.retrogenChunks.put(dimension, event.getChunk().getPos());
		}
    }

}
