package alec_wam.CrystalMod.util;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityUtil {

	public static Vec3d getEyePosition(LivingEntity entity) {
	   return entity.getEyePosition(1.0F);
	}

	/*public static Vector3d getLookVec(LivingEntity entity) {
	    Vec3d lv = entity.getLookVec();
	    return new Vector3d(lv.xCoord, lv.yCoord, lv.zCoord);
	}*/
	
	public static double getReachDistance(LivingEntity entity){
		return (entity instanceof PlayerEntity) ? ((PlayerEntity)entity).getAttribute(PlayerEntity.REACH_DISTANCE).getValue() : 4.5;
	}
	
	public static BlockRayTraceResult getLookedObject(LivingEntity entity){
        return getEntityLookedObject(entity, (float)(getReachDistance(entity)));
    }

    public static BlockRayTraceResult getEntityLookedObject(LivingEntity entity, float maxDistance){
        Pair<Vec3d, Vec3d> vecs = getStartAndEndLookVec(entity, maxDistance);
        return entity.getEntityWorld().rayTraceBlocks(new RayTraceContext(vecs.getLeft(), vecs.getRight(), RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, entity));
    }

    public static Pair<Vec3d, Vec3d> getStartAndEndLookVec(LivingEntity entity){
        return getStartAndEndLookVec(entity, (float)(4.5));
    }

    public static Pair<Vec3d, Vec3d> getStartAndEndLookVec(LivingEntity entity, float maxDistance){
        Vec3d entityVec = null;
        if(entity instanceof PlayerEntity) {
            entityVec = getEyePosition((PlayerEntity) entity);
        } else {
            entityVec = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight() - entity.getYOffset() - (entity.isSneaking() ? 0.08 : 0), entity.posZ);
        }
        Vec3d entityLookVec = entity.getLook(1.0F);
        Vec3d maxDistVec = entityVec.add(entityLookVec.x * maxDistance, entityLookVec.y * maxDistance, entityLookVec.z * maxDistance);
        return new ImmutablePair<Vec3d, Vec3d>(entityVec, maxDistVec);
    }
	
    public static BlockPos getEntityLookedBlock(LivingEntity entity, float maxDistance){
        RayTraceResult hit = getEntityLookedObject(entity, maxDistance);
        if(hit == null || hit.getType() != RayTraceResult.Type.BLOCK) {
            return BlockPos.ZERO;
        }
        BlockRayTraceResult blockraytrace = (BlockRayTraceResult)hit;
        return blockraytrace.getPos();
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
    
	public static List<LivingEntity> attackEntitiesInArea(World world, List<LivingEntity> targets, DamageSource damageSource, float damage, boolean attackMobs){
		List<LivingEntity> attacked = Lists.newArrayList();
		for(Object obj : targets){
        	if(obj instanceof LivingEntity){
        		LivingEntity atEntity = (LivingEntity) obj;
        		
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
	
	public static void setCustomEntityData(Entity entity, CompoundNBT nbt){
		if(entity == null)return;
		CompoundNBT entityNBT = entity.getEntityData();
		entityNBT.put(CrystalMod.MODID.toLowerCase(), nbt);
	}
	
	public static boolean hasCustomData(Entity entity){
		return entity != null && entity.getEntityData().contains(CrystalMod.MODID.toLowerCase()) && entity.getEntityData().get(CrystalMod.MODID.toLowerCase()) instanceof CompoundNBT;
	}
	
	public static CompoundNBT getCustomEntityData(Entity entity){
		if (hasCustomData(entity))
		{
			return entity.getEntityData().getCompound(CrystalMod.MODID.toLowerCase());
		}
		
		return new CompoundNBT();
	}

	public static final Random rand = new Random();
	
	public static boolean randomTeleport(Entity entityIn, double range) {
		double d0 = entityIn.posX + (rand.nextDouble() - 0.5D) * range;
        double d1 = entityIn.posY + (rand.nextInt((int)range) - ((int)range)/2);
        double d2 = entityIn.posZ + (rand.nextDouble() - 0.5D) * range;
        return teleportTo(entityIn, d0, d1, d2);
	}
	
	public static boolean teleportTo(Entity entity, double x, double y, double z)
    {
		double realX = x;
		double realY = y;
		double realZ = z;
        if(entity instanceof LivingEntity){
        	net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent((LivingEntity) entity, x, y, z, 0);
        	if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return false;
        	realX = event.getTargetX();
        	realY = event.getTargetY();
        	realZ = event.getTargetZ();
        }
        boolean flag = attemptTeleport(entity, realX, realY, realZ);

        if (flag)
        {
            entity.getEntityWorld().playSound((PlayerEntity)null, entity.prevPosX, entity.prevPosY, entity.prevPosZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, entity.getSoundCategory(), 1.0F, 1.0F);
            entity.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
        return flag;
    }
	
	@SuppressWarnings("deprecation")
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
        World world = entity.getEntityWorld();
        //Random random = entity instanceof LivingEntity ? ((LivingEntity)entity).getRNG() : rand;

        if (world.isBlockLoaded(blockpos))
        {
            boolean flag1 = false;

            while (!flag1 && blockpos.getY() > 0)
            {
                BlockPos blockpos1 = blockpos.down();
                BlockState iblockstate = world.getBlockState(blockpos1);

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
            	//TODO Fix this
                /*if (world.getCollisionBoxes(entity, entity.getBoundingBox(), entity.posX, entity.posY, entity.posZ).isEmpty() && !world.containsAnyLiquid(entity.getBoundingBox()))
                {
                    flag = true;
                }*/
            }
        }

        if (!flag)
        {
        	entity.setPositionAndUpdate(d0, d1, d2);
            return false;
        }
        else
        {
            for (int j = 0; j < 128; ++j)
            {
                /*double d6 = j / 127.0D;
                float f = (random.nextFloat() - 0.5F) * 0.2F;
                float f1 = (random.nextFloat() - 0.5F) * 0.2F;
                float f2 = (random.nextFloat() - 0.5F) * 0.2F;
                double d3 = d0 + (entity.posX - d0) * d6 + (random.nextDouble() - 0.5D) * entity.width * 2.0D;
                double d4 = d1 + (entity.posY - d1) * d6 + random.nextDouble() * entity.height;
                double d5 = d2 + (entity.posZ - d2) * d6 + (random.nextDouble() - 0.5D) * entity.width * 2.0D;*/
                //world.spawnParticle(EnumParticleTypes.PORTAL, d3, d4, d5, f, f1, f2, new int[0]);
            }

            if (entity instanceof CreatureEntity)
            {
                ((CreatureEntity)entity).getNavigator().clearPath();
            }

            return true;
        }
    }

	public static ItemStack getItemFromEntity(Entity entity, RayTraceResult ray) {
		ItemStack stack = ItemStackTools.getEmptyStack();
		if(entity !=null){
			if(entity instanceof ItemEntity){
				stack = ((ItemEntity)entity).getItem();
			}
			if(stack == null)stack = entity.getPickedResult(ray);
		}
		return ItemStackTools.safeCopy(stack);
	}
	
	public static EntityRayTraceResult getRayTraceEntity(PlayerEntity player, double range, boolean ignoreCanBeCollidedWith) {
		Vec3d eye = EntityUtil.getEyePosition(player);
		Vec3d look = player.getLook(1.0f);

		return getRayTraceEntity(player, eye, look, range, ignoreCanBeCollidedWith);
	}
	
	
	public static EntityRayTraceResult getRayTraceEntity(Entity entity, Vec3d start, Vec3d look, double range, boolean ignoreCanBeCollidedWith) {
		Vec3d direction = start.add(look.x * range, look.y * range, look.z * range);

		Entity pointedEntity = null;
		Vec3d hit = null;
		AxisAlignedBB bb = entity.getBoundingBox().grow(look.x * range, look.y * range, look.z * range).expand(1, 1, 1);
		List<Entity> entitiesInArea = entity.getEntityWorld().getEntitiesWithinAABBExcludingEntity(entity, bb);
		double range2 = range; // range to the current candidate. Used to find the closest entity.

		for(Entity candidate : entitiesInArea) {
			if(ignoreCanBeCollidedWith || candidate.canBeCollidedWith()) {
				// does our vector go through the entity?
				double colBorder = candidate.getCollisionBorderSize();
				AxisAlignedBB entityBB = candidate.getBoundingBox().expand(colBorder, colBorder, colBorder);
				Optional<Vec3d> movingobjectposition = entityBB.rayTrace(start, direction);

				// needs special casing: vector starts inside the entity
				if(entityBB.contains(start)) {
					if(0.0D < range2 || range2 == 0.0D) {
						pointedEntity = candidate;
						hit = movingobjectposition.orElse(start);
						range2 = 0.0D;
					}
				}
				else if(movingobjectposition.isPresent()) {
					Vec3d hitVec = movingobjectposition.orElse(Vec3d.ZERO);
					double dist = start.distanceTo(hitVec);

					if(dist < range2 || range2 == 0.0D) {
						if(candidate == entity.getRidingEntity() && !entity.canRiderInteract()) {
							if(range2 == 0.0D) {
								pointedEntity = candidate;
								hit = hitVec;
							}
						}
						else {
							pointedEntity = candidate;
							hit = hitVec;
							range2 = dist;
						}
					}
				}
			}
		}

		if(pointedEntity != null && range2 < range) {
			return new EntityRayTraceResult(pointedEntity, hit);
		}
		return null;
	}
	
}
