package alec_wam.CrystalMod.util;

import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import alec_wam.CrystalMod.CrystalMod;

public class EntityUtil {

	public static Vec3d getEyePosition(EntityPlayer player) {
	    double y = player.posY;
	    y += player.getEyeHeight();
	    return new Vec3d(player.posX, y, player.posZ);
	}

	public static Vector3d getEyePositionCM(EntityPlayer player) {
	    Vector3d res = new Vector3d(player.posX, player.posY, player.posZ);
	    res.y += player.getEyeHeight();
	    return res;
	}

	public static Vector3d getLookVec(EntityPlayer player) {
	    Vec3d lv = player.getLookVec();
	    return new Vector3d(lv.xCoord, lv.yCoord, lv.zCoord);
	}
	
	public static RayTraceResult getEntityLookedObject(EntityLivingBase entity){
        return getEntityLookedObject(entity, (float)(4.5));
    }

    public static RayTraceResult getEntityLookedObject(EntityLivingBase entity, float maxDistance){
        Pair<Vec3d, Vec3d> vecs = getStartAndEndLookVec(entity, maxDistance);
        return entity.worldObj.rayTraceBlocks(vecs.getLeft(), vecs.getRight());
    }

    public static Pair<Vec3d, Vec3d> getStartAndEndLookVec(EntityLivingBase entity){
        return getStartAndEndLookVec(entity, (float)(4.5));
    }

    public static Pair<Vec3d, Vec3d> getStartAndEndLookVec(EntityLivingBase entity, float maxDistance){
        Vec3d entityVec = null;
        if(entity instanceof EntityPlayer) {
            entityVec = getEyePosition((EntityPlayer) entity);
        } else {
            entityVec = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight() - entity.getYOffset() - (entity.isSneaking() ? 0.08 : 0), entity.posZ);
        }
        Vec3d entityLookVec = entity.getLook(1.0F);
        Vec3d maxDistVec = entityVec.addVector(entityLookVec.xCoord * maxDistance, entityLookVec.yCoord * maxDistance, entityLookVec.zCoord * maxDistance);
        return new ImmutablePair<Vec3d, Vec3d>(entityVec, maxDistVec);
    }
	
    public static BlockPos getEntityLookedBlock(EntityLivingBase entity, float maxDistance){
        RayTraceResult hit = getEntityLookedObject(entity, maxDistance);
        if(hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK) {
            return null;
        }
        return hit.getBlockPos();
    }
	


	public static double getDistanceToEntity(Entity entity1, Entity entity2)
	{
		return getDistanceToXYZ(entity1.posX, entity1.posY, entity1.posZ, entity2.posX, entity2.posY, entity2.posZ);
	}

	public static double getDistanceToXYZ(double x1, double y1, double z1, double x2, double y2, double z2)
	{
		final double deltaX = x2 - x1;
		final double deltaY = y2 - y1;
		final double deltaZ = z2 - z1;

		return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
	}

	/**
	 * Gets a list containing instances of all entities around the specified coordinates up to the specified distance away.
	 * 
	 * @param worldObj The world that the entity should be in.
	 * @param posX The X position to begin searching at.
	 * @param posY The Y position to begin searching at.
	 * @param posZ The Z position to begin searching at.
	 * @param maxDistanceAway The maximum distance away from the points to search.
	 * @return List containing all entities within the specified distance of the specified entity.
	 */
	public static List<Entity> getAllEntitiesWithinDistanceOfCoordinates(World worldObj, double posX, double posY, double posZ, int maxDistanceAway)
	{
		final List<Entity> entitiesAroundMe = worldObj.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(posX - maxDistanceAway, posY - maxDistanceAway, posZ - maxDistanceAway, posX + maxDistanceAway, posY + maxDistanceAway, posZ + maxDistanceAway));
		return entitiesAroundMe;
	}
    
	public static List<EntityLivingBase> attackEntitiesInArea(World world, List<EntityLivingBase> targets, DamageSource damageSource, float damage, boolean attackMobs){
		List<EntityLivingBase> attacked = Lists.newArrayList();
		for(Object obj : targets){
        	if(obj instanceof EntityLivingBase){
        		EntityLivingBase atEntity = (EntityLivingBase) obj;
        		
        		boolean isMob = attackMobs;
        		
        		if(atEntity instanceof IMob){
        			if(!isMob)continue;
        		} else if(isMob) {
        			continue;
        		}
        		
        		if(damageSource == null || atEntity.attackEntityFrom(damageSource, damage)){
        			attacked.add(atEntity);
        		}
        	}
        }
		return attacked;
	}
	
	public static void setCustomEntityData(Entity entity, NBTTagCompound nbt){
		if(entity == null)return;
		NBTTagCompound entityNBT = entity.getEntityData();
		entityNBT.setTag(CrystalMod.MODID.toLowerCase(), nbt);
	}
	
	public static boolean hasCustomData(Entity entity){
		return entity != null && entity.getEntityData().hasKey(CrystalMod.MODID.toLowerCase()) && entity.getEntityData().getTag(CrystalMod.MODID.toLowerCase()) instanceof NBTTagCompound;
	}
	
	public static NBTTagCompound getCustomEntityData(Entity entity){
		if (hasCustomData(entity))
		{
			return entity.getEntityData().getCompoundTag(CrystalMod.MODID.toLowerCase());
		}
		
		return new NBTTagCompound();
	}
	
}
