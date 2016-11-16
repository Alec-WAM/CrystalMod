package alec_wam.CrystalMod.util;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
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

	public static final Random rand = new Random();
	
	public static boolean randomTeleport(Entity entityIn, double range) {
		double d0 = entityIn.posX + (rand.nextDouble() - 0.5D) * range;
        double d1 = entityIn.posY + (double)(rand.nextInt((int)range) - ((int)range)/2);
        double d2 = entityIn.posZ + (rand.nextDouble() - 0.5D) * range;
        return teleportTo(entityIn, d0, d1, d2);
	}
	
	public static boolean teleportTo(Entity entity, double x, double y, double z)
    {
		double realX = x;
		double realY = y;
		double realZ = z;
        if(entity instanceof EntityLivingBase){
        	net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent((EntityLivingBase) entity, x, y, z, 0);
        	if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return false;
        	realX = event.getTargetX();
        	realY = event.getTargetY();
        	realZ = event.getTargetZ();
        }
        boolean flag = attemptTeleport(entity, realX, realY, realZ);

        if (flag)
        {
            entity.worldObj.playSound((EntityPlayer)null, entity.prevPosX, entity.prevPosY, entity.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, entity.getSoundCategory(), 1.0F, 1.0F);
            entity.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
        }
        return flag;
    }
	
	public static boolean attemptTeleport(Entity entity, double x, double y, double z)
    {
        double d0 = entity.posX;
        double d1 = entity.posY;
        double d2 = entity.posZ;
        entity.posX = x;
        entity.posY = y;
        entity.posZ = z;
        boolean flag = false;
        BlockPos blockpos = new BlockPos(entity);
        World world = entity.worldObj;
        Random random = entity instanceof EntityLivingBase ? ((EntityLivingBase)entity).getRNG() : rand;

        if (world.isBlockLoaded(blockpos))
        {
            boolean flag1 = false;

            while (!flag1 && blockpos.getY() > 0)
            {
                BlockPos blockpos1 = blockpos.down();
                IBlockState iblockstate = world.getBlockState(blockpos1);

                if (iblockstate.getMaterial().blocksMovement())
                {
                    flag1 = true;
                }
                else
                {
                    --entity.posY;
                    blockpos = blockpos1;
                }
            }

            if (flag1)
            {
            	entity.setPositionAndUpdate(entity.posX, entity.posY, entity.posZ);

                if (world.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty() && !world.containsAnyLiquid(entity.getEntityBoundingBox()))
                {
                    flag = true;
                }
            }
        }

        if (!flag)
        {
        	entity.setPositionAndUpdate(d0, d1, d2);
            return false;
        }
        else
        {
            int i = 128;

            for (int j = 0; j < 128; ++j)
            {
                double d6 = (double)j / 127.0D;
                float f = (random.nextFloat() - 0.5F) * 0.2F;
                float f1 = (random.nextFloat() - 0.5F) * 0.2F;
                float f2 = (random.nextFloat() - 0.5F) * 0.2F;
                double d3 = d0 + (entity.posX - d0) * d6 + (random.nextDouble() - 0.5D) * (double)entity.width * 2.0D;
                double d4 = d1 + (entity.posY - d1) * d6 + random.nextDouble() * (double)entity.height;
                double d5 = d2 + (entity.posZ - d2) * d6 + (random.nextDouble() - 0.5D) * (double)entity.width * 2.0D;
                world.spawnParticle(EnumParticleTypes.PORTAL, d3, d4, d5, (double)f, (double)f1, (double)f2, new int[0]);
            }

            if (entity instanceof EntityCreature)
            {
                ((EntityCreature)entity).getNavigator().clearPathEntity();
            }

            return true;
        }
    }
	
}
