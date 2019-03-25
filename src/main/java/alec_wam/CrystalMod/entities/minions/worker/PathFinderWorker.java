package alec_wam.CrystalMod.entities.minions.worker;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;

public class PathFinderWorker {
	
	public static PathFinderCustom customInstance;


    public static PathFinderCustom getPathFinder()
    {
    	if(customInstance == null){
    		WalkNodeProcessor nodeProcessor = new WalkNodeProcessor();
	        nodeProcessor.setCanEnterDoors(true);
	        customInstance = new PathFinderCustom(nodeProcessor);
    	}
    	return customInstance;
    }
	
	public static Path findDetailedPath(EntityLiving entity, PathNavigate nav, double x, double y, double z){
		if (!(entity.onGround || entity.isRiding()))
        {
            return null;
        }
        else if (nav.getPath() != null && !nav.getPath().isFinished())
        {
            return nav.getPath();
        }
        else
        {
            float f = nav.getPathSearchRange();
            BlockPos blockpos = new BlockPos(entity);
            int i = (int)(f + 8.0F);
            ChunkCache chunkcache = new ChunkCache(entity.getEntityWorld(), blockpos.add(-i, -i, -i), blockpos.add(i, i, i), 0);
            Path path = getPathFinder().findPath(chunkcache, entity, x, y, z, f);
            return path;
        }
	}
	
}
