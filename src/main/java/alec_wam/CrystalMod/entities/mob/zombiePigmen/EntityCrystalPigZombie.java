package alec_wam.CrystalMod.entities.mob.zombiePigmen;

import java.util.UUID;

import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ItemIngot.IngotType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.ItemCrystalSword;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityCrystalPigZombie extends EntityPigZombie
{
    private static final UUID ATTACK_SPEED_BOOST_MODIFIER_UUID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
    private static final AttributeModifier ATTACK_SPEED_BOOST_MODIFIER = (new AttributeModifier(ATTACK_SPEED_BOOST_MODIFIER_UUID, "Attacking speed boost", 0.05D, 0)).setSaved(false);
    /** Above zero if this PigZombie is Angry. */
    private int angerLevel;
    /** A random delay until this PigZombie next makes a sound. */
    private int randomSoundDelay;
    private UUID angerTargetUUID;

    private static final DataParameter<Byte> COLOR = EntityDataManager.<Byte>createKey(EntityCrystalPigZombie.class, DataSerializers.BYTE);
    
    public EntityCrystalPigZombie(World worldIn)
    {
        super(worldIn);
        this.isImmuneToFire = true;
    }

    public void setRevengeTarget(EntityLivingBase livingBase)
    {
        super.setRevengeTarget(livingBase);

        if (livingBase != null)
        {
            this.angerTargetUUID = livingBase.getUniqueID();
        }
    }
    
    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(COLOR, Byte.valueOf((byte)0));
    }

    protected void applyEntityAI()
    {
        this.targetTasks.addTask(1, new EntityCrystalPigZombie.AIHurtByAggressor(this));
        this.targetTasks.addTask(2, new EntityCrystalPigZombie.AITargetAggressor(this));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();
    }

    protected void updateAITasks()
    {
        IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

        if (this.isAngry())
        {
            if (!this.isChild() && !iattributeinstance.hasModifier(ATTACK_SPEED_BOOST_MODIFIER))
            {
                iattributeinstance.applyModifier(ATTACK_SPEED_BOOST_MODIFIER);
            }

            --this.angerLevel;
        }
        else if (iattributeinstance.hasModifier(ATTACK_SPEED_BOOST_MODIFIER))
        {
            iattributeinstance.removeModifier(ATTACK_SPEED_BOOST_MODIFIER);
        }

        if (this.randomSoundDelay > 0 && --this.randomSoundDelay == 0)
        {
            this.playSound(SoundEvents.ENTITY_ZOMBIE_PIG_ANGRY, this.getSoundVolume() * 2.0F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 1.8F);
        }

        if (this.angerLevel > 0 && this.angerTargetUUID != null && this.getAITarget() == null)
        {
            EntityPlayer entityplayer = this.getEntityWorld().getPlayerEntityByUUID(this.angerTargetUUID);
            this.setRevengeTarget(entityplayer);
            this.attackingPlayer = entityplayer;
            this.recentlyHit = this.getRevengeTimer();
        }

        super.updateAITasks();
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        return this.getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL;
    }
    
    /**
     * Checks that the entity is not colliding with any blocks / liquids
     */
    public boolean isNotColliding()
    {
        return this.getEntityWorld().checkNoEntityCollision(this.getEntityBoundingBox(), this) && this.getEntityWorld().getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty() && !this.getEntityWorld().containsAnyLiquid(this.getEntityBoundingBox());
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setByte("Color", (byte)this.getColor());
        tagCompound.setShort("Anger", (short)this.angerLevel);

        if (this.angerTargetUUID != null)
        {
            tagCompound.setString("HurtBy", this.angerTargetUUID.toString());
        }
        else
        {
            tagCompound.setString("HurtBy", "");
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        super.readEntityFromNBT(tagCompund);
        this.angerLevel = tagCompund.getShort("Anger");
        if (tagCompund.hasKey("Color", 99))
        {
            int i = tagCompund.getByte("Color");
            this.setColor(i);
        }
        String s = tagCompund.getString("HurtBy");

        if (s.length() > 0)
        {
            this.angerTargetUUID = UUID.fromString(s);
            EntityPlayer entityplayer = this.getEntityWorld().getPlayerEntityByUUID(this.angerTargetUUID);
            this.setRevengeTarget(entityplayer);

            if (entityplayer != null)
            {
                this.attackingPlayer = entityplayer;
                this.recentlyHit = this.getRevengeTimer();
            }
        }
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else
        {
            Entity entity = source.getEntity();

            if (entity instanceof EntityPlayer)
            {
                this.becomeAngryAt(entity);
            }

            return super.attackEntityFrom(source, amount);
        }
    }

    /**
     * Causes this PigZombie to become angry at the supplied Entity (which will be a player).
     */
    private void becomeAngryAt(Entity p_70835_1_)
    {
        this.angerLevel = 400 + this.rand.nextInt(400);
        this.randomSoundDelay = this.rand.nextInt(40);

        if (p_70835_1_ instanceof EntityLivingBase)
        {
            this.setRevengeTarget((EntityLivingBase)p_70835_1_);
        }
    }

    public boolean isAngry()
    {
        return this.angerLevel > 0;
    }

    /**
     * Drop 0-2 items of this living's type
     *  
     * @param wasRecentlyHit true if this this entity was recently hit by appropriate entity (generally only if player
     * or tameable)
     */
    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier)
    {
        int i = this.rand.nextInt(2 + lootingModifier);

        for (int j = 0; j < i; ++j)
        {
            this.dropItem(Items.ROTTEN_FLESH, 1);
        }

        i = this.rand.nextInt(2 + lootingModifier);

        for (int k = 0; k < i; ++k)
        {
        	int META = CrystalType.BLUE_SHARD.getMetadata() + getColor();
            this.entityDropItem(new ItemStack(ModItems.crystals, 1, META), 0.0f);
        }
        
        if(wasRecentlyHit){
        	//Taken from ZombiePigman Loottable
        	double chance = 0.025 + (0.01 * lootingModifier);
        	if(rand.nextFloat() < chance){
        		int META = IngotType.BLUE.getMetadata() + getColor();
                this.entityDropItem(new ItemStack(ModItems.ingots, 1, META), 0.0f);
        	}
        }
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
    	return false;
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty)
    {
    	
    }

    /**
     * Called only once on an entity when first time spawned, via egg, mob spawner, natural spawning etc, but not called
     * when entity is reloaded from nbt. Mainly used for initializing attributes and inventory
     */
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
    {
        super.onInitialSpawn(difficulty, livingdata);
        setColor(rand.nextInt(5));
        return livingdata;
    }
    
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_ZOMBIE_PIG_AMBIENT;
    }

    protected SoundEvent getHurtSound()
    {
        return SoundEvents.ENTITY_ZOMBIE_PIG_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_ZOMBIE_PIG_DEATH;
    }
    
    public int getColor()
    {
        return this.dataManager.get(COLOR);
    }
    
    public void setColor(int color){
    	this.dataManager.set(COLOR, Byte.valueOf((byte)color));
    	ItemStack sword = new ItemStack(ModItems.crystalSword);
    	ItemNBTHelper.setString(sword, "Color", ItemCrystalSword.colors[color % (ItemCrystalSword.colors.length)]);
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, sword);
    }

    static class AIHurtByAggressor extends EntityAIHurtByTarget
        {
            public AIHurtByAggressor(EntityCrystalPigZombie p_i45828_1_)
            {
                super(p_i45828_1_, true, new Class[0]);
            }

            protected void setEntityAttackTarget(EntityCreature creatureIn, EntityLivingBase entityLivingBaseIn)
            {
                super.setEntityAttackTarget(creatureIn, entityLivingBaseIn);

                if (creatureIn instanceof EntityCrystalPigZombie)
                {
                    ((EntityCrystalPigZombie)creatureIn).becomeAngryAt(entityLivingBaseIn);
                }
            }
        }

    static class AITargetAggressor extends EntityAINearestAttackableTarget<EntityPlayer>
        {
            public AITargetAggressor(EntityCrystalPigZombie p_i45829_1_)
            {
                super(p_i45829_1_, EntityPlayer.class, true);
            }

            /**
             * Returns whether the EntityAIBase should begin execution.
             */
            public boolean shouldExecute()
            {
                return ((EntityCrystalPigZombie)this.taskOwner).isAngry() && super.shouldExecute();
            }
        }
}