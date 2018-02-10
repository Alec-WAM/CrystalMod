package alec_wam.CrystalMod.util;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class EntityUtil {

	public static Vec3d getEyePosition(EntityLivingBase entity) {
	   return entity.getPositionEyes(1.0F);
	}

	public static Vector3d getEyePositionCM(EntityLivingBase entity) {
	    Vector3d res = new Vector3d(entity.posX, entity.posY, entity.posZ);
	    res.y += entity.getEyeHeight();
	    return res;
	}

	public static Vector3d getLookVec(EntityLivingBase entity) {
	    Vec3d lv = entity.getLookVec();
	    return new Vector3d(lv.xCoord, lv.yCoord, lv.zCoord);
	}
	
	public static double getReachDistance(EntityLivingBase entity){
		return (entity instanceof EntityPlayer) ? CrystalMod.proxy.getReachDistanceForPlayer((EntityPlayer)entity) : 4.5;
	}
	
	public static RayTraceResult getPlayerLookedObject(EntityPlayer entity){
        return getEntityLookedObject(entity, (float)(CrystalMod.proxy.getReachDistanceForPlayer(entity)));
    }
	
	public static RayTraceResult getEntityLookedObject(EntityLivingBase entity){
        return getEntityLookedObject(entity, (float)(4.5));
    }

    public static RayTraceResult getEntityLookedObject(EntityLivingBase entity, float maxDistance){
        Pair<Vec3d, Vec3d> vecs = getStartAndEndLookVec(entity, maxDistance);
        return entity.getEntityWorld().rayTraceBlocks(vecs.getLeft(), vecs.getRight());
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
	
	public static void sendSyncPacket(Entity entity){
		if(entity == null || entity.getEntityWorld() == null || entity.getEntityWorld().isRemote)return;
		CrystalModNetwork.sendToAllAround(new PacketEntityMessage(entity, "CustomDataSync", getCustomEntityData(entity)), entity);
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
            entity.getEntityWorld().playSound((EntityPlayer)null, entity.prevPosX, entity.prevPosY, entity.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, entity.getSoundCategory(), 1.0F, 1.0F);
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
        World world = entity.getEntityWorld();
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
            for (int j = 0; j < 128; ++j)
            {
                double d6 = j / 127.0D;
                float f = (random.nextFloat() - 0.5F) * 0.2F;
                float f1 = (random.nextFloat() - 0.5F) * 0.2F;
                float f2 = (random.nextFloat() - 0.5F) * 0.2F;
                double d3 = d0 + (entity.posX - d0) * d6 + (random.nextDouble() - 0.5D) * entity.width * 2.0D;
                double d4 = d1 + (entity.posY - d1) * d6 + random.nextDouble() * entity.height;
                double d5 = d2 + (entity.posZ - d2) * d6 + (random.nextDouble() - 0.5D) * entity.width * 2.0D;
                world.spawnParticle(EnumParticleTypes.PORTAL, d3, d4, d5, f, f1, f2, new int[0]);
            }

            if (entity instanceof EntityCreature)
            {
                ((EntityCreature)entity).getNavigator().clearPathEntity();
            }

            return true;
        }
    }

	public static ItemStack getItemFromEntity(Entity entity, RayTraceResult ray) {
		ItemStack stack = ItemStackTools.getEmptyStack();
		if(entity !=null){
			if(entity instanceof EntityItem){
				stack = ((EntityItem)entity).getEntityItem();
			}
			if(stack == null)stack = entity.getPickedResult(ray);
		}
		return ItemStackTools.safeCopy(stack);
	}

	/*public static RayTraceResult getRayTraceEntity(World world, EntityPlayer living, double maxrange, double range) {
		RayTraceResult rayTrace = null;
        double d0 = (double)range;
        rayTrace = living.rayTrace(d0, 1.0F);
        Vec3d vec3d = EntityUtil.getEyePosition(living);
        boolean flag = false;
        int i = 3;
        double d1 = d0;

        if (d0 > maxrange)
        {
        	flag = true;
        }

        if (rayTrace != null)
        {
            d1 = rayTrace.hitVec.distanceTo(vec3d);
        }
        Entity pointedEntity = null;
        Vec3d vec3d1 = living.getLook(1.0F);
        Vec3d vec3d2 = vec3d.addVector(vec3d1.xCoord * d0, vec3d1.yCoord * d0, vec3d1.zCoord * d0);
        Vec3d vec3d3 = null;
        float f = 1.0F;
        List<Entity> list = world.getEntitiesInAABBexcluding(living, living.getEntityBoundingBox().addCoord(vec3d1.xCoord * d0, vec3d1.yCoord * d0, vec3d1.zCoord * d0).expand(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
        {
            public boolean apply(@Nullable Entity p_apply_1_)
            {
                return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
            }
        }));
        double d2 = d1;

        for (int j = 0; j < list.size(); ++j)
        {
            Entity entity1 = (Entity)list.get(j);
            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expandXyz((double)entity1.getCollisionBorderSize());
            RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);

            if (axisalignedbb.isVecInside(vec3d))
            {
                if (d2 >= 0.0D)
                {
                    pointedEntity = entity1;
                    vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
                    d2 = 0.0D;
                }
            }
            else if (raytraceresult != null)
            {
                double d3 = vec3d.distanceTo(raytraceresult.hitVec);

                if (d3 < d2 || d2 == 0.0D)
                {
                    if (entity1.getLowestRidingEntity() == living.getLowestRidingEntity() && !living.canRiderInteract())
                    {
                        if (d2 == 0.0D)
                        {
                            pointedEntity = entity1;
                            vec3d3 = raytraceresult.hitVec;
                        }
                    }
                    else
                    {
                        pointedEntity = entity1;
                        vec3d3 = raytraceresult.hitVec;
                        d2 = d3;
                    }
                }
            }
        }

        if (pointedEntity != null && flag && vec3d.distanceTo(vec3d3) > maxrange)
        {
            pointedEntity = null;
            rayTrace = new RayTraceResult(RayTraceResult.Type.MISS, vec3d3, (EnumFacing)null, new BlockPos(vec3d3));
        }

        if (pointedEntity != null && (d2 < d1 || rayTrace == null))
        {
            rayTrace = new RayTraceResult(pointedEntity, vec3d3);
        }
        
        return rayTrace;
	}*/
	
	public static RayTraceResult getRayTraceEntity(EntityPlayer player, double range, boolean ignoreCanBeCollidedWith) {
		Vec3d eye = EntityUtil.getEyePosition(player);
		Vec3d look = player.getLook(1.0f);

		return getRayTraceEntity(player, eye, look, range, ignoreCanBeCollidedWith);
	}
	
	
	public static RayTraceResult getRayTraceEntity(Entity entity, Vec3d start, Vec3d look, double range, boolean ignoreCanBeCollidedWith) {
		Vec3d direction = start.addVector(look.xCoord * range, look.yCoord * range, look.zCoord * range);

		Entity pointedEntity = null;
		Vec3d hit = null;
		AxisAlignedBB bb = entity.getEntityBoundingBox().addCoord(look.xCoord * range, look.yCoord * range, look.zCoord * range).expand(1, 1, 1);
		List<Entity> entitiesInArea = entity.getEntityWorld().getEntitiesWithinAABBExcludingEntity(entity, bb);
		double range2 = range; // range to the current candidate. Used to find the closest entity.

		for(Entity candidate : entitiesInArea) {
			if(ignoreCanBeCollidedWith || candidate.canBeCollidedWith()) {
				// does our vector go through the entity?
				double colBorder = candidate.getCollisionBorderSize();
				AxisAlignedBB entityBB = candidate.getEntityBoundingBox().expand(colBorder, colBorder, colBorder);
				RayTraceResult movingobjectposition = entityBB.calculateIntercept(start, direction);

				// needs special casing: vector starts inside the entity
				if(entityBB.isVecInside(start)) {
					if(0.0D < range2 || range2 == 0.0D) {
						pointedEntity = candidate;
						hit = movingobjectposition == null ? start : movingobjectposition.hitVec;
						range2 = 0.0D;
					}
				}
				else if(movingobjectposition != null) {
					double dist = start.distanceTo(movingobjectposition.hitVec);

					if(dist < range2 || range2 == 0.0D) {
						if(candidate == entity.getRidingEntity() && !entity.canRiderInteract()) {
							if(range2 == 0.0D) {
								pointedEntity = candidate;
								hit = movingobjectposition.hitVec;
							}
						}
						else {
							pointedEntity = candidate;
							hit = movingobjectposition.hitVec;
							range2 = dist;
						}
					}
				}
			}
		}

		if(pointedEntity != null && range2 < range) {
			return new RayTraceResult(pointedEntity, hit);
		}
		return null;
	}

	public static void setSize(EntityPlayer player, float width, float height, float stepSize, float eyeHeight, boolean sendPacket) {
		setEntitySize(player, width, height);
		player.stepHeight = stepSize;
		player.eyeHeight = eyeHeight;

		if(sendPacket){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setFloat("Width", width);
			nbt.setFloat("Height", height);
			nbt.setFloat("StepSize", stepSize);
			nbt.setFloat("EyeHeight", eyeHeight);
			PacketEntityMessage message = new PacketEntityMessage(player, "SetSize", nbt);
			CrystalModNetwork.sendTo(message, (EntityPlayerMP)player);
			CrystalModNetwork.sendToAll(message);
		}
	}
	
	private static Method methodEntitySetSize;
	@SuppressWarnings("deprecation")
	public static void setEntitySize(Entity entity, float width, float height)
	{
		try
		{
			if (methodEntitySetSize == null) {
				methodEntitySetSize = ReflectionHelper.findMethod(Entity.class, entity, new String[] { "setSize", "func_177725_a"}, new Class[] { Float.TYPE, Float.TYPE });
			}
			if (methodEntitySetSize != null)methodEntitySetSize.invoke(entity, new Object[] { Float.valueOf(width), Float.valueOf(height) });
		}
		catch (Exception ex) {ex.printStackTrace();}
	}

	/**Returns if an arrow was infinite or not**/
	public static boolean shootArrow(World world, EntityPlayer entityplayer, ItemStack bow, ItemStack itemstack, int charge) {
		boolean flag = entityplayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, bow) > 0;
        int i = charge;

        if (ItemStackTools.isValid(itemstack) || flag)
        {
            if (ItemStackTools.isEmpty(itemstack))
            {
                itemstack = new ItemStack(Items.ARROW);
            }

            float f = ItemBow.getArrowVelocity(i);

            if (f >= 0.1D)
            {
                boolean flag1 = entityplayer.capabilities.isCreativeMode || (itemstack.getItem() instanceof ItemArrow ? ((ItemArrow)itemstack.getItem()).isInfinite(itemstack, bow, entityplayer) : false);

                if (!world.isRemote)
                {
                    ItemArrow itemarrow = ((ItemArrow)(itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW));
                    EntityArrow entityarrow = itemarrow.createArrow(world, itemstack, entityplayer);
                    entityarrow.setAim(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, f * 3.0F, 1.0F);

                    if (f == 1.0F)
                    {
                        entityarrow.setIsCritical(true);
                    }

                    int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, bow);

                    if (j > 0)
                    {
                        entityarrow.setDamage(entityarrow.getDamage() + j * 0.5D + 0.5D);
                    }

                    int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, bow);

                    if (k > 0)
                    {
                        entityarrow.setKnockbackStrength(k);
                    }

                    if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, bow) > 0)
                    {
                        entityarrow.setFire(100);
                    }

                    bow.damageItem(1, entityplayer);

                    if (flag1)
                    {
                        entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                    }

                    world.spawnEntity(entityarrow);
                }

                world.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

                entityplayer.addStat(StatList.getObjectUseStats(Items.BOW));
                return flag1;
            }
        }
        return false;
    }
	
	public static boolean isSneakSuccessful(final EntityLivingBase sneaker, final EntityLivingBase target) {
        double sneakerFacing = (sneaker.rotationYaw + 90.0f) % 360.0f;
        if (sneakerFacing < 0.0) {
            sneakerFacing += 360.0;
        }
        double targetFacing = (target.rotationYaw + 90.0f) % 360.0f;
        if (targetFacing < 0.0) {
            targetFacing += 360.0;
        }
        final double diff = Math.abs(targetFacing - sneakerFacing);
        double chance = 0.0;
        if (360.0 - diff % 360.0 < 45.0 || diff % 360.0 < 45.0) {
            chance = (sneaker.isSneaking() ? 0.6 : 0.3);
        }
        else {
            chance = (sneaker.isSneaking() ? 0.1 : 0.01);
            if (sneaker.isSilent()) {
                chance += 0.1;
            }
        }
        return sneaker.getEntityWorld().rand.nextDouble() < chance;
    }
	
}
