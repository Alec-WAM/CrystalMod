package alec_wam.CrystalMod.entities.pet.bombomb;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.entities.ai.AIBase;
import alec_wam.CrystalMod.entities.minions.MinionConstants;
import alec_wam.CrystalMod.entities.minions.warrior.EnumCombatBehaviors;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.util.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class BombombAICombat extends AIBase<EntityBombomb>
{
	private int attackMethodInt;
	private int attackTriggerInt;
	private int attackTargetInt;

	private EntityLivingBase attackTarget;
	public int maxFuseTime;
	public int fuseTime;
	public int lastFuseTime;
	
	public BombombAICombat() 
	{
		attackMethodInt = EnumCombatBehaviors.METHOD_MELEE_ONLY.getNumericId();
		attackTriggerInt = EnumCombatBehaviors.TRIGGER_PLAYER_TAKE_DAMAGE.getNumericId();
		attackTargetInt = EnumCombatBehaviors.TARGET_HOSTILE_MOBS.getNumericId();
	}

	@Override
	public void onUpdateCommon(EntityBombomb bombomb) 
	{
		
		
	}

	@Override
	public void onUpdateClient(EntityBombomb bombomb) 
	{	
	}

	@Override
	public void onUpdateServer(EntityBombomb bombomb) 
	{

		//Do nothing when we're sitting.
		if (bombomb.isSitting())
		{
			return;
		}
		
		//Cancel attack targets and stop when we're not supposed to fight.
		if (attackTarget != null && (/*getMethodBehavior() == EnumCombatBehaviors.METHOD_DO_NOT_FIGHT ||*/ !isEntityValidToAttack(bombomb, attackTarget)))
		{
			fuseTime = 0;
			maxFuseTime = 0;
			attackTarget = null;
			return;
		}

		//Also clear our attack target if it is dead.
		if (attackTarget != null && (attackTarget.isDead || attackTarget.getHealth() <= 0.0F || !attackTarget.isEntityAlive()))
		{
			fuseTime = 0;
			maxFuseTime = 0;
			attackTarget = null;
		}
		
		//Check if we should be searching for a target.
		if (attackTarget == null && getTriggerBehavior() == EnumCombatBehaviors.TRIGGER_ALWAYS)
		{
			findAttackTarget(bombomb);
		}

		if (attackTarget == null && getTriggerBehavior() == EnumCombatBehaviors.TRIGGER_PLAYER_DEAL_DAMAGE)
		{
			EntityLivingBase owner = bombomb.getOwner();
			if(owner !=null){
				
				EntityLivingBase target = owner.getLastAttacker();
				
				if (target !=null && isEntityValidToAttack(bombomb, target))
				{
					attackTarget = target;
				}
			}
		}
		
		if (attackTarget == null && getTriggerBehavior() == EnumCombatBehaviors.TRIGGER_PLAYER_TAKE_DAMAGE)
		{
			EntityLivingBase owner = bombomb.getOwner();
			if(owner !=null){
				
				EntityLivingBase target = owner.getAITarget();
				
				if (target !=null && isEntityValidToAttack(bombomb, target))
				{
					attackTarget = target;
				}
			}
		}
		
		if (attackTarget == null)
		{
			EntityLivingBase target = bombomb.getAITarget();
				
			if (target !=null && isEntityValidToAttack(bombomb, target))
			{
				attackTarget = target;
			}
		}
		
		//If we have a target, proceed to attack.
		else if (attackTarget != null)
		{
			double distanceToTarget = EntityUtil.getDistanceToEntity(bombomb, attackTarget);
			
			moveToAttackTarget(bombomb);
			
			if (distanceToTarget < 3.0F)
			{
				if(maxFuseTime <= 0)maxFuseTime = 40;
			}
		}
		
		if(maxFuseTime > 0 && fuseTime < maxFuseTime){
			lastFuseTime = fuseTime;
			fuseTime++;
			if(fuseTime == 1){
				bombomb.playSound(SoundEvents.ENTITY_TNT_PRIMED, 1.0f, 0.5f);
			}
			if(fuseTime >=maxFuseTime){
				fuseTime = maxFuseTime = 0;
				createExplosion(bombomb);
			}
		}
	}
	
	public void createExplosion(EntityBombomb bombomb){
		float explosionSize = 3.0f;
		Map<EntityPlayer, Vec3d> playerKnockbackMap = Maps.newHashMap();
		double explosionX = bombomb.posX;
		double explosionY = bombomb.posY;
		double explosionZ = bombomb.posZ;
		float f3 = explosionSize * 2.0F;
        int k1 = MathHelper.floor_double(explosionX - (double)f3 - 1.0D);
        int l1 = MathHelper.floor_double(explosionX + (double)f3 + 1.0D);
        int i2 = MathHelper.floor_double(explosionY - (double)f3 - 1.0D);
        int i1 = MathHelper.floor_double(explosionY + (double)f3 + 1.0D);
        int j2 = MathHelper.floor_double(explosionZ - (double)f3 - 1.0D);
        int j1 = MathHelper.floor_double(explosionZ + (double)f3 + 1.0D);
        List<Entity> list = bombomb.worldObj.getEntitiesWithinAABBExcludingEntity(bombomb, new AxisAlignedBB((double)k1, (double)i2, (double)j2, (double)l1, (double)i1, (double)j1));
        //net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.worldObj, this, list, f3);
        Vec3d vec3d = new Vec3d(explosionX, explosionY, explosionZ);

        for (int k2 = 0; k2 < list.size(); ++k2)
        {
            Entity entity = (Entity)list.get(k2);

            if (!entity.isImmuneToExplosions() && entity instanceof EntityLivingBase && isEntityValidToAttack(bombomb, (EntityLivingBase)entity))
            {
                double d12 = entity.getDistance(explosionX, explosionY, explosionZ) / (double)f3;

                if (d12 <= 1.0D)
                {
                    double d5 = entity.posX - explosionX;
                    double d7 = entity.posY + (double)entity.getEyeHeight() - explosionY;
                    double d9 = entity.posZ - explosionZ;
                    double d13 = (double)MathHelper.sqrt_double(d5 * d5 + d7 * d7 + d9 * d9);

                    if (d13 != 0.0D)
                    {
                        d5 = d5 / d13;
                        d7 = d7 / d13;
                        d9 = d9 / d13;
                        double d14 = (double)bombomb.worldObj.getBlockDensity(vec3d, entity.getEntityBoundingBox());
                        double d10 = (1.0D - d12) * d14;
                        entity.attackEntityFrom(DamageSource.causeExplosionDamage(bombomb), (float)((int)((d10 * d10 + d10) / 2.0D * 7.0D * (double)f3 + 1.0D)));
                        double d11 = 1.0D;

                        if (entity instanceof EntityLivingBase)
                        {
                            d11 = EnchantmentProtection.getBlastDamageReduction((EntityLivingBase)entity, d10);
                        }

                        entity.motionX += d5 * d11;
                        entity.motionY += d7 * d11;
                        entity.motionZ += d9 * d11;

                        if (entity instanceof EntityPlayer)
                        {
                            EntityPlayer entityplayer = (EntityPlayer)entity;

                            if (!entityplayer.isSpectator() && (!entityplayer.isCreative() || !entityplayer.capabilities.isFlying))
                            {
                                playerKnockbackMap.put(entityplayer, new Vec3d(d5 * d10, d7 * d10, d9 * d10));
                            }
                        }
                    }
                }
            }
        }
        
        for(EntityPlayer player : playerKnockbackMap.keySet()){
        	Vec3d vec = playerKnockbackMap.get(player);
        	double oldVelX = player.motionX;
            double oldVelY = player.motionY;
            double oldVelZ = player.motionZ;
        	player.addVelocity(vec.xCoord, vec.yCoord, vec.zCoord);
        	if(player instanceof EntityPlayerMP && player.velocityChanged){
        		if(player !=null && player instanceof EntityPlayerMP && ((EntityPlayerMP) player).connection != null) {
          	      ((EntityPlayerMP) player).connection.sendPacket(new SPacketEntityVelocity(player));
          	    }
        		player.velocityChanged = false;
        		player.motionX = oldVelX;
        		player.motionY = oldVelY;
        		player.motionZ = oldVelZ;
        	}
        }
        
        bombomb.worldObj.playSound((EntityPlayer)null, explosionX, explosionY, explosionZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (bombomb.worldObj.rand.nextFloat() - bombomb.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
        //bombomb.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D, new int[0]);

        if(!bombomb.worldObj.isRemote)
           CrystalModNetwork.sendToAllAround(new PacketEntityMessage(bombomb, "Explosion"), bombomb);
        
        /*if (explosionSize >= 2.0F)
        {
            bombomb.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D, new int[0]);
        }
        else
        {
        	bombomb.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D, new int[0]);
        }*/

        /*if (this.isSmoking)
        {
            for (BlockPos blockpos : this.affectedBlockPositions)
            {
                IBlockState iblockstate = this.worldObj.getBlockState(blockpos);
                Block block = iblockstate.getBlock();

                if (spawnParticles)
                {
                    double d0 = (double)((float)blockpos.getX() + this.worldObj.rand.nextFloat());
                    double d1 = (double)((float)blockpos.getY() + this.worldObj.rand.nextFloat());
                    double d2 = (double)((float)blockpos.getZ() + this.worldObj.rand.nextFloat());
                    double d3 = d0 - this.explosionX;
                    double d4 = d1 - this.explosionY;
                    double d5 = d2 - this.explosionZ;
                    double d6 = (double)MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
                    d3 = d3 / d6;
                    d4 = d4 / d6;
                    d5 = d5 / d6;
                    double d7 = 0.5D / (d6 / (double)this.explosionSize + 0.1D);
                    d7 = d7 * (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F);
                    d3 = d3 * d7;
                    d4 = d4 * d7;
                    d5 = d5 * d7;
                    this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + this.explosionX) / 2.0D, (d1 + this.explosionY) / 2.0D, (d2 + this.explosionZ) / 2.0D, d3, d4, d5, new int[0]);
                    this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5, new int[0]);
                }

                if (iblockstate.getMaterial() != Material.AIR)
                {
                    if (block.canDropFromExplosion(this))
                    {
                        block.dropBlockAsItemWithChance(this.worldObj, blockpos, this.worldObj.getBlockState(blockpos), 1.0F / this.explosionSize, 0);
                    }

                    block.onBlockExploded(this.worldObj, blockpos, this);
                }
            }
        }*/
	}

	@Override
	public void reset(EntityBombomb bombomb)
	{
	}

	@Override
	public void writeToNBT(EntityBombomb bombomb, NBTTagCompound nbt) 
	{
		nbt.setInteger("attackMethod", attackMethodInt);
		nbt.setInteger("attackTrigger", attackTriggerInt);
		nbt.setInteger("attackTarget", attackTargetInt);
	}

	@Override
	public void readFromNBT(EntityBombomb bombomb, NBTTagCompound nbt) 
	{
		setMethodBehavior(nbt.getInteger("attackMethod"));
		setTriggerBehavior(nbt.getInteger("attackTrigger"));
		setTargetBehavior(nbt.getInteger("attackTarget"));
	}

	public EnumCombatBehaviors getMethodBehavior()
	{
		return EnumCombatBehaviors.getById(attackMethodInt);
	}
	
	public int getNextMethodBehavior(){
		int current = getMethodBehavior().getNumericId();
		if(current < EnumCombatBehaviors.METHOD_DO_NOT_FIGHT.getNumericId()){
			current++;
		}else{
			current = EnumCombatBehaviors.METHOD_MELEE_AND_RANGED.getNumericId();
		}
		return current;
	}
	
	public int getPrevMethodBehavior(){
		int current = getMethodBehavior().getNumericId();
		if(current > EnumCombatBehaviors.METHOD_MELEE_AND_RANGED.getNumericId()){
			current--;
		}else{
			current = EnumCombatBehaviors.METHOD_DO_NOT_FIGHT.getNumericId();
		}
		return current;
	}

	public EnumCombatBehaviors getTriggerBehavior()
	{
		return EnumCombatBehaviors.getById(attackTriggerInt);
	}
	
	public int getNextTriggerBehavior(){
		int current = getTriggerBehavior().getNumericId();
		if(current < EnumCombatBehaviors.TRIGGER_PLAYER_DEAL_DAMAGE.getNumericId()){
			current++;
		}else{
			current = EnumCombatBehaviors.TRIGGER_ALWAYS.getNumericId();
		}
		return current;
	}
	
	public int getPrevTriggerBehavior(){
		int current = getTriggerBehavior().getNumericId();
		if(current > EnumCombatBehaviors.TRIGGER_ALWAYS.getNumericId()){
			current--;
		}else{
			current = EnumCombatBehaviors.TRIGGER_PLAYER_DEAL_DAMAGE.getNumericId();
		}
		return current;
	}

	public EnumCombatBehaviors getTargetBehavior()
	{
		return EnumCombatBehaviors.getById(attackTargetInt);
	}
	
	public int getNextTargetBehavior(){
		int current = getTargetBehavior().getNumericId();
		if(current < EnumCombatBehaviors.TARGET_PASSIVE_OR_HOSTILE_MOBS.getNumericId()){
			current++;
		}else{
			current = EnumCombatBehaviors.TARGET_PASSIVE_MOBS.getNumericId();
		}
		return current;
	}
	
	public int getPrevTargetBehavior(){
		int current = getTargetBehavior().getNumericId();
		if(current > EnumCombatBehaviors.TARGET_PASSIVE_MOBS.getNumericId()){
			current--;
		}else{
			current = EnumCombatBehaviors.TARGET_PASSIVE_OR_HOSTILE_MOBS.getNumericId();
		}
		return current;
	}

	public void setMethodBehavior(int value)
	{
		this.attackMethodInt = (value);
	}

	public void setTriggerBehavior(int value)
	{
		this.attackTriggerInt = (value);
	}

	public void setTargetBehavior(int value)
	{
		this.attackTargetInt = (value);
	}

	private void findAttackTarget(EntityBombomb bombomb)
	{
		List<Entity> entitiesAroundMe = EntityUtil.getAllEntitiesWithinDistanceOfCoordinates(bombomb.worldObj, bombomb.posX, bombomb.posY, bombomb.posZ, 10);
		double distance = 100.0D;
		EntityLivingBase target = null;

		for (Entity entity : entitiesAroundMe)
		{
			if (entity instanceof EntityLivingBase)
			{
				EntityLivingBase livingBase = (EntityLivingBase)entity;
				double distanceTo = EntityUtil.getDistanceToEntity(bombomb, livingBase);

				if (isEntityValidToAttack(bombomb, livingBase) && distanceTo < distance && bombomb.getEntitySenses().canSee(livingBase))
				{
					distance = EntityUtil.getDistanceToEntity(bombomb, livingBase);
					target = livingBase;
				}
			}
		}

		attackTarget = target;
	}

	private void moveToAttackTarget(EntityBombomb bombomb)
	{
		final EntityLiving entityPathController = bombomb;
		float speed = MinionConstants.SPEED_WALK*2;
		
		double dis = EntityUtil.getDistanceToEntity(bombomb, attackTarget);
		
		if(dis >= 5){
			speed = MinionConstants.SPEED_RUN*2;
		}
				
		if(bombomb.getNavigator().noPath())
		entityPathController.getNavigator().tryMoveToEntityLiving(attackTarget, speed);
	}

	public boolean isEntityValidToAttack(EntityBombomb bombomb, EntityLivingBase entity)
	{
		if(entity == bombomb || entity == bombomb.getRidingEntity() || !EntityAITarget.isSuitableTarget(bombomb, entity, false, false) || entity.getClass() == EntityCreeper.class) return false;
		return true;
	}

	public void setAttackTarget(EntityBombomb bombomb, EntityLivingBase entity)
	{
		if (entity != bombomb)
		{
			this.attackTarget = entity;
		}
	}
	
	public EntityLivingBase getAttackTarget()
	{
		return this.attackTarget;
	}
}
