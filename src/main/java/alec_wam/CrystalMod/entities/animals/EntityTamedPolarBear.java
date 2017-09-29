package alec_wam.CrystalMod.entities.animals;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.items.ItemMiscFood.FoodType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityTamedPolarBear extends EntityTameable implements IJumpingMount {

	private static final DataParameter<Boolean> IS_STANDING = EntityDataManager.<Boolean>createKey(EntityTamedPolarBear.class, DataSerializers.BOOLEAN);
    private float clientSideStandAnimation0;
    private float clientSideStandAnimation;
    private int warningSoundTicks;
    protected float jumpPower;
    protected boolean bearJumping;
    private boolean allowStandSliding;

    public EntityTamedPolarBear(World worldIn)
    {
        super(worldIn);
        this.setSize(1.3F, 1.4F);
    }

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return null;
	}
	
	/**
     * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
     * the animal type)
     */
	@Override
	public boolean isBreedingItem(ItemStack stack)
    {
        return false;
    }
	
	private EntityTamedPolarBear.AIMeleeAttack aiAttack = new EntityTamedPolarBear.AIMeleeAttack();
	
	@Override
    protected void initEntityAI()
    {
        super.initEntityAI();
        this.aiSit = new EntityAISit(this);
        this.tasks.addTask(0, new AISwim(this));
        this.tasks.addTask(1, this.aiSit);
        this.tasks.addTask(4, new AIMeleeAttack());
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new AIDefendOwner(this));
        this.targetTasks.addTask(2, new AIHelpOwnerAttack(this));
        this.targetTasks.addTask(3, new AIDefendSelf(this, true, new Class[0]));
    }

	@Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return this.isChild() ? SoundEvents.ENTITY_POLAR_BEAR_BABY_AMBIENT : SoundEvents.ENTITY_POLAR_BEAR_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound()
    {
        return SoundEvents.ENTITY_POLAR_BEAR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_POLAR_BEAR_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        this.playSound(SoundEvents.ENTITY_POLAR_BEAR_STEP, 0.15F, 1.0F);
    }

    protected void playWarningSound()
    {
        if (this.warningSoundTicks <= 0)
        {
            this.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, 1.0F, 1.0F);
            this.warningSoundTicks = 40;
        }
    }

    @Override
    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTableList.ENTITIES_POLAR_BEAR;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(IS_STANDING, Boolean.valueOf(false));
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate()
    {
        super.onUpdate();

        /*if(this.isBeingRidden()){
        	if(this.stepHeight < 1.05F){
        		this.stepHeight = 1.05F;
        	}
        } else {*/
        	if(this.stepHeight > 0.6F){
                this.stepHeight = 0.6F;
        	}
        //}
        
        if (this.world.isRemote)
        {
            this.clientSideStandAnimation0 = this.clientSideStandAnimation;

            if (this.isStanding())
            {
                this.clientSideStandAnimation = MathHelper.clamp(this.clientSideStandAnimation + 1.0F, 0.0F, 6.0F);
            }
            else
            {
                this.clientSideStandAnimation = MathHelper.clamp(this.clientSideStandAnimation - 1.0F, 0.0F, 6.0F);
            }
        }

        if (this.warningSoundTicks > 0)
        {
            --this.warningSoundTicks;
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn)
    {
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));

        if (flag)
        {
            this.applyEnchantments(this, entityIn);
        }

        return flag;
    }

    public boolean isStanding()
    {
        return ((Boolean)this.dataManager.get(IS_STANDING)).booleanValue();
    }

    public void setStanding(boolean standing)
    {
        this.dataManager.set(IS_STANDING, Boolean.valueOf(standing));
    }

    @SideOnly(Side.CLIENT)
    public float getStandingAnimationScale(float p_189795_1_)
    {
        return (this.clientSideStandAnimation0 + (this.clientSideStandAnimation - this.clientSideStandAnimation0) * p_189795_1_) / 6.0F;
    }

    @Override
    protected float getWaterSlowDown()
    {
        return isBeingRidden() && !onGround ? 0.8f : 0.98F;
    }

    /**
     * Called only once on an entity when first time spawned, via egg, mob spawner, natural spawning etc, but not called
     * when entity is reloaded from nbt. Mainly used for initializing attributes and inventory
     */
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
    {
        return (IEntityLivingData)livingdata;
    }
    
    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
        ItemStack itemstack = player.getHeldItem(hand);

        if (this.isTamed())
        {
        	if (this.isOwner(player) && ItemStackTools.isValid(itemstack))
            {
        		if(itemstack.getItem() == Items.SADDLE){
	        		this.mountTo(player);
	        		return true;
        		}
        		if(itemstack.getItem() == Items.FISH && this.getHealth() < this.getMaxHealth()){
        			if(!player.capabilities.isCreativeMode){
        				itemstack.shrink(1);
        			}
        			this.heal(((ItemFood)itemstack.getItem()).getHealAmount(itemstack));
                    this.playTameEffect(true);
        			return true;
        		}
            }
        	if (this.isOwner(player) && !this.world.isRemote && !this.isBreedingItem(itemstack))
            {
                this.aiSit.setSitting(!this.isSitting());
                this.isJumping = false;
                this.navigator.clearPathEntity();
                this.setAttackTarget((EntityLivingBase)null);
                return true;
            }
        }
        else if (itemstack.getItem() == ModItems.miscFood && itemstack.getMetadata() == FoodType.WHITE_FISH_RAW.getMetadata())
        {
            if (!player.capabilities.isCreativeMode)
            {
                itemstack.shrink(1);
            }

            if (!this.world.isRemote)
            {
                if (this.rand.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player))
                {
                    this.setTamed(true);
                    this.navigator.clearPathEntity();
                    this.setAttackTarget((EntityLivingBase)null);
                    this.aiSit.setSitting(true);
                    this.setOwnerId(player.getUniqueID());
                    this.playTameEffect(true);
                    this.world.setEntityState(this, (byte)7);
                }
                else
                {
                    this.playTameEffect(false);
                    this.world.setEntityState(this, (byte)6);
                }
            }

            return true;
        }

        return super.processInteract(player, hand);
    }

    //Riding
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        Entity entity = source.getEntity();
        return this.isBeingRidden() && entity != null && this.isRidingOrBeingRiddenBy(entity) ? false : super.attackEntityFrom(source, amount);
    }
    
    @Override
    public boolean canBePushed()
    {
        return !this.isBeingRidden();
    }
    
    @Override
    public void fall(float distance, float damageMultiplier)
    {
        int i = MathHelper.ceil((distance * 0.5F - 3.0F) * damageMultiplier);

        if (i > 0)
        {
            this.attackEntityFrom(DamageSource.FALL, (float)i);

            if (this.isBeingRidden())
            {
                for (Entity entity : this.getRecursivePassengers())
                {
                    entity.attackEntityFrom(DamageSource.FALL, (float)i);
                }
            }

            IBlockState iblockstate = this.world.getBlockState(new BlockPos(this.posX, this.posY - 0.2D - (double)this.prevRotationYaw, this.posZ));
            Block block = iblockstate.getBlock();

            if (iblockstate.getMaterial() != Material.AIR && !this.isSilent())
            {
                SoundType soundtype = block.getSoundType();
                this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, soundtype.getStepSound(), this.getSoundCategory(), soundtype.getVolume() * 0.5F, soundtype.getPitch() * 0.75F);
            }
        }
    }
    
    protected void mountTo(EntityPlayer player)
    {
        player.rotationYaw = this.rotationYaw;
        player.rotationPitch = this.rotationPitch;
        this.setStanding(false);

        if (!this.world.isRemote)
        {
            player.startRiding(this);
        }
    }
    
    @Override
    protected boolean isMovementBlocked()
    {
        return super.isMovementBlocked() && this.isBeingRidden();
    }
    
    @Override
    public void moveEntityWithHeading(float strafe, float forward)
    {
        if (this.isBeingRidden() && this.canBeSteered())
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)this.getControllingPassenger();
            this.rotationYaw = entitylivingbase.rotationYaw;
            this.prevRotationYaw = this.rotationYaw;
            this.rotationPitch = entitylivingbase.rotationPitch * 0.5F;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.renderYawOffset = this.rotationYaw;
            this.rotationYawHead = this.renderYawOffset;
            strafe = entitylivingbase.moveStrafing * 0.5F;
            forward = entitylivingbase.moveForward;

            if (forward <= 0.0F)
            {
                forward *= 0.25F;
            }

            /*if (this.onGround && this.isRearing())
            {
                strafe = 0.0F;
                forward = 0.0F;
            }*/

            if (this.jumpPower > 0.0F && !bearJumping && this.onGround)
            {
                this.motionY = 0.55 * (double)this.jumpPower;

                if (this.isPotionActive(MobEffects.JUMP_BOOST))
                {
                    this.motionY += (double)((float)(this.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
                }

                this.bearJumping = true;
                this.isAirBorne = true;

                if (forward > 0.0F)
                {
                    float f = MathHelper.sin(this.rotationYaw * 0.017453292F);
                    float f1 = MathHelper.cos(this.rotationYaw * 0.017453292F);
                    this.motionX += (double)(-0.4F * f * this.jumpPower);
                    this.motionZ += (double)(0.4F * f1 * this.jumpPower);
                    this.playSound(SoundEvents.ENTITY_HORSE_JUMP, 0.4F, 1.0F);
                }

                this.jumpPower = 0.0F;
            }

            this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;

            if (this.canPassengerSteer())
            {
            	float slowdown = 1.8F;
            	boolean water = this.isInsideOfMaterial(Material.WATER);
                if(water){
                	motionY+=this.getJumpUpwardsMotion()/2;
                }
                this.setAIMoveSpeed((float)this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() / slowdown);
                
                super.moveEntityWithHeading(strafe, forward);
            }
            else if (entitylivingbase instanceof EntityPlayer)
            {
                this.motionX = 0.0D;
                this.motionY = 0.0D;
                this.motionZ = 0.0D;
            }

            if (this.onGround)
            {
                this.jumpPower = 0.0F;
                this.bearJumping = false;        
            }

            this.prevLimbSwingAmount = this.limbSwingAmount;
            double d1 = this.posX - this.prevPosX;
            double d0 = this.posZ - this.prevPosZ;
            float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 0.5F;

            if (f2 > 1.0F)
            {
                f2 = 1.0F;
            }

            this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
            this.limbSwing += this.limbSwingAmount;
        }
        else
        {
            this.jumpMovementFactor = 0.02F;
            super.moveEntityWithHeading(strafe, forward);
        }
    }
    
    @Override
    public boolean canBeSteered()
    {
        return this.getControllingPassenger() instanceof EntityLivingBase;
    }
    
    @Override
    public boolean isOnLadder()
    {
        return false;
    }
    
    @Override
    @Nullable
    public Entity getControllingPassenger()
    {
        return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
    }

	@Override
	@SideOnly(Side.CLIENT)
    public void setJumpPower(int jumpPowerIn) {
		if (jumpPowerIn < 0)
        {
            jumpPowerIn = 0;
        }
        else
        {
            //this.allowStandSliding = true;
            //this.makeHorseRear();
        }

        if (jumpPowerIn >= 90)
        {
            this.jumpPower = 1.0F;
        }
        else
        {
            this.jumpPower = 0.4F + 0.4F * (float)jumpPowerIn / 90.0F;
        }
	}

	@Override
	public boolean canJump() {
		return true;
	}

	@Override
	public void handleStartJump(int p_184775_1_) {
		
	}

	@Override
	public void handleStopJump() {}
    
	@Override
	public boolean shouldDismountInWater(Entity rider)
    {
		return super.shouldDismountInWater(rider);
    }
	
	public class AISwim extends EntityAISwimming {

		public AISwim(EntityTamedPolarBear entitylivingIn) {
			super(entitylivingIn);
		}
		
		@Override
		public void updateTask()
	    {
			if(EntityTamedPolarBear.this.isBeingRidden()){
				EntityTamedPolarBear.this.motionY = 0.0;
			} else {
				super.updateTask();
			}
	    }
	}
	
    public class AIMeleeAttack extends EntityAIAttackMelee
    {
        public AIMeleeAttack()
        {
            super(EntityTamedPolarBear.this, 1.25D, true);
        }
        
        @Override
        public boolean shouldExecute()
        {
        	return super.shouldExecute() && !EntityTamedPolarBear.this.isBeingRidden();
        }

        protected void checkAndPerformAttack(EntityLivingBase p_190102_1_, double p_190102_2_)
        {
            double d0 = this.getAttackReachSqr(p_190102_1_);

            if (p_190102_2_ <= d0 && this.attackTick <= 0)
            {
                this.attackTick = 20;
                this.attacker.attackEntityAsMob(p_190102_1_);
                EntityTamedPolarBear.this.setStanding(false);
            }
            else if (p_190102_2_ <= d0 * 2.0D)
            {
                if (this.attackTick <= 0)
                {
                    EntityTamedPolarBear.this.setStanding(false);
                    this.attackTick = 20;
                }

                if (this.attackTick <= 10)
                {
                    EntityTamedPolarBear.this.setStanding(true);
                    EntityTamedPolarBear.this.playWarningSound();
                }
            }
            else
            {
                this.attackTick = 20;
                EntityTamedPolarBear.this.setStanding(false);
            }
        }

        /**
         * Resets the task
         */
        public void resetTask()
        {
            EntityTamedPolarBear.this.setStanding(false);
            super.resetTask();
        }

        protected double getAttackReachSqr(EntityLivingBase attackTarget)
        {
            return (double)(4.0F + attackTarget.width);
        }
    }
    
    public class AIDefendOwner extends EntityAIOwnerHurtByTarget {

		public AIDefendOwner(EntityTameable theDefendingTameableIn) {
			super(theDefendingTameableIn);
		}
    	
		@Override
        public boolean shouldExecute()
        {
        	return super.shouldExecute() && !EntityTamedPolarBear.this.isBeingRidden();
        }
    }
    
    public class AIHelpOwnerAttack extends EntityAIOwnerHurtTarget {

		public AIHelpOwnerAttack(EntityTameable theEntityTameableIn) {
			super(theEntityTameableIn);
		}
		
		@Override
        public boolean shouldExecute()
        {
        	return super.shouldExecute() && !EntityTamedPolarBear.this.isBeingRidden();
        }
    }
    
    public class AIDefendSelf extends EntityAIHurtByTarget {

		public AIDefendSelf(EntityCreature creatureIn, boolean entityCallsForHelpIn, Class<?>[] targetClassesIn) {
			super(creatureIn, entityCallsForHelpIn, targetClassesIn);
		}
		
		@Override
        public boolean shouldExecute()
        {
        	return super.shouldExecute() && !EntityTamedPolarBear.this.isBeingRidden();
        }
    	
    }

}
