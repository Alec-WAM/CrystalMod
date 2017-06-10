package alec_wam.CrystalMod.entities.mob.devil;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.client.sound.ModSounds;
import alec_wam.CrystalMod.entities.ModEntites;
import alec_wam.CrystalMod.entities.mob.angel.EntityAngel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.projectiles.EntityDarkarang;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityDevil extends EntityMob
{
    public EntityDevil(World worldIn)
    {
        super(worldIn);
        this.moveHelper = new EntityDevil.AIMoveControl(this);
        this.setSize(0.8F, 1.6F);
        this.experienceValue = 6;
    }

    /**
     * Tries to move the entity towards the specified location.
     */
    public void move(MoverType type, double x, double y, double z)
    {
        super.move(type, x, y, z);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();
        this.fallDistance = 0;
        this.setNoGravity(true);
    }

    protected void initEntityAI()
    {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(4, new EntityDevil.AIChargeAttack());
        this.tasks.addTask(8, new EntityDevil.AIMoveRandom());
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityDevil.class, 3.0F, 1.0F));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[] {EntityDevil.class}));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<EntityAngel>(this, EntityAngel.class, true));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
    }

    protected void entityInit()
    {
        super.entityInit();
    }

    public static void registerFixesVex(DataFixer fixer)
    {
        EntityLiving.registerFixesMob(fixer, EntityDevil.class);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
    }

    protected SoundEvent getAmbientSound()
    {
        return ModSounds.devil_ambient;
    }

    protected SoundEvent getDeathSound()
    {
        return ModSounds.devil_death;
    }

    protected SoundEvent getHurtSound()
    {
        return ModSounds.devil_hurt;
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return ModEntites.LOOTTABLE_DEVIL;
    }

    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float partialTicks)
    {
        return 15728880;
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness(float partialTicks)
    {
        return 1.0F;
    }

    /**
     * Called only once on an entity when first time spawned, via egg, mob spawner, natural spawning etc, but not called
     * when entity is reloaded from nbt. Mainly used for initializing attributes and inventory
     */
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
    {
        this.setEquipmentBasedOnDifficulty(difficulty);
        this.setEnchantmentBasedOnDifficulty(difficulty);
        return super.onInitialSpawn(difficulty, livingdata);
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty)
    {
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ModItems.darkarang));
        this.setDropChance(EntityEquipmentSlot.MAINHAND, 0.0F);
    }

    class AIChargeAttack extends EntityAIBase
    {
    	private int attackTime;
    	private int seeTime;
    	public AIChargeAttack()
        {
            this.setMutexBits(1);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute()
        {
            return EntityDevil.this.getAttackTarget() != null;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean continueExecuting()
        {
            return EntityDevil.this.getAttackTarget() != null && EntityDevil.this.getAttackTarget().isEntityAlive();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting()
        {
            attackTime = 20;
        }

        /**
         * Resets the task
         */
        public void resetTask()
        {
            this.seeTime = 0;
        }

        /**
         * Updates the task
         */
        public void updateTask()
        {
        	if(attackTime > 0)--this.attackTime;
            
        	EntityLivingBase entitylivingbase = EntityDevil.this.getAttackTarget();
            EntityDevil devil = EntityDevil.this;
            boolean canSee = devil.getEntitySenses().canSee(entitylivingbase);
            double d0 = EntityDevil.this.getDistanceSqToEntity(entitylivingbase);
            
            if(d0 > 8.0D){
            	Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
                EntityDevil.this.moveHelper.setMoveTo(vec3d.xCoord, vec3d.yCoord-(entitylivingbase.getEyeHeight()/2), vec3d.zCoord, 1.0D);
            }
            
            devil.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
            	
            if (this.attackTime <= 0 && canSee)
            {
            	attackTime = 40 + devil.rand.nextInt(10);
            	EntityDarkarang darkarang = new EntityDarkarang(world);
            	darkarang.shootingEntity = devil;
            	darkarang.setLocationAndAngles(devil.posX, devil.posY + (double) devil.getEyeHeight(), devil.posZ, devil.rotationYaw, devil.rotationPitch);

            	darkarang.setPosition(darkarang.posX, darkarang.posY, darkarang.posZ);
            	darkarang.motionX = -MathHelper.sin(darkarang.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(darkarang.rotationPitch / 180.0F * (float) Math.PI);
            	darkarang.motionZ = +MathHelper.cos(darkarang.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(darkarang.rotationPitch / 180.0F * (float) Math.PI);
            	darkarang.motionY = -MathHelper.sin(darkarang.rotationPitch / 180.0F * (float) Math.PI);
            	darkarang.setThrowableHeading(darkarang.motionX, darkarang.motionY, darkarang.motionZ, 2.0f, 14.0f - (4.0f * world.getDifficulty().getDifficultyId()));
            	devil.getEntityWorld().spawnEntity(darkarang);
            }
        }
    }

    class AIMoveControl extends EntityMoveHelper
    {
        public AIMoveControl(EntityDevil vex)
        {
            super(vex);
        }

        public void onUpdateMoveHelper()
        {
            if (this.action == EntityMoveHelper.Action.MOVE_TO)
            {
                double d0 = this.posX - EntityDevil.this.posX;
                double d1 = this.posY - EntityDevil.this.posY;
                double d2 = this.posZ - EntityDevil.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                d3 = (double)MathHelper.sqrt(d3);

                if (d3 < EntityDevil.this.getEntityBoundingBox().getAverageEdgeLength())
                {
                    this.action = EntityMoveHelper.Action.WAIT;
                    EntityDevil.this.motionX *= 0.5D;
                    EntityDevil.this.motionY *= 0.5D;
                    EntityDevil.this.motionZ *= 0.5D;
                }
                else
                {
                    EntityDevil.this.motionX += d0 / d3 * 0.05D * this.speed;
                    EntityDevil.this.motionY += d1 / d3 * 0.05D * this.speed;
                    EntityDevil.this.motionZ += d2 / d3 * 0.05D * this.speed;

                    /*if (EntityDevil.this.getAttackTarget() == null)
                    {*/
                        EntityDevil.this.rotationYaw = -((float)MathHelper.atan2(EntityDevil.this.motionX, EntityDevil.this.motionZ)) * (180F / (float)Math.PI);
                        EntityDevil.this.renderYawOffset = EntityDevil.this.rotationYaw;
                    /*}
                    else
                    {
                        double d4 = EntityDevil.this.getAttackTarget().posX - EntityDevil.this.posX;
                        double d5 = EntityDevil.this.getAttackTarget().posZ - EntityDevil.this.posZ;
                        EntityDevil.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * (180F / (float)Math.PI);
                        EntityDevil.this.renderYawOffset = EntityDevil.this.rotationYaw;
                    }*/
                }
            }
        }
    }

    class AIMoveRandom extends EntityAIBase
    {
        public AIMoveRandom()
        {
            this.setMutexBits(1);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute()
        {
            return !EntityDevil.this.getMoveHelper().isUpdating() && EntityDevil.this.rand.nextInt(14) == 0;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean continueExecuting()
        {
            return false;
        }

        /**
         * Updates the task
         */
        public void updateTask()
        {
            BlockPos blockpos = new BlockPos(EntityDevil.this);

            for (int i = 0; i < 3; ++i)
            {
                BlockPos blockpos1 = blockpos.add(EntityDevil.this.rand.nextInt(15) - 7, EntityDevil.this.rand.nextInt(5) - 3, EntityDevil.this.rand.nextInt(15) - 7);
                BlockPos blockpos2 = blockpos1.add(0, 1, 0);
                IBlockState state = EntityDevil.this.world.getBlockState(blockpos1);
                IBlockState state2 = EntityDevil.this.world.getBlockState(blockpos2);
                
                if (state.getBlock().isAir(state, world, blockpos1) && state2.getBlock().isAir(state2, world, blockpos2))
                {
                    EntityDevil.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);

                    if (EntityDevil.this.getAttackTarget() == null)
                    {
                        EntityDevil.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }

                    break;
                }
            }
        }
    }
}