package alec_wam.CrystalMod.world;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ArrayListMultimap;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.api.world.IGenerationFeature;
import alec_wam.CrystalMod.util.ModLogger;
import gnu.trove.set.hash.THashSet;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class WorldTickHandler {

    public static WorldTickHandler instance = new WorldTickHandler();
    
    public static ArrayListMultimap<Integer, ChunkPos> retrogenChunks = ArrayListMultimap.create();

    @SubscribeEvent
    public void tickEnd(TickEvent.WorldTickEvent event) {
    	if(event.side==Side.CLIENT || event.phase==TickEvent.Phase.START)
			return;
        World world = event.world;
        int dim = world.provider.getDimension();

        List<ChunkPos> chunks = retrogenChunks.get(dim);

        if (chunks != null && !chunks.isEmpty()) {
        	for(int i=0; i<2; i++)
			{
        		chunks = retrogenChunks.get(dim);
        		if(chunks == null || chunks.size()<= 0)
					break;
	            ChunkPos loc = chunks.get(0);
	            long worldSeed = world.getSeed();
	            Random rand = new Random(worldSeed);
	            long xSeed = rand.nextLong() >> 2 + 1L;
	            long zSeed = rand.nextLong() >> 2 + 1L;
	            rand.setSeed(xSeed * loc.chunkXPos + zSeed * loc.chunkZPos ^ worldSeed);
	            
	            for(IGenerationFeature feature : CrystalModWorldGenerator.featureList){
	            	if (feature.isRetroGenAllowed() && feature.generateFeature(world, rand, loc.chunkXPos, loc.chunkZPos, false)) {
	                    world.getChunkFromChunkCoords(loc.chunkXPos, loc.chunkZPos).setChunkModified();
	                }
	            }	 
	            
	            chunks.remove(0);
	            if(Config.retrogenInfo)
	            	ModLogger.info("Retrogen was performed on "+loc.toString()+", "+Math.max(0,chunks.size())+" chunks remaining (Dimension "+world.provider.getDimensionType().getName()+")");
			}
        }
    }

    
    @SubscribeEvent
	public void livingUpdate(LivingUpdateEvent event)
	{
		/*if (!event.entityLiving.worldObj.isRemote)
		{
			if (event.entityLiving instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer) event.entityLiving;
				if (player.dimension == ModDimensions.CUBE_ID)
				{
					CubeManager manager;

					if ((manager = CubeManager.getInstance()) != null)
					{
						manager.checkPosition((EntityPlayerMP) player);
					}
				}
			}
		}*/
	}
    
    public static class RetroChunkCoord {

        private static final THashSet<String> emptySet = new THashSet<String>(0);
        public final Pair<Integer,Integer> coord;
        public final THashSet<String> generatedFeatures;

        public RetroChunkCoord(Pair<Integer,Integer> pos, NBTTagList features) {

            coord = pos;
            if (features == null) {
                generatedFeatures = emptySet;
            } else {
                int i = 0;
                int e = features.tagCount();
                generatedFeatures = new THashSet<String>(e);
                for (; i < e; ++i) {
                    generatedFeatures.add(features.getStringTagAt(i));
                }
            }
        }
    }

}
