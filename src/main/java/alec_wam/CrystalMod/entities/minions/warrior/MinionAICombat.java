package alec_wam.CrystalMod.entities.minions.warrior;

import java.util.List;

import alec_wam.CrystalMod.entities.minions.MinionConstants;
import alec_wam.CrystalMod.entities.minions.ai.MinionAIBase;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.util.EntityUtil;
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
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;

public class MinionAICombat extends MinionAIBase<EntityMinionWarrior>
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
		if (minion.isSitting())
		{
			return;
		}
		
		//Cancel attack targets and stop when we're not supposed to fight.
		if (attackTarget != null && (getMethodBehavior() == EnumCombatBehaviors.METHOD_DO_NOT_FIGHT || !isEntityValidToAttack(minion, attackTarget)))
		{
			attackTarget = null;
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
				
				EntityLivingBase target = owner.getLastAttacker();
				
				if (target !=null && isEntityValidToAttack(minion, target))
				{
					attackTarget = target;
				}
			}
		}
		
		if (attackTarget == null && getTriggerBehavior() == EnumCombatBehaviors.TRIGGER_PLAYER_TAKE_DAMAGE)
		{
			EntityLivingBase owner = minion.getOwner();
			if(owner !=null){
				
				EntityLivingBase target = owner.getAITarget();
				
				if (target !=null && isEntityValidToAttack(minion, target))
				{
					attackTarget = target;
				}
			}
		}
		
		if (attackTarget == null)
		{
			EntityLivingBase target = minion.getAITarget();
				
			if (target !=null && isEntityValidToAttack(minion, target))
			{
				attackTarget = target;
			}
		}
		
		//If we have a target, proceed to attack.
		else if (attackTarget != null)
		{
			double distanceToTarget = EntityUtil.getDistanceToEntity(minion, attackTarget);
			
			//Melee attacks
			if (getMethodBehavior() == EnumCombatBehaviors.METHOD_MELEE_ONLY || 
				(getMethodBehavior() == EnumCombatBehaviors.METHOD_MELEE_AND_RANGED && 
				distanceToTarget < 5.0F))
			{
				moveToAttackTarget(minion);
				ItemStack heldItem = minion.getHeldItemMainhand();
				
				if((heldItem == null || !(heldItem.getItem() instanceof ItemSword)) && (minion.getBackItem() !=null && minion.getBackItem().getItem() instanceof ItemSword)){
					minion.switchItems();
					CrystalModNetwork.sendToAllAround(new PacketEntityMessage(minion, "SWITCHITEMS"), minion);
				}
				
				if (distanceToTarget < 1.5F || (minion.getRidingEntity() !=null && distanceToTarget < 2.5F))
				{
					minion.swingArm(EnumHand.MAIN_HAND);
					
					
					Item.ToolMaterial swordMaterial = null;
					
					//ItemStack copy = null;
					
					if (heldItem != null && heldItem.getItem() instanceof ItemSword)
					{
						ItemSword sword = (ItemSword)heldItem.getItem();
						swordMaterial = Item.ToolMaterial.valueOf(sword.getToolMaterialName());
						//copy = heldItem.copy();
					}
					
					float damage = swordMaterial != null ? 4.0F + swordMaterial.getDamageVsEntity() : 0.5F;
					
					if (attackTarget.canBeAttackedWithItem())
			        {
			            if (!attackTarget.hitByEntity(minion))
			            {
			            	damage+=EnchantmentHelper.getModifierForCreature(heldItem, ((EntityLivingBase)attackTarget).getCreatureAttribute());
			            
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
		                    	EnchantmentHelper.applyThornEnchantments((EntityLivingBase)attackTarget, minion);
		                    	EnchantmentHelper.applyArthropodEnchantments(minion, attackTarget);
		                    	
		                    	if(heldItem !=null){
		                    		heldItem.getItem().hitEntity(heldItem, attackTarget, minion);
		                    		if (heldItem.stackSize <= 0)
		                            {
		                                minion.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null);
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
					
					
					/*if(copy !=null){
						copy.damageItem(1, minion);
						minion.setCurrentItemOrArmor(0, copy);;
					}*/
				}
			}
			
			//Ranged attacks
			else if (getMethodBehavior() == EnumCombatBehaviors.METHOD_RANGED_ONLY ||
					(getMethodBehavior() == EnumCombatBehaviors.METHOD_MELEE_AND_RANGED &&
					distanceToTarget >= 5.0F))
			{
				minion.getLookHelper().setLookPosition(attackTarget.posX, attackTarget.posY + (double)attackTarget.getEyeHeight(), attackTarget.posZ, 10.0F, minion.getVerticalFaceSpeed());
				
				if (rangedAttackTime <= 0)
				{
					ItemStack held = minion.getHeldItem(EnumHand.MAIN_HAND);
					if((held == null || !EntityMinionWarrior.isBow(held)) && (minion.getBackItem() !=null && EntityMinionWarrior.isBow(minion.getBackItem()))){
						minion.switchItems();
						CrystalModNetwork.sendToAllAround(new PacketEntityMessage(minion, "SWITCHITEMS"), minion);
					}
					
					if(held !=null && EntityMinionWarrior.isBow(held)){
						EntityTippedArrow arrow = new EntityTippedArrow(minion.worldObj, minion);
				        double dX = attackTarget.posX - minion.posX;
				        double dY = attackTarget.getEntityBoundingBox().minY + (double)(attackTarget.height / 3.0F) - arrow.posY;
				        double dZ = attackTarget.posZ - minion.posZ;
				        double d3 = (double)MathHelper.sqrt_double(dX * dX + dZ * dZ);
				        
				        arrow.setThrowableHeading(dX, dY + d3 * 0.20000000298023224D, dZ, 1.6F, (float)(14 - minion.worldObj.getDifficulty().getDifficultyId() * 4));
				        arrow.setDamage((double)(5.0F) + minion.getRNG().nextGaussian() * 0.25D + (double)((float)minion.worldObj.getDifficulty().getDifficultyId() * 0.11F));
				        minion.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (minion.getRNG().nextFloat() * 0.4F + 0.8F));
				        minion.worldObj.spawnEntityInWorld(arrow);
						rangedAttackTime = 60;
					}
				}

				else
				{
					rangedAttackTime--;
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
		List<Entity> entitiesAroundMe = EntityUtil.getAllEntitiesWithinDistanceOfCoordinates(minion.worldObj, minion.posX, minion.posY, minion.posZ, 10);
		double distance = 100.0D;
		EntityLivingBase target = null;

		for (Entity entity : entitiesAroundMe)
		{
			if (entity instanceof EntityLivingBase)
			{
				EntityLivingBase livingBase = (EntityLivingBase)entity;
				double distanceTo = EntityUtil.getDistanceToEntity(minion, livingBase);

				if (isEntityValidToAttack(minion, livingBase) && distanceTo < distance && minion.getEntitySenses().canSee(livingBase))
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
		float speed = entityPathController instanceof EntityHorse ? MinionConstants.SPEED_HORSE_RUN :  MinionConstants.SPEED_WALK;
		if (entityPathController instanceof EntityHorse)
		{
			final EntityHorse horse = (EntityHorse) entityPathController;

			//This makes the horse move properly.
			if (horse.isHorseSaddled())
			{
				horse.setHorseSaddled(false);
			}
		}
		
		if(minion.getNavigator().noPath())
		entityPathController.getNavigator().tryMoveToEntityLiving(attackTarget, speed);
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
