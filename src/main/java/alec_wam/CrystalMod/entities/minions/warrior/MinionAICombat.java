package alec_wam.CrystalMod.entities.minions.warrior;

import java.util.List;

import alec_wam.CrystalMod.entities.ai.AIBase;
import alec_wam.CrystalMod.entities.minions.EnumMovementState;
import alec_wam.CrystalMod.entities.minions.MinionConstants;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class MinionAICombat extends AIBase<EntityMinionWarrior>
{
	private int attackMethodInt;
	private int attackTriggerInt;
	private int attackTargetInt;

	private EntityLivingBase attackTarget;
	private int rangedAttackTime;
	
	public MinionAICombat() 
	{
		attackMethodInt = EnumCombatBehaviors.METHOD_DO_NOT_FIGHT.getNumericId();
		attackTriggerInt = EnumCombatBehaviors.TRIGGER_PLAYER_TAKE_DAMAGE.getNumericId();
		attackTargetInt = EnumCombatBehaviors.TARGET_HOSTILE_MOBS.getNumericId();
	}

	@Override
	public void onUpdateCommon(EntityMinionWarrior minion) 
	{
	}

	@Override
	public void onUpdateClient(EntityMinionWarrior minion) 
	{	
	}

	@Override
	public void onUpdateServer(EntityMinionWarrior minion) 
	{
		//Do nothing when we're sitting.
		if (minion.isSitting() || minion.isDead || minion.isEating() || minion.getMovementState() == EnumMovementState.STAY)
		{
			return;
		}
		
		//Cancel attack targets and stop when we're not supposed to fight.
		if (attackTarget != null && (getMethodBehavior() == EnumCombatBehaviors.METHOD_DO_NOT_FIGHT || !isEntityValidToAttack(minion, attackTarget)))
		{
			attackTarget = null;
			return;
		}
		
		if(getMethodBehavior() == EnumCombatBehaviors.METHOD_DO_NOT_FIGHT){
			return;
		}

		//Also clear our attack target if it is dead.
		if (attackTarget != null && (attackTarget.isDead || attackTarget.getHealth() <= 0.0F || !attackTarget.isEntityAlive()))
		{
			attackTarget = null;
		}
		
		//Check if we should be searching for a target.
		if (attackTarget == null && getTriggerBehavior() == EnumCombatBehaviors.TRIGGER_ALWAYS)
		{
			findAttackTarget(minion);
		}

		if (attackTarget == null && getTriggerBehavior() == EnumCombatBehaviors.TRIGGER_PLAYER_DEAL_DAMAGE)
		{
			EntityLivingBase owner = minion.getOwner();
			if(owner !=null){
				boolean boundsCheck = true;
				if(minion.getMovementState() == EnumMovementState.GUARD){
					if(!minion.isWithinGuardBounds(new BlockPos(owner))){
						boundsCheck = false;
					}
				}	
				EntityLivingBase target = owner.getLastAttacker();
				
				if (target !=null && isEntityValidToAttack(minion, target) && boundsCheck)
				{
					attackTarget = target;
				}
			}
		}
		
		if (attackTarget == null && getTriggerBehavior() == EnumCombatBehaviors.TRIGGER_PLAYER_TAKE_DAMAGE)
		{
			EntityLivingBase owner = minion.getOwner();
			if(owner !=null){
				boolean boundsCheck = true;
				if(minion.getMovementState() == EnumMovementState.GUARD){
					if(!minion.isWithinGuardBounds(new BlockPos(owner))){
						boundsCheck = false;
					}
				}				
				
				EntityLivingBase target = owner.getAITarget();
				
				if (target !=null && isEntityValidToAttack(minion, target) && boundsCheck)
				{
					attackTarget = target;
				}
			}
		}
		
		//If we have a target, proceed to attack.
		else if (attackTarget != null)
		{
			double distanceToTarget = EntityUtil.getDistanceToEntity(minion, attackTarget);
			
			//Melee attacks
			if (getMethodBehavior() == EnumCombatBehaviors.METHOD_MELEE_ONLY || 
				(getMethodBehavior() == EnumCombatBehaviors.METHOD_MELEE_AND_RANGED && 
				distanceToTarget < 2.0F))
			{
				moveToAttackTarget(minion);
				
				ItemStack swordStack = minion.inventory.getStackInSlot(0);
				if(ItemStackTools.isValid(swordStack) && minion.getSlotSelected() !=0){
					minion.setSlotSelected(0);
				}
				
				if (distanceToTarget < 1.5F || (minion.getRidingEntity() !=null && distanceToTarget < 2.5F))
				{
					minion.swingArm(EnumHand.MAIN_HAND);
					
					Item.ToolMaterial swordMaterial = null;
					
					//ItemStack copy = null;
					
					if (ToolUtil.isSword(swordStack))
					{
						ItemSword sword = (ItemSword)swordStack.getItem();
						swordMaterial = Item.ToolMaterial.valueOf(sword.getToolMaterialName());
						//copy = heldItem.copy();
					}
					
					float damage = swordMaterial != null ? 4.0F + swordMaterial.getDamageVsEntity() : 0.5F;
					
					if (attackTarget.canBeAttackedWithItem())
			        {
			            if (!attackTarget.hitByEntity(minion))
			            {
			            	damage+=EnchantmentHelper.getModifierForCreature(swordStack, attackTarget.getCreatureAttribute());
			            
			            	int j = EnchantmentHelper.getFireAspectModifier(minion);

			            	boolean flag1 = false;
			            	
		                    if (j > 0 && !attackTarget.isBurning())
		                    {
		                        flag1 = true;
		                        attackTarget.setFire(1);
		                    }
		                    
		                    boolean flag2 = attackTarget.attackEntityFrom(DamageSource.causeMobDamage(minion), damage);
		                    if(flag2){
		                    	minion.attackEntityAsMob(attackTarget);
		                    	EnchantmentHelper.applyThornEnchantments(attackTarget, minion);
		                    	EnchantmentHelper.applyArthropodEnchantments(minion, attackTarget);
		                    	
		                    	if(ItemStackTools.isValid(swordStack)){
		                    		swordStack.getItem().hitEntity(swordStack, attackTarget, minion);
		                    		if (ItemStackTools.isEmpty(swordStack))
		                            {
		                                minion.inventory.setInventorySlotContents(0, ItemStackTools.getEmptyStack());
		                            }
		                    	}
		                    	
		                    	if (j > 0)
	                            {
		                    		attackTarget.setFire(j * 4);
	                            }
		                    }else if(flag1){
		                    	attackTarget.extinguish();
		                    }
			            }
			        }
				}
			}
			
			//Ranged attacks
			else if (getMethodBehavior() == EnumCombatBehaviors.METHOD_RANGED_ONLY ||
					(getMethodBehavior() == EnumCombatBehaviors.METHOD_MELEE_AND_RANGED &&
					distanceToTarget >= 3.0F))
			{
				minion.getLookHelper().setLookPosition(attackTarget.posX, attackTarget.posY + attackTarget.getEyeHeight(), attackTarget.posZ, 10.0F, minion.getVerticalFaceSpeed());
				
				if(getMethodBehavior() == EnumCombatBehaviors.METHOD_RANGED_ONLY){
					if(distanceToTarget > 4.0F){
						moveToAttackTarget(minion);
					} else {
						if(!minion.getNavigator().noPath()){
							minion.getNavigator().clearPathEntity();
						}
					}
				}
				
				if(getMethodBehavior() == EnumCombatBehaviors.METHOD_MELEE_AND_RANGED){
					if(distanceToTarget > 4.0F){
						moveToAttackTarget(minion);
					} 
				}
				
				if (rangedAttackTime <= 0)
				{
					ItemStack bowStack = minion.inventory.getStackInSlot(1);
					if(ItemStackTools.isValid(bowStack) && minion.getSlotSelected() !=1){
						minion.setSlotSelected(1);
					}
					
					if(ItemStackTools.isValid(bowStack) && EntityMinionWarrior.isBow(bowStack)){
						//TODO Damage Bow
						EntityTippedArrow arrow = new EntityTippedArrow(minion.getEntityWorld(), minion);
				        double dX = attackTarget.posX - minion.posX;
				        double dY = attackTarget.getEntityBoundingBox().minY + attackTarget.height / 3.0F - arrow.posY;
				        double dZ = attackTarget.posZ - minion.posZ;
				        double d3 = MathHelper.sqrt(dX * dX + dZ * dZ);
				        
				        arrow.setThrowableHeading(dX, dY + d3 * 0.20000000298023224D, dZ, 1.6F, 14 - minion.getEntityWorld().getDifficulty().getDifficultyId() * 4);
				        
				        double damage = (5.0F) + minion.getRNG().nextGaussian() * 0.25D + minion.getEntityWorld().getDifficulty().getDifficultyId() * 0.11F;
				        int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, bowStack);
                        if (power > 0)
                        {
                        	damage += (double)power * 0.5D + 0.5D;
                        }
				        arrow.setDamage(damage);
                        
				        int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, bowStack);
				        if (punch > 0)
                        {
				        	arrow.setKnockbackStrength(punch);
                        }

                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, bowStack) > 0)
                        {
                        	arrow.setFire(100);
                        }
				        
				        minion.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F / (minion.getRNG().nextFloat() * 0.4F + 0.8F));
				        minion.getEntityWorld().spawnEntity(arrow);
				        bowStack.damageItem(1, minion);
				        if (ItemStackTools.isEmpty(bowStack))
                        {
                            minion.inventory.setInventorySlotContents(1, ItemStackTools.getEmptyStack());
                        }
						rangedAttackTime = 50;
					}
				}

				else
				{
					rangedAttackTime--;
				}
			}
		} else {
			if(minion.getMovementState() == EnumMovementState.GUARD){
				BlockPos home = minion.getGuardPos();
				if(home !=null && home !=BlockPos.ORIGIN){
					minion.getNavigator().tryMoveToXYZ(home.getX() + 0.5, home.getY() + 0.5, home.getZ() + 0.5, MinionConstants.SPEED_WALK);
				}
			}
		}
	}

	@Override
	public void reset(EntityMinionWarrior minion)
	{
	}

	@Override
	public void writeToNBT(EntityMinionWarrior minion, NBTTagCompound nbt) 
	{
		nbt.setInteger("attackMethod", attackMethodInt);
		nbt.setInteger("attackTrigger", attackTriggerInt);
		nbt.setInteger("attackTarget", attackTargetInt);
	}

	@Override
	public void readFromNBT(EntityMinionWarrior minion, NBTTagCompound nbt) 
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

	private void findAttackTarget(EntityMinionWarrior minion)
	{
		List<Entity> entitiesAroundMe = EntityUtil.getAllEntitiesWithinDistanceOfCoordinates(minion.getEntityWorld(), minion.posX, minion.posY, minion.posZ, 10);
		double distance = 20.0D;
		EntityLivingBase target = null;

		for (Entity entity : entitiesAroundMe)
		{
			if (entity instanceof EntityLivingBase)
			{
				EntityLivingBase livingBase = (EntityLivingBase)entity;
				double distanceTo = EntityUtil.getDistanceToEntity(minion, livingBase);
				boolean boundsCheck = true;
				if(minion.getMovementState() == EnumMovementState.GUARD){
					if(!minion.isWithinGuardBounds(new BlockPos(livingBase))){
						boundsCheck = false;
					}
				}	
				if (isEntityValidToAttack(minion, livingBase) && distanceTo < distance && minion.getEntitySenses().canSee(livingBase) && boundsCheck)
				{
					distance = EntityUtil.getDistanceToEntity(minion, livingBase);
					target = livingBase;
				}
			}
		}

		attackTarget = target;
	}

	private void moveToAttackTarget(EntityMinionWarrior minion)
	{
		final EntityLiving entityPathController = (EntityLiving) (minion.getRidingEntity() instanceof EntityHorse ? minion.getRidingEntity() : minion);
		double distance = EntityUtil.getDistanceToEntity(minion, attackTarget);
		float speed = entityPathController instanceof EntityHorse ? MinionConstants.SPEED_HORSE_RUN : distance > 5.0 ? MinionConstants.SPEED_WALK * 1.2f : MinionConstants.SPEED_WALK;
		if (entityPathController instanceof EntityHorse)
		{
			final EntityHorse horse = (EntityHorse) entityPathController;

			//This makes the horse move properly.
			if (horse.isHorseSaddled())
			{
				horse.setHorseSaddled(false);
			}
		}
		
		if(minion.getNavigator().noPath()){
			entityPathController.getNavigator().tryMoveToEntityLiving(attackTarget, speed);
		}
	}

	public boolean isEntityValidToAttack(EntityMinionWarrior minion, EntityLivingBase entity)
	{
		if(entity == minion || entity == minion.getRidingEntity() || !EntityAITarget.isSuitableTarget(minion, entity, false, false) || entity.getClass() == EntityCreeper.class) return false;
		
		if (entity instanceof EntityMob &&
				(getTargetBehavior() == EnumCombatBehaviors.TARGET_HOSTILE_MOBS || 
				getTargetBehavior() == EnumCombatBehaviors.TARGET_PASSIVE_OR_HOSTILE_MOBS))
		{
			return true;
		}

		else if (entity instanceof EntityAnimal &&
				(getTargetBehavior() == EnumCombatBehaviors.TARGET_PASSIVE_MOBS || 
				getTargetBehavior() == EnumCombatBehaviors.TARGET_PASSIVE_OR_HOSTILE_MOBS))
		{
			return true;
		}

		else
		{
			return false;
		}
	}

	public void setAttackTarget(EntityMinionWarrior minion, EntityLivingBase entity)
	{
		if (entity != minion)
		{
			this.attackTarget = entity;
		}
	}
	
	public EntityLivingBase getAttackTarget()
	{
		return this.attackTarget;
	}
}
